package com.higgs.server.db.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceUtilsTest {
    @ParameterizedTest
    @MethodSource(value = "getLikeStringParams")
    @DisplayName("Test PersistenceUtils#getLikeString")
    void getLikeString(final String input, final String expected) {
        assertEquals(expected, PersistenceUtils.getLikeString(input));
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> getLikeStringParams() {
        return Stream.of(
                Arguments.of("test", "%test%"),
                Arguments.of("TEST", "%TEST%"),
                Arguments.of("1", "%1%"),
                Arguments.of(null, "%null%")
        );
    }
}