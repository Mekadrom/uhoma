package com.higgs.common.handler.http;

import com.higgs.common.handler.HandlerDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { HttpHandler.class, HttpHandlerUtil.class })
class HttpHandlerTest {
    @Autowired
    private HttpHandler httpHandler;

    @ParameterizedTest
    @MethodSource("getTestSetHeadersParams")
    void testSetHeaders(final Map<String, String> headers, final int times) {
        final HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        this.httpHandler.setHeaders(mockConnection, headers);
        verify(mockConnection, times(times)).setRequestProperty(anyString(), anyString());
    }

    static Stream<Arguments> getTestSetHeadersParams() {
        return Stream.of(
                Arguments.of(
                        Map.of("bruh", "hurb"),
                        1
                ),
                Arguments.of(
                        Map.of("bruh", "hurb", "herb", "breh"),
                        2
                ),
                Arguments.of(
                        Collections.emptyMap(),
                        0
                ),
                Arguments.of(
                        null,
                        0
                )
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testRequestBodyToRequestObjNull() {
        assertThrows(IllegalArgumentException.class, () -> this.httpHandler.requestBodyToRequestObj(null));
    }

    @ParameterizedTest
    @MethodSource("getTestRequestBodyToRequestObjNonNullParams")
    void testRequestBodyToRequestObjNonNull(final Map<String, Object> requestBody, final HttpHandlerRequest expectedRequest) {
        assertThat(this.httpHandler.requestBodyToRequestObj(requestBody), is(equalTo(expectedRequest)));
    }

    static Stream<Arguments> getTestRequestBodyToRequestObjNonNullParams() {
        return Stream.of(
                Arguments.of(
                        Map.of(
                                HttpHandler.METHOD_FIELD, "GET",
                                HttpHandler.CONNECT_TYPE_FIELD, "http",
                                HttpHandler.URL_FIELD, "localhost",
                                HttpHandler.PORT_FIELD, "8080",
                                HttpHandler.ENDPOINT_FIELD, "rest/api/v2",
                                HttpHandler.QUERY_PARAMS_FIELD, Map.of("bruh", "hurb"),
                                HttpHandler.HEADERS_FIELD, Map.of("Content-Type", "application/json", "herb", "breh"),
                                HttpHandler.BODY_FIELD, "{\"test\":\"body\"}"
                        ),
                        HttpHandlerTestUtil.getTestRequestObj(
                                HttpMethod.GET,
                                "http",
                                "localhost",
                                "8080",
                                "rest/api/v2",
                                Map.of("bruh", "hurb"),
                                Map.of("Content-Type", "application/json", "herb", "breh"),
                                "{\"test\":\"body\"}"
                        )
                )
        );
    }

    @Test
    void testQualifiesNull() {
        assertThrows(IllegalArgumentException.class, () -> this.httpHandler.qualifies(null));
    }

    @ParameterizedTest
    @MethodSource("getTestQualifiesNonNullParams")
    void testQualifiesNonNull(final HandlerDefinition handlerDef, final boolean expectedResult) {
        assertThat(this.httpHandler.qualifies(handlerDef), is(equalTo(expectedResult)));
    }

    static Stream<Arguments> getTestQualifiesNonNullParams() {
        return Stream.of(
                Arguments.of(
                        new HandlerDefinition(Map.of("is_builtin", true, "builtin_type", "http_handler"), null),
                        true
                ),
                Arguments.of(
                        new HandlerDefinition(Map.of("extends_from", "http_handler"), null),
                        false
                ),
                Arguments.of(
                        new HandlerDefinition(Map.of("extends_from", "not_http_handler"), null),
                        false
                ),
                Arguments.of(
                        new HandlerDefinition(Map.of("is_builtin", false, "builtin_type", "http_handler"), null),
                        false
                ),
                Arguments.of(
                        new HandlerDefinition(Map.of("is_builtin", true, "builtin_type", "not_http_handler"), null),
                        false
                ),
                Arguments.of(
                        new HandlerDefinition(Map.of("is_builtin", true), null),
                        false
                ),
                Arguments.of(
                        new HandlerDefinition(Map.of("is_builtin", true, "extends_from", "http_handler"), null),
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getTestMergeMapsParams")
    void testMergeMaps(final HandlerDefinition handlerDef, final Map<String, Object> requestBody, final Map<String, Object> expected) {
        assertThat(this.httpHandler.mergeValuesOntoDefTemplate(handlerDef, requestBody), is(equalTo(expected)));
    }

    static Stream<Arguments> getTestMergeMapsParams() {
        return Stream.of(
                Arguments.of(
                        new HandlerDefinition(null, Map.of("testfield", "string", "other", "string")),
                        Map.of("testfield", "bruh", "other", "breh"),
                        Map.of("testfield", "bruh", "other", "breh")
                ),
                Arguments.of(
                        new HandlerDefinition(null, Map.of("testfield", "string", "other", "string")),
                        Map.of("testfield", "bruh", "notother", "breh"),
                        Map.of("testfield", "bruh")
                ),
                Arguments.of(
                        new HandlerDefinition(null, Map.of("testfield", "string", "other", "string")),
                        Map.of("nottestfield", "bruh", "notother", "breh"),
                        Map.of()
                )
        );
    }
}
