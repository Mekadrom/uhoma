package com.higgs.simulator.httpsim.web.rest;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import com.higgs.simulator.httpsim.util.JsonUtils;
import com.higgs.simulator.httpsim.web.svc.ProfileService;
import com.higgs.simulator.httpsim.web.svc.ResponseBodyService;
import com.higgs.simulator.httpsim.web.svc.ResponseGroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
public class JsonProfileHttpRestController {
    private static final String RESPONSE_CODE = "responseCode";
    private static final String PROFILE_ALREADY_EXISTS = "{\"error\":\"Profile already exists at /%s\"}";
    private static final String BAD_SET_REQUEST_ERROR = "{\"error\":\"Unable to set response body for request %s\"}";
    private static final String RESPONSE_MAPPING_NOT_FOUND = "{\"error\":\"Response body not found at /%s/%s with request %s\"}";
    private static final String PROFILE_MAPPING_NOT_FOUND = "{\"error\":\"Profile not found at /%s\"}";

    private final ProfileService profileService;
    private final ResponseBodyService responseBodyService;
    private final ResponseGroupService responseGroupService;

    @RequestMapping(value = "/get/json/{root}/{endpoint}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getResponse(@PathVariable final String root,
                                              @PathVariable final String endpoint,
                                              @RequestBody final String body) {
        JsonProfileHttpRestController.log.info("Received request to get response for {}/{} \n\twith body: \n\t\t{}", root, endpoint, body);
        final Optional<Profile> profileOpt = this.profileService.findByEndpoint(root);
        if (profileOpt.isPresent()) {
            final Profile profile = profileOpt.get();
            final ResponseGroup responseGroup = this.responseGroupService.findByProfileAndEndpointOrDefault(profile, endpoint);
            final Map<String, Object> requestFields = JsonUtils.convertJsonStringToMappedValues(body);
            final Map<String, Object> keyedFields = this.filterKeyedFields(profile, requestFields);
            final Optional<ResponseBody> responseBodyOpt = this.responseBodyService.findByResponseGroupAndKeyedFields(responseGroup, keyedFields);
            if (responseBodyOpt.isPresent()) {
                final ResponseBody responseBody = responseBodyOpt.get();
                final ResponseEntity.BodyBuilder responseEntityBuilder = ResponseEntity.status(responseBody.getResponseCode());
                responseBody.getHeaders().forEach((key, value) -> responseEntityBuilder.header(key, String.valueOf(value)));
                final String bodyStr = responseBody.getBody();
                JsonProfileHttpRestController.log.info("Returning response: \n\t{}", bodyStr);
                return responseEntityBuilder.body(bodyStr);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(JsonProfileHttpRestController.RESPONSE_MAPPING_NOT_FOUND, root, endpoint, body));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(JsonProfileHttpRestController.PROFILE_MAPPING_NOT_FOUND, root));
    }

    @GetMapping(value = "/get/json/{root}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getProfile(@PathVariable final String root) {
        JsonProfileHttpRestController.log.info("Received request to set response for {}", root);
        final Optional<Profile> profileOpt = this.profileService.findByEndpoint(root);
        if (profileOpt.isPresent()) {
            return ResponseEntity.ok(JsonUtils.serialize(profileOpt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(JsonProfileHttpRestController.PROFILE_MAPPING_NOT_FOUND, root));
    }


    @RequestMapping(value = "/set/json/{root}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = { RequestMethod.POST, RequestMethod.PUT })
    public ResponseEntity<String> putPostProfile(@PathVariable final String root,
                                                 @RequestBody final String body) {
        JsonProfileHttpRestController.log.info("Received request to set response for {} \n\twith body: \n\t\t{}", root, body);
        if (this.profileService.findByEndpoint(root).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format(JsonProfileHttpRestController.PROFILE_ALREADY_EXISTS, root));
        }
        return ResponseEntity.ok(JsonUtils.serialize(
                this.profileService.createNewProfile(root, Optional.ofNullable(JsonUtils.convertJsonStringToMappedValues(body))
                        .map(Map::keySet)
                        .orElseThrow())));
    }

    @RequestMapping(value = "/set/json/{root}/{endpoint}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = { RequestMethod.POST, RequestMethod.PUT })
    public ResponseEntity<String> putPostMapping(@PathVariable final String root,
                                                 @PathVariable final String endpoint,
                                                 @RequestHeader(value = JsonProfileHttpRestController.RESPONSE_CODE) final Integer responseCode,
                                                 @RequestHeader final Map<String, Object> headers,
                                                 @RequestBody final String body) {
        JsonProfileHttpRestController.log.info("Received request to set response for {}/{} \n\twith body: \n\t\t{}", root, endpoint, body);
        return this.putPostMapping(root, endpoint, responseCode, headers,  JsonUtils.convertJsonStringToMappedValues(body));
    }

    ResponseEntity<String> putPostMapping(final String root, final String endpoint, final Integer responseCode, final Map<String, Object> headers, final Map<String, Object> requestFields) {
        if (requestFields != null) {
            final Optional<Profile> profileOpt = this.profileService.findByEndpoint(root);
            if (profileOpt.isPresent()) {
                final Profile profile = profileOpt.get();
                final Map<String, Object> keyedFields = this.filterKeyedFields(profile, requestFields);
                final ResponseGroup responseGroup = this.responseGroupService.findByProfileAndEndpointOrDefault(profile, endpoint);
                final ResponseBody responseBody = this.responseBodyService.findByResponseGroupAndKeyedFieldsOrDefault(profile, responseGroup, keyedFields,  requestFields, headers, responseCode);
                if (responseBody != null) {
                    this.responseBodyService.updateResponseBody(profile, responseBody, responseCode, headers, requestFields);
                    return ResponseEntity.ok(JsonUtils.serialize(responseBody));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format(JsonProfileHttpRestController.BAD_SET_REQUEST_ERROR, JsonUtils.convertMapToJsonString(requestFields)));
    }

    Map<String, Object> filterKeyedFields(final Profile profile, final Map<String, Object> requestFields) {
        return profile.getKeyedFields().stream().collect(Collectors.toMap(key -> key, requestFields::get));
    }
}
