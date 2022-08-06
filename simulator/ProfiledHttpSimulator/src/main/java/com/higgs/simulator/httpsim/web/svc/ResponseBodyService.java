package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import com.higgs.simulator.httpsim.db.repo.ResponseBodyRepository;
import com.higgs.simulator.httpsim.util.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ResponseBodyService {
    private static final Set<String> BLACKLISTED_HEADERS = Set.of("user-agent", "accept", "postman-token", "connection", "accept-encoding", "content-length", "host");

    private final ResponseBodyRepository responseBodyRepository;

    public ResponseBody findByResponseGroupAndKeyedFieldsOrDefault(final Profile profile, final ResponseGroup responseGroup, final Map<String, Object> keyedFields, final Map<String, Object> body, final Map<String, Object> headers, final Integer responseCode) {
        return this.findByResponseGroupAndKeyedFields(responseGroup, keyedFields).orElseGet(() -> this.createResponseBody(profile, responseGroup, keyedFields, body, headers, responseCode));
    }

    public Optional<ResponseBody> findByResponseGroupAndKeyedFields(final ResponseGroup responseGroup, final Map<String, Object> keyedFields) {
        return this.determineResponseBody(this.responseBodyRepository.findByResponseGroupSeq(responseGroup.getResponseGroupSeq()), keyedFields);
    }

    ResponseBody createResponseBody(final Profile profile, final ResponseGroup responseGroup, final Map<String, Object> keyedFieldValues, final Map<String, Object> body, final Map<String, Object> headers, final Integer responseCode) {
        return this.responseBodyRepository.save(new ResponseBody()
                .setResponseGroupSeq(responseGroup.getResponseGroupSeq())
                .setKeyedFieldValues(keyedFieldValues)
                .setHeaders(this.filterHeaders(headers))
                .setResponseCode(responseCode)
                .setBody(this.getCleanBody(body, profile.getKeyedFields())));
    }

    public void updateResponseBody(final Profile profile, final ResponseBody responseBody, final Integer responseCode, final Map<String, Object> headers, final Map<String, Object> requestFields) {
        this.responseBodyRepository.save(responseBody
                .setBody(this.getCleanBody(requestFields, profile.getKeyedFields()))
                .setResponseCode(responseCode)
                .setHeaders(this.filterHeaders(headers)));
    }

    String getCleanBody(final Map<String, Object> body, final Set<String> keyedFields) {
        return Optional.ofNullable(JsonUtils.convertMapToJsonString(this.removeKeyedFields(body, keyedFields)))
                .map(it -> it.replaceAll("\\s*", ""))
                .orElse("");
    }

    Map<String, Object> removeKeyedFields(final Map<String, Object> body, final Set<String> keyedFields) {
        return body.entrySet().stream()
                .filter(it -> !keyedFields.contains(it.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    Map<String, Object> filterHeaders(final Map<String, Object> headers) {
        return headers.entrySet().stream()
                .filter(it -> !ResponseBodyService.BLACKLISTED_HEADERS.contains(it.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    Optional<ResponseBody> determineResponseBody(final List<ResponseBody> responseBodies, final Map<String, Object> keyedFields) {
        for (final ResponseBody responseBody : responseBodies) {
            final Map<String, Object> responseBodyKeyedFields = responseBody.getKeyedFieldValues();
            if (this.keyedFieldsMatch(responseBodyKeyedFields, keyedFields)) {
                return Optional.of(responseBody);
            }
        }
        return Optional.empty();
    }

    boolean keyedFieldsMatch(final Map<String, Object> savedKeyFields, final Map<String, Object> requestKeyedFields) {
        for (final Map.Entry<String, Object> entry : savedKeyFields.entrySet()) {
            final String key = entry.getKey();
            final Object expectedValue = entry.getValue();
            final Object requestValue = requestKeyedFields.get(key);
            if (!Objects.equals(expectedValue, requestValue)) {
                return false;
            }
        }
        return true;
    }
}
