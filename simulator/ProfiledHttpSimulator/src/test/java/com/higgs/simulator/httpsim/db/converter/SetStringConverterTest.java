package com.higgs.simulator.httpsim.db.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class SetStringConverterTest {
    private SetStringConverter converter;

    @BeforeEach
    public void setUp() throws Exception {
        this.converter = new SetStringConverter();
    }

    @ParameterizedTest
    @MethodSource("getTestConvertToDatabaseValueParams")
    void testConvertToDatabaseColumn(final Set<String> value, final String expected) {
        assertThat(this.converter.convertToDatabaseColumn(value), is(expected));
    }

    public static Stream<Arguments> getTestConvertToDatabaseValueParams() {
        return Stream.of(
                Arguments.of(Set.of("a", "b", "c"), "a,b,c"),
                Arguments.of(Set.of("a", "b", "c", "d"), "a,b,c,d"),
                Arguments.of(Set.of(), "")
        );
    }

    @ParameterizedTest
    @MethodSource("getTestConvertToEntityAttributeParams")
    void testConvertToEntityAttribute(final String value, final Set<String> expected) {
        assertThat(this.converter.convertToEntityAttribute(value), is(expected));
    }

    public static Stream<Arguments> getTestConvertToEntityAttributeParams() {
        return Stream.of(
                Arguments.of("a,b,c", Set.of("a", "b", "c")),
                Arguments.of("a,b,c,d", Set.of("a", "b", "c", "d")),
                Arguments.of("", Set.of())
        );
    }
}
