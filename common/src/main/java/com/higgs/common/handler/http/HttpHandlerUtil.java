package com.higgs.common.handler.http;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HttpHandlerUtil {
    public String getFullUrl(@NonNull final HttpHandlerRequest request) {
        final String intendedConnectType = request.getConnectType() != null ? request.getConnectType() : "http";
        final String intendedPort = request.getPort() == null ? StringUtils.EMPTY : ":" + request.getPort();
        return String.format("%s://%s%s/%s%s", intendedConnectType, request.getUrl(), intendedPort, this.getFormattedEndpoint(request), this.getQueryParamsString(request.getQueryParams()));
    }

    private String getQueryParamsString(final Map<String, String> queryParams) {
        if (queryParams != null) {
            String queryParamsString = queryParams.entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue())).collect(Collectors.joining("&"));
            queryParamsString = StringUtils.isNotBlank(queryParamsString) ? String.format("?%s", queryParamsString) : StringUtils.EMPTY;
            return queryParamsString;
        }
        return StringUtils.EMPTY;
    }

    private String getFormattedEndpoint(@NonNull final HttpHandlerRequest request) {
        if (StringUtils.isNotBlank(request.getEndpoint())) {
            String formattedEndpoint = request.getEndpoint().charAt(0) == '/' ? request.getEndpoint().substring(1) : request.getEndpoint();
            formattedEndpoint = formattedEndpoint.charAt(formattedEndpoint.length() - 1) == '/' ? formattedEndpoint.substring(0, formattedEndpoint.length() - 1) : formattedEndpoint;
            return formattedEndpoint;
        }
        return StringUtils.EMPTY;
    }
}
