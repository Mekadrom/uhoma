package com.higgs.simulator.httpsim.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonUtilsTest {
    @Test
    void testConvertMapToJsonString() {
        assertThat(JsonUtils.convertMapToJsonString(Map.of("bruh", "value")), is("{\"bruh\":\"value\"}"));
    }

    @ParameterizedTest
    @MethodSource("getTestConvertJsonStringToMappedValues")
    void testConvertJsonStringToMappedValues(final String json, final Map<String, Object> expected) {
        assertThat(JsonUtils.convertJsonStringToMappedValues(json), is(expected));
    }

    public static Stream<Arguments> getTestConvertJsonStringToMappedValues() {
        return Stream.of(
                Arguments.of("{\"bruh\":\"value\"}", Map.of("bruh", "value")),
                Arguments.of("{\"bruh\":\"value\",\"bruh2\":\"value2\"}", Map.of("bruh", "value", "bruh2", "value2"))
        );
    }

    @ParameterizedTest
    @MethodSource("getTestConvertJsonStringToMappedValuesInvalidParams")
    void testConvertJsonStringToMappedValuesInvalid(final String input) {
        assertNull(JsonUtils.convertJsonStringToMappedValues(input));
    }

    public static Stream<Arguments> getTestConvertJsonStringToMappedValuesInvalidParams() {
        return Stream.of(
                Arguments.of("{\"bruh\":\"value\""),
                Arguments.of("{{")
        );
    }

    @Test
    void testSerialize() {
        assertThat(JsonUtils.serialize(Map.of()), is("{}"));
        assertThat(JsonUtils.serialize(Map.of("bruh", "value")), is("{\"bruh\":\"value\"}"));
    }
}
