package com.higgs.common.handler.http;

import com.higgs.common.handler.Handler;
import com.higgs.common.util.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@AllArgsConstructor
public class HttpHandler implements Handler<HandlerHttpRequest, HandlerHttpResponse> {
    private static final String HTTP_HANDLER = "http_handler";

    private static final String METHOD_FIELD = "method";
    private static final String CONNECT_TYPE_FIELD = "connect_type";
    private static final String URL_FIELD = "url";
    private static final String PORT_FIELD = "port";
    private static final String ENDPOINT_FIELD = "endpoint";
    private static final String QUERY_PARAMS_FIELD = "query_params";
    private static final String HEADERS_FIELD = "headers";
    private static final String BODY_FIELD = "body";

    @Override
    public HandlerHttpResponse handle(final HandlerHttpRequest request) {
        HttpURLConnection connection = null;
        final String fullUrl = request.getFullUrl();
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

    private HandlerHttpResponse buildResponse(final int responseCode, final HttpURLConnection connection, final HandlerHttpRequest request) {
        final HandlerHttpResponse handlerResponse = new HandlerHttpResponse(request);
        handlerResponse.setResponseCode(responseCode);

        final String response = this.getResponse(connection, request);
        handlerResponse.setBody(response);
        handlerResponse.setHeaders(connection.getHeaderFields());
        return handlerResponse;
    }

    private String getResponse(final HttpURLConnection connection, final HandlerHttpRequest request) {
        try {
            final InputStream is = connection.getInputStream();
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                final List<String> lines = new ArrayList<>();

                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                return String.join("\n", lines);
            }
        } catch (final IOException e) {
            HttpHandler.log.error(String.format("couldn't response for url %s", request.getFullUrl()), e);
        }
        return null;
    }

    private void writeRequest(final HttpURLConnection connection, final HandlerHttpRequest request) {
        try (final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream())) {
            dataOutputStream.writeBytes(request.getBody());
        } catch (final IOException e) {
            HttpHandler.log.error(String.format("couldn't write request for url %s: %s", request.getFullUrl(), request.getBody()));
        }
    }

    private void setHeaders(final HttpURLConnection connection, final Map<String, String> headers) {
        headers.forEach(connection::setRequestProperty);
    }

    @Override
    public HandlerHttpRequest requestBodyToRequestObj(final Map<String, Object> requestBody) {
        final HandlerHttpRequest request = new HandlerHttpRequest();
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
    public boolean qualifies(final Map<String, Object> handlerDef) {
        return Boolean.TRUE.equals(Boolean.valueOf(String.valueOf(handlerDef.get(Handler.IS_BUILTIN)))) && HttpHandler.HTTP_HANDLER.equals(String.valueOf(handlerDef.get(Handler.BUILTIN_TYPE)));
    }
}
