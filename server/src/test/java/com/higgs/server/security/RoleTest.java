package com.higgs.server.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link Role}.
 */
class RoleTest {
    /**
     * Tests {@link Role#getByRoleName(String)} with valid role names.
     *
     * @param input    a valid role name
     * @param expected the expected {@link Role}
     */
    @ParameterizedTest
    @MethodSource("getTestGetByRoleNameParams")
    void testGetByRoleName(final String input, final Role expected) {
        assertThat(Role.getByRoleName(input), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestGetByRoleNameParams() {
        return Stream.of(
                Arguments.of("ROLE_ADMIN", Role.ADMIN),
                Arguments.of("ROLE_USER", Role.USER)
        );
    }

    /**
     * Tests {@link Role#getByRoleName(String)} with invalid role names/inputs.
     */
    @Test
    void testGetByRoleNameInvalid() {
        assertAll(
                () -> assertNull(Role.getByRoleName(null)),
                () -> assertNull(Role.getByRoleName("")),
                () -> assertNull(Role.getByRoleName("ROLE_DOESNT_EXIST"))
        );
    }
}