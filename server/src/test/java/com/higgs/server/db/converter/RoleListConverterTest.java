package com.higgs.server.db.converter;

import com.higgs.server.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link RoleListConverter}.
 */
class RoleListConverterTest {
    private RoleListConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = new RoleListConverter();
    }

    /**
     * Test for {@link RoleListConverter#convertToDatabaseColumn(List)}. Verifies that the method returns a comma
     * separated string of the roles from the input list.
     * @param input The input {@link List} of {@link Role}.
     * @param expected The expected output string.
     */
    @ParameterizedTest
    @MethodSource("getTestConvertToDatabaseColumnParams")
    void testConvertToDatabaseColumn(final List<Role> input, final String expected) {
        assertThat(this.converter.convertToDatabaseColumn(input), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestConvertToDatabaseColumnParams() {
        return Stream.of(
                Arguments.of(List.of(Role.ADMIN), "ROLE_ADMIN"),
                Arguments.of(List.of(Role.USER), "ROLE_USER"),
                Arguments.of(List.of(Role.ADMIN, Role.USER), "ROLE_ADMIN,ROLE_USER"),
                Arguments.of(List.of(), null),
                Arguments.of(null, null)
        );
    }

    /**
     * Test for {@link RoleListConverter#convertToEntityAttribute(String)}. Verifies that the method returns a list of
     * {@link Role}s from the input string.
     * @param input The input string.
     * @param expected The expected output {@link List} of {@link Role}.
     */
    @ParameterizedTest
    @MethodSource("getTestConvertToEntityAttributeParams")
    void testConvertToEntityAttribute(final String input, final List<Role> expected) {
        assertThat(this.converter.convertToEntityAttribute(input), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestConvertToEntityAttributeParams() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", List.of(Role.ADMIN)),
                Arguments.of("ROLE_USER", List.of(Role.USER)),
                Arguments.of("ROLE_ADMIN,ROLE_USER", List.of(Role.ADMIN, Role.USER)),
                Arguments.of("", new ArrayList<>()),
                Arguments.of(null, new ArrayList<>())
        );
    }
}
