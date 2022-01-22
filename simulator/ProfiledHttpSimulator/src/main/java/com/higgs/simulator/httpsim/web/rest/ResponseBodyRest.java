package com.higgs.simulator.httpsim.web.rest;

import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import com.higgs.simulator.httpsim.web.svc.ResponseBodyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class ResponseBodyRest {
    private static final String RESPONSE_CODE = "responseCode";

    private final ResponseBodyService responseBodyService;

    @RequestMapping(value = "/get/{endpoint}")
    public ResponseEntity<String> get(@PathVariable final String endpoint) {
        final Collection<ResponseBody> responseBodies = this.responseBodyService.getAllResponseBodies();
        final Optional<ResponseBody> responseBodyOpt = responseBodies.stream().filter(it -> endpoint.equals(it.getEndpoint())).findFirst();
        if (responseBodyOpt.isPresent()) {
            final ResponseBody responseBody = responseBodyOpt.get();
            final ResponseEntity<String> response = ResponseEntity.status(responseBody.getResponseCode()).body(responseBody.getBody());
            for (final Map.Entry<String, Object> entry : responseBody.getHeaders().entrySet()) {
                response.getHeaders().add(entry.getKey(), entry.getValue().toString());
            }
            return response;
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/set/{endpoint}")
    public ResponseEntity<ResponseBody> set(@PathVariable final String endpoint,
                                            @RequestHeader final Map<String, Object> headers,
                                            @RequestBody final String body) {
        final ResponseBody responseBody = new ResponseBody();
        responseBody.setEndpoint(endpoint);
        responseBody.setBody(body);
        responseBody.setResponseCode(Integer.parseInt(String.valueOf(headers.remove(ResponseBodyRest.RESPONSE_CODE))));
        responseBody.setHeaders(headers);
        this.responseBodyService.saveResponseBody(responseBody);
        return ResponseEntity.ok(responseBody);
    }
}