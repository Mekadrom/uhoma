package com.higgs.common.handler.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensionHandlerRequestTest {
    @Test
    void testConstructorCallsSuper() {
        final ExtensionHandlerRequest request = new ExtensionHandlerRequest(Map.of("test", "value"));
        assertEquals("value", request.get("test"));
    }

    @ParameterizedTest
    @MethodSource("getTestGetParametersParams")
    void testGetParameters(final Map<String, Object> input, final Map<String, Object> expected) {
        final ExtensionHandlerRequest request = new ExtensionHandlerRequest(input);
        assertEquals(expected, request.getParameters());
    }

    public static Stream<Arguments> getTestGetParametersParams() {
        return Stream.of(
                Arguments.of(Map.of("actionWithParams", Map.of("parameters", List.of(Map.of("name", "test", "currentValue", "value")))), Map.of("test", "value")),
                Arguments.of(Map.of("actionWithParams", Map.of("parameters", List.of(Map.of()))), Map.of()),
                Arguments.of(Map.of("actionWithParams", Map.of()), Map.of()),
                Arguments.of(Map.of(), Map.of())
        );
    }

    @Test
    void testCanAddToGetParametersMap() {
        final Map<String, Object> input = Map.of("actionWithParams", Map.of("parameters", List.of(Map.of("name", "test", "currentValue", "value"))));
        final ExtensionHandlerRequest request = new ExtensionHandlerRequest(input);

        assertDoesNotThrow(() -> request.getParameters().put("test", "value2"));
    }
}
