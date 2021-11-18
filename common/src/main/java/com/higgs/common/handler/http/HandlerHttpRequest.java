package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class HandlerHttpRequest extends HandlerRequest {
    private HttpMethod httpMethod;
    private String connectType;
    private String url;
    private String port;
    private String endpoint;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private String body;

    public String getFullUrl() {
        final String intendedConnectType = this.connectType != null ? this.connectType : "http";
        final String intendedPort = this.port == null ? StringUtils.EMPTY : ":" + this.port;
        return String.format("%s://%s%s/%s/%s", intendedConnectType, this.url, intendedPort, this.getFormattedEndpoint(), this.getQueryParamsString(this.queryParams));
    }

    private String getQueryParamsString(final Map<String, String> queryParams) {
        String queryParamsString = queryParams.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining("&"));
        queryParamsString = StringUtils.isNotBlank(queryParamsString) ? String.format("?%s", queryParamsString) : StringUtils.EMPTY;
        return queryParamsString;
    }

    private String getFormattedEndpoint() {
        if (StringUtils.isNotBlank(this.endpoint)) {
            String formattedEndpoint = this.endpoint.charAt(0) == '/' ? this.endpoint.substring(1) : this.endpoint;
            formattedEndpoint = formattedEndpoint.charAt(formattedEndpoint.length() - 1) == '/' ? formattedEndpoint.substring(0, formattedEndpoint.length() - 1) : formattedEndpoint;
            return formattedEndpoint;
        }
        return StringUtils.EMPTY;
    }
}

