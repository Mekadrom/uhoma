package com.higgs.simulator.httpsim.db.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonMapConverterTest {
    private JsonMapConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = new JsonMapConverter();
    }

    @ParameterizedTest
    @MethodSource("getTestConvertToDatabaseColumn")
    void testConvertToDatabaseColumn(final Map<String, Object> input, final String expected) {
        assertThat(this.converter.convertToDatabaseColumn(input), is(expected));
    }

    public static Stream<Arguments> getTestConvertToDatabaseColumn() {
        return Stream.of(
                Arguments.of(null, "null"),
                Arguments.of(Map.of(), "{}"),
                Arguments.of(Map.of("key", "value"), "{\"key\":\"value\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("getTestConvertToEntityAttributeParams")
    void testConvertToEntityAttribute(final String input, final Map<String, Object> expected) {
        assertThat(this.converter.convertToEntityAttribute(input), is(expected));
    }

    public static Stream<Arguments> getTestConvertToEntityAttributeParams() {
        return Stream.of(
                Arguments.of("", null),
                Arguments.of("{}", Map.of()),
                Arguments.of("{\"key\":\"value\"}", Map.of("key", "value"))
        );
    }

    @Test
    void testConvertToEntityAttributeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> this.converter.convertToEntityAttribute(null));
    }
}
