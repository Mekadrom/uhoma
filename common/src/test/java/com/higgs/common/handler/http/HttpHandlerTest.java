package com.higgs.common.handler.http;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.higgs.common.handler.HandlerDefinition;
import com.higgs.common.handler.HandlerHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WireMockTest
@ExtendWith(MockitoExtension.class)
class HttpHandlerTest {
    @Mock
    private HttpHandlerUtil httpHandlerUtil;

    private HttpHandler httpHandler;

    @BeforeEach
    void setUp() {
        this.httpHandler = new HttpHandler(this.httpHandlerUtil);
    }

    @Test
    @SneakyThrows
    void testHandle(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = new HashMap<>();
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        final HttpHandlerResponse response = mock(HttpHandlerResponse.class);
        final HandlerHandler handlerHandler = mock(HandlerHandler.class);
        doCallRealMethod().when(httpHandlerSpy).handle(any(), any(), any(), any());
        doReturn(List.of(response)).when(httpHandlerSpy).configureAndHandle(any(), any());
        stubFor(get("/test").willReturn(ok()));
        when(this.httpHandlerUtil.getFullUrl(any())).thenReturn("http://localhost:0/test");
        final List<HttpHandlerResponse> actual = httpHandlerSpy.handle(handlerDefinition, headers, request, handlerHandler);
        verify(httpHandlerSpy, times(1)).configureAndHandle(any(), any());
        assertThat(actual, is(List.of(response)));
    }

    @ParameterizedTest
    @MethodSource("getTestHandleInvalidArgsParams")
    void testHandleInvalidArgs(final Map<String, Object> headers, final HttpHandlerRequest request, final HandlerHandler handlerHandler) {
        assertThrows(IllegalArgumentException.class, () -> this.httpHandler.handle(null, headers, request, handlerHandler));
    }

    public static Stream<Arguments> getTestHandleInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(new HashMap<>(), null, null),
                Arguments.of(new HashMap<>(), mock(HttpHandlerRequest.class), null),
                Arguments.of(new HashMap<>(), null, mock(HandlerHandler.class))
        );
    }

    @Test
    @SneakyThrows
    void testHandleNoConnection() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = new HashMap<>();
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        final HandlerHandler handlerHandler = mock(HandlerHandler.class);
        doCallRealMethod().when(httpHandlerSpy).handle(any(), any(), any(), any());
        when(this.httpHandlerUtil.getFullUrl(any())).thenReturn("invalid url");
        final List<HttpHandlerResponse> actual = assertDoesNotThrow(() -> httpHandlerSpy.handle(handlerDefinition, headers, request, handlerHandler));
        verify(httpHandlerSpy, times(0)).configureAndHandle(any(), any());
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    @SneakyThrows
    void testConfigureAndHandle() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        final List<HttpHandlerResponse> actual = httpHandlerSpy.configureAndHandle(httpUrlConnection, request);
        verify(httpHandlerSpy, times(1)).setHeaders(any(), any());
        verify(httpHandlerSpy, times(1)).writeRequest(any(), any());
        verify(httpUrlConnection, times(1)).setRequestMethod(any());
        verify(httpUrlConnection, times(1)).setUseCaches(anyBoolean());
        verify(httpUrlConnection, times(1)).setDoOutput(anyBoolean());
        assertThat(actual, is(Collections.emptyList()));
    }

    @Test
    @SneakyThrows
    void testConfigureAndHandlerResponseReturned() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        final HttpHandlerResponse response = mock(HttpHandlerResponse.class);
        doCallRealMethod().when(httpHandlerSpy).configureAndHandle(any(), any());
        when(request.getReturnResponse()).thenReturn(true);
        doReturn(response).when(httpHandlerSpy).buildResponse(anyInt(), any(), any());
        final List<HttpHandlerResponse> actual = httpHandlerSpy.configureAndHandle(httpUrlConnection, request);
        assertThat(actual, is(List.of(response)));
    }

    @ParameterizedTest
    @MethodSource("getTestConfigureAndHandleInvalidArgsParams")
    void testConfigureAndHandleInvalidArgs(final HttpURLConnection httpUrlConnection, final HttpHandlerRequest request) {
        assertThrows(IllegalArgumentException.class, () -> this.httpHandler.configureAndHandle(httpUrlConnection, request));
    }

    public static Stream<Arguments> getTestConfigureAndHandleInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(mock(HttpURLConnection.class), null),
                Arguments.of(null, mock(HttpHandlerRequest.class))
        );
    }

    @Test
    void testBuildResponse() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        doReturn("body").when(httpHandlerSpy).getResponse(any(), any());
        final HttpHandlerResponse actual = httpHandlerSpy.buildResponse(200, httpUrlConnection, request);
        verify(httpHandlerSpy, times(1)).getResponse(any(), any());
        assertAll(
                () -> assertNotNull(actual),
                () -> assertThat(actual.getResponseCode(), is(200)),
                () -> assertThat(actual.getHeaders(), is(new HashMap<>())),
                () -> assertThat(actual.getBody(), is("body"))
        );
    }

    @Test
    @SneakyThrows
    void testGetResponse() {
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final InputStream inputStream = new ByteArrayInputStream("body\nbody".getBytes());
        when(httpUrlConnection.getInputStream()).thenReturn(inputStream);
        final String actual = this.httpHandler.getResponse(httpUrlConnection, null);
        assertThat(actual, is("body\nbody"));
    }

    @Test
    @SneakyThrows
    void testGetResponseThrows() {
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        when(httpUrlConnection.getInputStream()).thenThrow(new IOException());
        assertNull(assertDoesNotThrow(() -> this.httpHandler.getResponse(httpUrlConnection, null)));
    }

    @Test
    void testWriteRequestDelegates() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        when(request.getBody()).thenReturn("body");
        doNothing().when(httpHandlerSpy).writeRequest(any(), any(), any());
        httpHandlerSpy.writeRequest(httpUrlConnection, request);
        verify(httpHandlerSpy, times(1)).writeRequest(any(), any(), any());
    }

    @Test
    void testWriteRequestDoesntDelegate() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        httpHandlerSpy.writeRequest(httpUrlConnection, request);
        verify(httpHandlerSpy, times(0)).writeRequest(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void testWriteRequest() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final OutputStream outputStream = new ByteArrayOutputStream();
        when(httpUrlConnection.getOutputStream()).thenReturn(outputStream);
        httpHandlerSpy.writeRequest(httpUrlConnection, "test", "body");
        verify(httpUrlConnection, times(1)).getOutputStream();
        assertThat(outputStream.toString(), is("body"));
    }

    @Test
    @SneakyThrows
    void testWriteRequestThrows() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        when(httpUrlConnection.getOutputStream()).thenThrow(new IOException());
        assertDoesNotThrow(() -> httpHandlerSpy.writeRequest(httpUrlConnection, "test", "body"));
    }

    @Test
    void testSetHeaders() {
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        final Map<String, String> headers = Map.of("test", "test", "test2", "test2");
        this.httpHandler.setHeaders(httpUrlConnection, headers);
        verify(httpUrlConnection, times(1)).setRequestProperty("test", "test");
        verify(httpUrlConnection, times(1)).setRequestProperty("test2", "test2");
    }

    @Test
    void testSetHeadersNull() {
        final HttpURLConnection httpUrlConnection = mock(HttpURLConnection.class);
        this.httpHandler.setHeaders(httpUrlConnection, null);
        verify(httpUrlConnection, times(0)).setRequestProperty(any(), any());
    }

    @Test
    void testRequestBodyToRequestObj() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final Map<String, Object> requestBody = Map.of(
                HttpHandler.METHOD_FIELD, "GET",
                HttpHandler.CONNECT_TYPE_FIELD, "http",
                HttpHandler.URL_FIELD, "localhost",
                HttpHandler.PORT_FIELD, "8080",
                HttpHandler.ENDPOINT_FIELD, "endpoint",
                HttpHandler.QUERY_PARAMS_FIELD, Map.of("test1", "test1"),
                HttpHandler.HEADERS_FIELD, Map.of("test2", "test2"),
                HttpHandler.BODY_FIELD, "body"
        );
        doReturn(requestBody).when(httpHandlerSpy).mergeValuesOntoDefTemplate(any(), any());
        final HttpHandlerRequest actual = httpHandlerSpy.requestBodyToRequestObj(requestBody);
        assertAll(
                () -> assertThat(actual.getHttpMethod(), is(HttpMethod.GET)),
                () -> assertThat(actual.getConnectType(), is("http")),
                () -> assertThat(actual.getUrl(), is("localhost")),
                () -> assertThat(actual.getPort(), is("8080")),
                () -> assertThat(actual.getEndpoint(), is("endpoint")),
                () -> assertThat(actual.getQueryParams(), is(Map.of("test1", "test1"))),
                () -> assertThat(actual.getHeaders(), is(Map.of("test2", "test2"))),
                () -> assertThat(actual.getBody(), is("body"))
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testRequestBodyToRequestObjNullArg() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        assertThrows(IllegalArgumentException.class, () -> httpHandlerSpy.requestBodyToRequestObj(null));
    }

    @Test
    void testMergeValuesOntoDefTemplateCorrectType() {
        final HttpHandlerUtil httpHandlerUtil = new HttpHandlerUtil();
        final HttpHandler httpHandler = new HttpHandler(httpHandlerUtil);
        final HandlerDefinition handlerDefinition = HttpHandler.PROTOTYPE_HANDLER_DEF;
        final Map<String, Object> requestBody = Map.of(
                HttpHandler.METHOD_FIELD, "GET",
                HttpHandler.CONNECT_TYPE_FIELD, "http",
                HttpHandler.URL_FIELD, "localhost",
                HttpHandler.PORT_FIELD, "8080",
                HttpHandler.ENDPOINT_FIELD, "endpoint",
                HttpHandler.QUERY_PARAMS_FIELD, Map.of("test1", "test1"),
                HttpHandler.HEADERS_FIELD, Map.of("test2", "test2"),
                HttpHandler.BODY_FIELD, "body",
                "extra_field", "extra_value"
        );
        final Map<String, Object> actual = httpHandler.mergeValuesOntoDefTemplate(handlerDefinition, requestBody);
        assertAll(
                () -> assertThat(actual.get(HttpHandler.METHOD_FIELD), is("GET")),
                () -> assertThat(actual.get(HttpHandler.CONNECT_TYPE_FIELD), is("http")),
                () -> assertThat(actual.get(HttpHandler.URL_FIELD), is("localhost")),
                () -> assertThat(actual.get(HttpHandler.PORT_FIELD), is("8080")),
                () -> assertThat(actual.get(HttpHandler.ENDPOINT_FIELD), is("endpoint")),
                () -> assertThat(actual.get(HttpHandler.QUERY_PARAMS_FIELD), is(Map.of("test1", "test1"))),
                () -> assertThat(actual.get(HttpHandler.HEADERS_FIELD), is(Map.of("test2", "test2"))),
                () -> assertThat(actual.get(HttpHandler.BODY_FIELD), is("body")),
                () -> assertNull(actual.get("extra_field"))
        );
    }

    @Test
    void testMergeValuesOntoDefTemplateWrongTypes() {
        final HttpHandlerUtil httpHandlerUtil = new HttpHandlerUtil();
        final HttpHandler httpHandler = new HttpHandler(httpHandlerUtil);
        final HandlerDefinition handlerDefinition = HttpHandler.PROTOTYPE_HANDLER_DEF;
        final Map<String, Object> requestBody = Map.of(
                HttpHandler.METHOD_FIELD, 1,
                HttpHandler.CONNECT_TYPE_FIELD, 2,
                HttpHandler.URL_FIELD, 3,
                HttpHandler.PORT_FIELD, 4,
                HttpHandler.ENDPOINT_FIELD, 5,
                HttpHandler.QUERY_PARAMS_FIELD, "6",
                HttpHandler.HEADERS_FIELD, new Object() {
                    private static final int SEVEN = 7;
                },
                HttpHandler.BODY_FIELD, 8
        );
        final Map<String, Object> actual = httpHandler.mergeValuesOntoDefTemplate(handlerDefinition, requestBody);
        assertAll(
                () -> assertNull(actual.get(HttpHandler.METHOD_FIELD)),
                () -> assertNull(actual.get(HttpHandler.CONNECT_TYPE_FIELD)),
                () -> assertNull(actual.get(HttpHandler.URL_FIELD)),
                () -> assertNull(actual.get(HttpHandler.PORT_FIELD)),
                () -> assertNull(actual.get(HttpHandler.ENDPOINT_FIELD)),
                () -> assertNull(actual.get(HttpHandler.QUERY_PARAMS_FIELD)),
                () -> assertNull(actual.get(HttpHandler.HEADERS_FIELD)),
                () -> assertNull(actual.get(HttpHandler.BODY_FIELD))
        );
    }

    @Test
    void testGetName() {
        assertThat(this.httpHandler.getName(), is(HttpHandler.NAME));
    }

    @Test
    void testGetPrototypeHandlerDef() {
        assertThat(this.httpHandler.getPrototypeHandlerDef(), is(HttpHandler.PROTOTYPE_HANDLER_DEF));
    }

    @Test
    @SneakyThrows
    void testHandleClosesConnection() {
        final HttpHandler httpHandlerSpy = spy(this.httpHandler);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = Map.of("test", "test");
        final HttpHandlerRequest request = mock(HttpHandlerRequest.class);
        final HandlerHandler handlerHandler = mock(HandlerHandler.class);

        final URL url = mock(URL.class);

        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);

        doReturn(url).when(httpHandlerSpy).getUrl(any());
        when(this.httpHandlerUtil.getFullUrl(any())).thenReturn("url");
        when(url.openConnection()).thenReturn(httpURLConnection);

        httpHandlerSpy.handle(handlerDefinition, headers, request, handlerHandler);

        verify(httpHandlerSpy, times(1)).getUrl("url");
        verify(httpHandlerSpy, times(1)).configureAndHandle(httpURLConnection, request);
        verify(httpURLConnection, times(1)).disconnect();
    }
}
