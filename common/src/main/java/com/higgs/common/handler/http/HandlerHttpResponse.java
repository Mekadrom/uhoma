package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class HandlerHttpResponse extends HandlerResponse {
    public HandlerHttpResponse(final HandlerHttpRequest requestFor) {
        super(requestFor);
    }

    private int responseCode;
    private Map<String, List<String>> headers;
    private String body;
}
