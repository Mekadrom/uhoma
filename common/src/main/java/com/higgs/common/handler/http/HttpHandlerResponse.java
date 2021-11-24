package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HttpHandlerResponse extends HandlerResponse {
    public HttpHandlerResponse(final HttpHandlerRequest requestFor) {
        super(requestFor);
    }

    private int responseCode;
    private Map<String, List<String>> headers;
    private String body;
}
