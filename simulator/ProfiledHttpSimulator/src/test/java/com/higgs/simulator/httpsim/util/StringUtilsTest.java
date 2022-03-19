package com.higgs.simulator.httpsim.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StringUtilsTest {
    @ParameterizedTest
    @MethodSource("getTestSetToStringParams")
    void testSetToString(final Set<String> set, final String expected) {
        assertThat(StringUtils.setToString(set), is(expected));
    }

    public static Stream<Arguments> getTestSetToStringParams() {
        return Stream.of(
                Arguments.of(Set.of("a", "b", "c"), "a,b,c"),
                Arguments.of(Set.of("a", "b", "c", "d"), "a,b,c,d"),
                Arguments.of(Set.of(), null),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestStringToSetParams")
    void testStringToSet(final String str, final Set<String> expected) {
        assertThat(StringUtils.stringToSet(str), is(expected));
    }

    public static Stream<Arguments> getTestStringToSetParams() {
        return Stream.of(
                Arguments.of("a,b,c", Set.of("a", "b", "c")),
                Arguments.of("a,b,c,d", Set.of("a", "b", "c", "d")),
                Arguments.of(null, Set.of()),
                Arguments.of("", Set.of())
        );
    }

    @Test
    void testAddToResultFromStringToSet() {
        assertDoesNotThrow(() -> StringUtils.stringToSet("").add("a"));
    }
}
