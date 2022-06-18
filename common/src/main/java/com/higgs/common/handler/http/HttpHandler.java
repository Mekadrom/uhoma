package com.higgs.common.handler.http;

import com.higgs.common.handler.Handler;
import com.higgs.common.handler.HandlerDefinition;
import com.higgs.common.handler.HandlerHandler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class HttpHandler implements Handler<HttpHandlerRequest, HttpHandlerResponse> {
    private static final String CONNECTION_FAILURE_ERROR = "couldn't create connection for url %s";

    static final String NAME = "http_handler";

    static final String METHOD_FIELD = "method";
    static final String CONNECT_TYPE_FIELD = "connect_type";
    static final String URL_FIELD = "url";
    static final String PORT_FIELD = "port";
    static final String ENDPOINT_FIELD = "endpoint";
    static final String QUERY_PARAMS_FIELD = "query_params";
    static final String HEADERS_FIELD = "headers";
    static final String BODY_FIELD = "body";

    private static final String STRING_TYPE = "string";
    private static final String OBJECT_TYPE = "object";

    static final HandlerDefinition PROTOTYPE_HANDLER_DEF = new HandlerDefinition(
            Map.of(Handler.IS_BUILTIN, true, Handler.BUILTIN_TYPE, HttpHandler.NAME),
            Map.of(
                    HttpHandler.METHOD_FIELD, HttpHandler.STRING_TYPE,
                    HttpHandler.CONNECT_TYPE_FIELD, HttpHandler.STRING_TYPE,
                    HttpHandler.URL_FIELD, HttpHandler.STRING_TYPE,
                    HttpHandler.PORT_FIELD, HttpHandler.STRING_TYPE,
                    HttpHandler.ENDPOINT_FIELD, HttpHandler.STRING_TYPE,
                    HttpHandler.QUERY_PARAMS_FIELD, HttpHandler.OBJECT_TYPE,
                    HttpHandler.HEADERS_FIELD, HttpHandler.OBJECT_TYPE,
                    HttpHandler.BODY_FIELD, HttpHandler.STRING_TYPE
            )
    );

    private final HttpHandlerUtil httpHandlerUtil;

    @Override
    public List<HttpHandlerResponse> handle(@Nullable final HandlerDefinition handlerDef,
                                            @NonNull final Map<String, Object> headers,
                                            @NonNull final HttpHandlerRequest request,
                                            @NonNull final HandlerHandler handlerHandler) {
        final String fullUrl = this.httpHandlerUtil.getFullUrl(request);
        try {
            final HttpURLConnection connection = (HttpURLConnection) this.getUrl(fullUrl).openConnection();
            try {
                return this.configureAndHandle(connection, request);
            } finally {
                connection.disconnect();
            }
        } catch (final IOException e) {
            HttpHandler.log.error(String.format(HttpHandler.CONNECTION_FAILURE_ERROR, fullUrl), e);
        }
        return Collections.emptyList();
    }

    URL getUrl(final String fullUrl) throws MalformedURLException {
        return new URL(fullUrl);
    }

    List<HttpHandlerResponse> configureAndHandle(@NonNull final HttpURLConnection connection, @NonNull final HttpHandlerRequest request) throws IOException {
        this.setHeaders(connection, request.getHeaders());

        connection.setRequestMethod(Optional.ofNullable(request.getHttpMethod()).map(Enum::toString).orElse("GET"));
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        this.writeRequest(connection, request);
        final int responseCode = connection.getResponseCode(); // actually does request
        if (request.getReturnResponse()) {
            return List.of(this.buildResponse(responseCode, connection, request));
        }
        return Collections.emptyList();
    }

    HttpHandlerResponse buildResponse(final int responseCode, final HttpURLConnection connection, final HttpHandlerRequest request) {
        final HttpHandlerResponse handlerResponse = new HttpHandlerResponse(request);
        handlerResponse.setResponseCode(responseCode);
        handlerResponse.setBody(this.getResponse(connection, request));
        handlerResponse.setHeaders(connection.getHeaderFields());
        return handlerResponse;
    }

    String getResponse(final HttpURLConnection connection, final HttpHandlerRequest request) {
        try {
            final InputStream is = connection.getInputStream();
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                final List<String> lines = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                return String.join("\n", lines);
            }
        } catch (final IOException e) {
            HttpHandler.log.error(String.format("couldn't response for url %s", this.httpHandlerUtil.getFullUrl(request)), e);
        }
        return null;
    }

    void writeRequest(final HttpURLConnection connection, final HttpHandlerRequest request) {
        final String requestBody = request.getBody();
        if (StringUtils.isNotBlank(requestBody)) {
            this.writeRequest(connection, this.httpHandlerUtil.getFullUrl(request), requestBody);
        }
    }

    void writeRequest(final HttpURLConnection connection, final String url, final String requestBody) {
        try (final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
            dataOutputStream.writeBytes(requestBody);
        } catch (final IOException e) {
            HttpHandler.log.error(String.format("couldn't write request for url %s: %s", url, requestBody));
        }
    }

    void setHeaders(final HttpURLConnection connection, final Map<String, String> headers) {
        Optional.ofNullable(headers).ifPresent(it -> it.forEach(connection::setRequestProperty));
    }

    @Override
    @SuppressWarnings("unchecked")
    public HttpHandlerRequest requestBodyToRequestObj(@NonNull final Map<String, Object> requestBody) {
        final HttpHandlerRequest request = new HttpHandlerRequest();
        final Map<String, Object> valueMap = this.mergeValuesOntoDefTemplate(HttpHandler.PROTOTYPE_HANDLER_DEF, requestBody);
        request.setHttpMethod(HttpMethod.valueOf(String.valueOf(valueMap.get(HttpHandler.METHOD_FIELD))));
        request.setConnectType(String.valueOf(valueMap.get(HttpHandler.CONNECT_TYPE_FIELD)));
        request.setUrl(String.valueOf(valueMap.get(HttpHandler.URL_FIELD)));
        request.setPort(String.valueOf(valueMap.get(HttpHandler.PORT_FIELD)));
        request.setEndpoint(String.valueOf(valueMap.get(HttpHandler.ENDPOINT_FIELD)));
        request.setQueryParams((Map<String, String>) valueMap.get(HttpHandler.QUERY_PARAMS_FIELD));
        request.setHeaders((Map<String, String>) valueMap.get(HttpHandler.HEADERS_FIELD));
        request.setBody(String.valueOf(valueMap.get(HttpHandler.BODY_FIELD)));
        return request;
    }

    Map<String, Object> mergeValuesOntoDefTemplate(final HandlerDefinition handlerDef, final Map<String, Object> requestBody) {
        final Map<String, Object> map = new HashMap<>();

        // only put keys which exist in the handler def
        requestBody.entrySet().stream()
                .filter(e -> handlerDef.getDef().containsKey(e.getKey()))
                .filter(e -> this.httpHandlerUtil.typeMatches(handlerDef.getDef().get(e.getKey()), e.getValue()))
                .forEach(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    @Override
    public String getName() {
        return HttpHandler.NAME;
    }

    @Override
    public HandlerDefinition getPrototypeHandlerDef() {
        return HttpHandler.PROTOTYPE_HANDLER_DEF;
    }
}
