package com.higgs.common.handler.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
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
}