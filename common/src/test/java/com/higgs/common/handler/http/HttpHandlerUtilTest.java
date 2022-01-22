package com.higgs.common.handler.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { HttpHandler.class, HttpHandlerUtil.class })
class HttpHandlerUtilTest {
    @Autowired
    private HttpHandlerUtil httpHandlerUtil;

    @Test
    void testGetFullUrlNull() {
        assertThrows(IllegalArgumentException.class, () -> this.httpHandlerUtil.getFullUrl(null));
    }

    @ParameterizedTest
    @MethodSource("testGetFullUrlNonNullParams")
    void testGetFullUrlNonNull(final HttpHandlerRequest request, final String expectedFullUrl) {
        assertThat(this.httpHandlerUtil.getFullUrl(request), is(equalTo(expectedFullUrl)));
    }

    static Stream<Arguments> testGetFullUrlNonNullParams() {
        return Stream.of(
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                "8080",
                                "rest/api/v2",
                                Map.of("bruh", "hurb"),
                                null,
                                null
                        ),
                        "http://localhost:8080/rest/api/v2?bruh=hurb"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                "8080",
                                null,
                                Map.of("bruh", "hurb"),
                                null,
                                null
                        ),
                        "http://localhost:8080/?bruh=hurb"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                null,
                                "rest/api/v2",
                                Map.of("bruh", "hurb"),
                                null,
                                null
                        ),
                        "http://localhost/rest/api/v2?bruh=hurb"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                "8080",
                                "rest/api/v2",
                                null,
                                null,
                                null
                        ),
                        "http://localhost:8080/rest/api/v2"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                null,
                                "localhost",
                                "8080",
                                "rest/api/v2",
                                Map.of("bruh", "hurb"),
                                null,
                                null
                        ),
                        "http://localhost:8080/rest/api/v2?bruh=hurb"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                "8080",
                                "/rest/api/v2",
                                Map.of("bruh", "hurb"),
                                null,
                                null
                        ),
                        "http://localhost:8080/rest/api/v2?bruh=hurb"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                "8080",
                                "rest/api/v2/",
                                Map.of("bruh", "hurb"),
                                null,
                                null
                        ),
                        "http://localhost:8080/rest/api/v2?bruh=hurb"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "localhost",
                                "8080",
                                "rest/api/v2",
                                Collections.emptyMap(),
                                null,
                                null
                        ),
                        "http://localhost:8080/rest/api/v2"
                ),
                Arguments.of(
                        HttpHandlerTestUtil.getTestRequestObj(
                                null,
                                "http",
                                "other-url.com",
                                "8080",
                                "rest/api/v2",
                                Collections.emptyMap(),
                                null,
                                null
                        ),
                        "http://other-url.com:8080/rest/api/v2"
                )
        );
    }

    @Test
    void testTypeMatchesThrows() {
        assertThrows(IllegalArgumentException.class, () -> this.httpHandlerUtil.typeMatches("not a type", "valid value"));
    }

    @ParameterizedTest
    @MethodSource("getTestTypeMatchesParams")
    void testTypeMatches(final Object type, final Object value, final boolean expectedMatches) {
        assertThat(this.httpHandlerUtil.typeMatches(type, value), is(equalTo(expectedMatches)));
    }

    static Stream<Arguments> getTestTypeMatchesParams() {
        return Stream.of(
                Arguments.of(
                        "string",
                        "testvalue",
                        true
                ),
                Arguments.of(
                        "number",
                        1,
                        true
                ),
                Arguments.of(
                        "number",
                        1.2,
                        true
                ),
                Arguments.of(
                        "boolean",
                        true,
                        true
                ),
                Arguments.of(
                        "bool",
                        false,
                        true
                ),
                Arguments.of(
                        "BOOLEAN",
                        "testvalue",
                        false
                ),
                Arguments.of(
                        "object",
                        new HashMap<>(),
                        true
                ),
                Arguments.of(
                        String.class,
                        "testvalue",
                        true
                ),
                Arguments.of(
                        Number.class,
                        1L,
                        true
                ),
                Arguments.of(
                        Number.class,
                        new BigDecimal("1"),
                        true
                ),
                Arguments.of(
                        Number.class,
                        "2",
                        false
                ),
                Arguments.of(
                        2,
                        2,
                        true
                ),
                Arguments.of(
                        2,
                        "2",
                        false
                )
        );
    }
}
