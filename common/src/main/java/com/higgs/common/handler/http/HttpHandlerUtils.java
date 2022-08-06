package com.higgs.common.handler.http;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HttpHandlerUtils {
    public String getFullUrl(@NonNull final HttpHandlerRequest request) {
        final String intendedConnectType = request.getConnectType() != null ? request.getConnectType() : "http";
        final String intendedPort = StringUtils.isBlank(request.getPort()) ? StringUtils.EMPTY : ":" + request.getPort();
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

    private String getFormattedEndpoint(final HttpHandlerRequest request) {
        if (StringUtils.isNotBlank(request.getEndpoint())) {
            String formattedEndpoint = request.getEndpoint().charAt(0) == '/' ? request.getEndpoint().substring(1) : request.getEndpoint();
            formattedEndpoint = formattedEndpoint.charAt(formattedEndpoint.length() - 1) == '/' ? formattedEndpoint.substring(0, formattedEndpoint.length() - 1) : formattedEndpoint;
            return formattedEndpoint;
        }
        return StringUtils.EMPTY;
    }

    public boolean typeMatches(final Object expectedType, final Object value) {
        if (expectedType instanceof String expectedTypeString) {
            return this.typeMatches(expectedTypeString, value);
        } else if (expectedType instanceof Class<?> expectedTypeClass) {
            return expectedTypeClass.isAssignableFrom(value.getClass());
        } else {
            return expectedType.getClass().isAssignableFrom(value.getClass());
        }
    }

    private boolean typeMatches(final String expectedType, final Object value) {
        return switch (expectedType.toLowerCase(Locale.ROOT)) {
            case "string" -> String.class.isAssignableFrom(value.getClass());
            case "number" -> this.isAssignableFromAny(value.getClass(), Number.class, int.class, double.class, byte.class, short.class, long.class);
            case "object" -> Map.class.isAssignableFrom(value.getClass());
            case "boolean", "bool" -> this.isAssignableFromAny(value.getClass(), Boolean.class, boolean.class);
            default -> throw new IllegalArgumentException("unexpected template data type");
        };
    }

    private boolean isAssignableFromAny(final Class<?> actualClass, final Class<?>... classes) {
        return Arrays.stream(classes).anyMatch(cla -> cla.isAssignableFrom(actualClass));
    }
}
