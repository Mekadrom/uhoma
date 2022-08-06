package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerRequest;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.io.Serial;
import java.util.Map;

@Getter
public class HttpHandlerRequest extends HandlerRequest {
    @Serial
    private static final long serialVersionUID = 214L;

    public HttpMethod getHttpMethod() {
        return (HttpMethod) this.get(HttpHandler.METHOD_FIELD);
    }

    public void setHttpMethod(final HttpMethod httpMethod) {
        this.put(HttpHandler.METHOD_FIELD, httpMethod);
    }

    public String getConnectType() {
        return (String) this.get(HttpHandler.CONNECT_TYPE_FIELD);
    }

    public void setConnectType(final String connectType) {
        this.put(HttpHandler.CONNECT_TYPE_FIELD, connectType);
    }

    public String getUrl() {
        return (String) this.get(HttpHandler.URL_FIELD);
    }

    public void setUrl(final String url) {
        this.put(HttpHandler.URL_FIELD, url);
    }

    public String getPort() {
        return (String) this.get(HttpHandler.PORT_FIELD);
    }

    public void setPort(final String port) {
        this.put(HttpHandler.PORT_FIELD, port);
    }

    public String getEndpoint() {
        return (String) this.get(HttpHandler.ENDPOINT_FIELD);
    }

    public void setEndpoint(final String endpoint) {
        this.put(HttpHandler.ENDPOINT_FIELD, endpoint);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getQueryParams() {
        return (Map<String, String>) this.get(HttpHandler.QUERY_PARAMS_FIELD);
    }

    public void setQueryParams(final Map<String, String> queryParams) {
        this.put(HttpHandler.QUERY_PARAMS_FIELD, queryParams);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getHeaders() {
        return (Map<String, String>) this.get(HttpHandler.HEADERS_FIELD);
    }

    public void setHeaders(final Map<String, String> headers) {
        this.put(HttpHandler.HEADERS_FIELD, headers);
    }

    public String getBody() {
        return (String) this.get(HttpHandler.BODY_FIELD);
    }

    public void setBody(final String body) {
        this.put(HttpHandler.BODY_FIELD, body);
    }
}
