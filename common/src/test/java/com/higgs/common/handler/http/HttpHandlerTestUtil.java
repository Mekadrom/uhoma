package com.higgs.common.handler.http;

import org.springframework.http.HttpMethod;

import java.util.Map;

public class HttpHandlerTestUtil {
    public static HttpHandlerRequest getTestRequestObj(final HttpMethod method,
                                                       final String connectType,
                                                       final String url,
                                                       final String port,
                                                       final String endpoint,
                                                       final Map<String, String> queryParams,
                                                       final Map<String, String> headers,
                                                       final String body) {
        final HttpHandlerRequest request = new HttpHandlerRequest();
        request.setHttpMethod(method);
        request.setConnectType(connectType);
        request.setUrl(url);
        request.setPort(port);
        request.setEndpoint(endpoint);
        request.setQueryParams(queryParams);
        request.setHeaders(headers);
        request.setBody(body);
        return request;
    }
}
