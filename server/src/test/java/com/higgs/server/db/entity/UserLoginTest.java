package com.higgs.server.db.entity;

import com.higgs.server.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link UserLogin}
 */
class UserLoginTest {
    /**
     * Tests the method {@link UserLogin#onInsert()}. Verifies that it sets the created date.
     */
    @Test
    void testOnInsert() {
        final UserLogin userLogin = new UserLogin();
        userLogin.onInsert();
        assertNotNull(userLogin.getCreated());
    }

    /**
     * Tests the method {@link UserLogin#getAuthorities()}. Verifies that it returns the correct authorities for the
     * user's roles.
     */
    @Test
    void testGetAuthorities() {
        final UserLogin userLogin = new UserLogin();
        userLogin.setRoles(Set.of(Role.ADMIN));
        final Collection<? extends GrantedAuthority> actual = userLogin.getAuthorities();
        assertNotNull(actual);
        assertThat(actual.size(), is(equalTo(1)));
        assertThat(actual.iterator().next().getAuthority(), is(equalTo(Role.ADMIN.getRoleName())));
    }

    /**
     * Tests the method {@link UserLogin#isAccountNonExpired()}. Verifies that it returns the opposite of the value of
     * the {@link UserLogin#isExpired()} method.
     */
    @Test
    void testIsAccountNonExpired() {
        final UserLogin userLogin = new UserLogin();
        userLogin.setExpired(false);
        assertThat(userLogin.isAccountNonExpired(), is(equalTo(true)));
        userLogin.setExpired(true);
        assertThat(userLogin.isAccountNonExpired(), is(equalTo(false)));
    }

    /**
     * Tests the method {@link UserLogin#isAccountNonLocked()}. Verifies that it returns the opposite of the value of
     * the {@link UserLogin#isLocked()} method.
     */
    @Test
    void testIsAccountNonLocked() {
        final UserLogin userLogin = new UserLogin();
        userLogin.setLocked(false);
        assertThat(userLogin.isAccountNonLocked(), is(equalTo(true)));
        userLogin.setLocked(true);
        assertThat(userLogin.isAccountNonLocked(), is(equalTo(false)));
    }

    /**
     * Tests the method {@link UserLogin#isCredentialsNonExpired()}. Verifies that it returns the opposite of the value
     * of the {@link UserLogin#isCredentialsExpired()} method.
     */
    @Test
    void testIsCredentialsNonExpired() {
        final UserLogin userLogin = new UserLogin();
        userLogin.setCredentialsExpired(false);
        assertThat(userLogin.isCredentialsNonExpired(), is(equalTo(true)));
        userLogin.setCredentialsExpired(true);
        assertThat(userLogin.isCredentialsNonExpired(), is(equalTo(false)));
    }

    /**
     * Tests the method {@link UserLogin#addRole(Role)}. Verifies that it adds the role to the list of roles.
     */
    @Test
    void testAddRole() {
        final UserLogin userLogin = new UserLogin();
        final UserLogin actual = userLogin.addRole(Role.ADMIN);
        assertThat(userLogin.getRoles(), is(equalTo(Set.of(Role.ADMIN))));
        assertThat(actual, is(equalTo(userLogin)));
    }

    /**
     * Tests the method {@link UserLogin#addRole(Role)}. Verifies that it throws an exception if the role is null.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testAddRoleNullArg() {
        final UserLogin userLogin = new UserLogin();
        assertThrows(IllegalArgumentException.class, () -> userLogin.addRole(null));
    }
}
