package com.higgs.server.db.converter;

import com.higgs.server.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

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
     * Test for {@link RoleListConverter#convertToDatabaseColumn(Set)}. Verifies that the method returns a comma
     * separated string of the roles from the input list.
     *
     * @param input The input {@link Set} of {@link Role}.
     * @param expected The expected output string.
     */
    @ParameterizedTest
    @MethodSource("getTestConvertToDatabaseColumnParams")
    void testConvertToDatabaseColumn(final Set<Role> input, final List<String> expectedToBeIn) {
        if (expectedToBeIn == null) {
            assertNull(this.converter.convertToDatabaseColumn(input));
        } else {
            assertThat(expectedToBeIn.contains(this.converter.convertToDatabaseColumn(input)), is(true));
        }
    }

    public static Stream<Arguments> getTestConvertToDatabaseColumnParams() {
        return Stream.of(
                Arguments.of(Set.of(Role.ADMIN), List.of("ROLE_ADMIN")),
                Arguments.of(Set.of(Role.USER), List.of("ROLE_USER")),
                Arguments.of(Set.of(Role.ADMIN, Role.USER), List.of("ROLE_ADMIN,ROLE_USER", "ROLE_USER,ROLE_ADMIN")),
                Arguments.of(Set.of(), null),
                Arguments.of(null, null)
        );
    }

    /**
     * Test for {@link RoleListConverter#convertToEntityAttribute(String)}. Verifies that the method returns a list of
     * {@link Role}s from the input string.
     *
     * @param input The input string.
     * @param expected The expected output {@link Set} of {@link Set}.
     */
    @ParameterizedTest
    @MethodSource("getTestConvertToEntityAttributeParams")
    void testConvertToEntityAttribute(final String input, final Set<Role> expected) {
        assertThat(this.converter.convertToEntityAttribute(input), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestConvertToEntityAttributeParams() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", Set.of(Role.ADMIN)),
                Arguments.of("ROLE_USER", Set.of(Role.USER)),
                Arguments.of("ROLE_ADMIN,ROLE_USER", Set.of(Role.ADMIN, Role.USER)),
                Arguments.of("", new HashSet<>()),
                Arguments.of(null, new HashSet<>())
        );
    }

    @Test
    void testConvertToEntityAttribute() {
        final Set<Role> roles = this.converter.convertToEntityAttribute("ROLE_ADMIN");
        assertDoesNotThrow(() -> roles.add(Role.USER));
        final Set<Role> emptyRoles = this.converter.convertToEntityAttribute(null);
        assertDoesNotThrow(() -> emptyRoles.add(Role.USER));
    }
}
