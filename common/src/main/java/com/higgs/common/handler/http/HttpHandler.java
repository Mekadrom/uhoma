package com.higgs.common.handler.http;

import com.higgs.common.handler.Handler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class HttpHandler implements Handler<HttpHandlerRequest, HttpHandlerResponse> {
    static final String HTTP_HANDLER = "http_handler";

    static final String METHOD_FIELD = "method";
    static final String CONNECT_TYPE_FIELD = "connect_type";
    static final String URL_FIELD = "url";
    static final String PORT_FIELD = "port";
    static final String ENDPOINT_FIELD = "endpoint";
    static final String QUERY_PARAMS_FIELD = "query_params";
    static final String HEADERS_FIELD = "headers";
    static final String BODY_FIELD = "body";

    private final HttpHandlerUtil httpHandlerUtil;

    @Override
    public HttpHandlerResponse handle(@NonNull final HttpHandlerRequest request) {
        HttpURLConnection connection = null;
        final String fullUrl = this.httpHandlerUtil.getFullUrl(request);
        try {
            final URL url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getHttpMethod().toString());
            this.setHeaders(connection, request.getHeaders());
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            this.writeRequest(connection, request);
            final int responseCode = connection.getResponseCode(); // actually does request
            if (request.getReturnResponse()) {
                return this.buildResponse(responseCode, connection, request);
            }
        } catch (final MalformedURLException e) {
            HttpHandler.log.error(String.format("couldn't create url %s", fullUrl), e);
        } catch (final IOException e) {
            HttpHandler.log.error(String.format("couldn't create connection for url %s", fullUrl), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private HttpHandlerResponse buildResponse(final int responseCode, final HttpURLConnection connection, final HttpHandlerRequest request) {
        final HttpHandlerResponse handlerResponse = new HttpHandlerResponse(request);
        handlerResponse.setResponseCode(responseCode);

        final String response = this.getResponse(connection, request);
        handlerResponse.setBody(response);
        handlerResponse.setHeaders(connection.getHeaderFields());
        return handlerResponse;
    }

    private String getResponse(final HttpURLConnection connection, final HttpHandlerRequest request) {
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

    private void writeRequest(final HttpURLConnection connection, final HttpHandlerRequest request) {
        final String requestBody = request.getBody();
        if (StringUtils.isNotBlank(requestBody)) {
            this.writeRequest(connection, this.httpHandlerUtil.getFullUrl(request), request.getBody());
        }
    }

    private void writeRequest(final HttpURLConnection connection, final String url, final String requestBody) {
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
    public HttpHandlerRequest requestBodyToRequestObj(@NonNull final Map<String, Object> requestBody) {
        final HttpHandlerRequest request = new HttpHandlerRequest();
        request.setHttpMethod(HttpMethod.valueOf(String.valueOf(requestBody.get(HttpHandler.METHOD_FIELD))));
        request.setConnectType(String.valueOf(requestBody.get(HttpHandler.CONNECT_TYPE_FIELD)));
        request.setUrl(String.valueOf(requestBody.get(HttpHandler.URL_FIELD)));
        request.setPort(String.valueOf(requestBody.get(HttpHandler.PORT_FIELD)));
        request.setEndpoint(String.valueOf(requestBody.get(HttpHandler.ENDPOINT_FIELD)));
        request.setQueryParams((Map<String, String>) requestBody.get(HttpHandler.QUERY_PARAMS_FIELD));
        request.setHeaders((Map<String, String>) requestBody.get(HttpHandler.HEADERS_FIELD));
        request.setBody(String.valueOf(requestBody.get(HttpHandler.BODY_FIELD)));
        return request;
    }

    @Override
    public boolean qualifies(@NonNull final Map<String, Object> handlerDef) {
        return (this.isBuiltin(handlerDef) && this.builtinTypeIs(handlerDef, HttpHandler.HTTP_HANDLER)) || this.extendsFrom(handlerDef, HttpHandler.HTTP_HANDLER);
    }
}
