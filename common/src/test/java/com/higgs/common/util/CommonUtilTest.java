package com.higgs.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonUtilTest {
    private final CommonUtils commonUtils = new CommonUtils();

    @Test
    void testParseMap() throws JsonProcessingException {
        final Map<String, Object> actualMap = this.commonUtils.parseMap("{\"bruh\": 1, \"herb\": true}");
        final Map<String, Object> expectedMap = Stream.of(
                new AbstractMap.SimpleEntry<>("bruh", 1),
                new AbstractMap.SimpleEntry<>("herb", true)
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        assertThat(actualMap, is(equalTo((expectedMap))));
    }

    @Test
    void testToObjectMapNonNull() {
        final Map<String, String> map = Map.of("hello", "world", "world", "1");
        final Map<String, Object> expectedMap = Map.of("hello", "world", "world", "1");
        assertThat(this.commonUtils.toObjectMap(map), is(equalTo(expectedMap)));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testToObjectMapNull() {
        assertThrows(IllegalArgumentException.class, () -> this.commonUtils.toObjectMap(null));
    }

    @ParameterizedTest
    @MethodSource("getTestFlattenMapParams")
    <K, V> void testFlattenMap(final Map<K, List<V>> input, final Map<K, V> expected) {
        assertThat(this.commonUtils.flattenMap(input), is(equalTo(expected)));
    }

    static Stream<Arguments> getTestFlattenMapParams() {
        return Stream.of(
                Arguments.of(
                        Map.of("1", Stream.of("2", "3").collect(Collectors.toList())),
                        Map.of("1", "2")
                ),
                Arguments.of(
                        Map.of("1", Stream.of(null, "3").collect(Collectors.toList())),
                        Map.of("1", "3")
                )
        );
    }

    @Test
    void getDefaultMapper() {
        assertNotNull(this.commonUtils.getDefaultMapper());
    }
}