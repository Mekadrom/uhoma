package com.higgs.server.web.rest;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.web.dto.AuthRequest;
import com.higgs.server.web.dto.AuthResult;
import com.higgs.server.web.dto.UserRegistrationRequest;
import com.higgs.server.web.rest.util.AuthSupplier;
import com.higgs.server.web.svc.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AuthenticationRest}.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationRestTest {
    @Mock
    private AuthenticationService authenticationService;

    private AuthenticationRest authenticationRest;

    @BeforeEach
    void setUp() {
        this.authenticationRest = new AuthenticationRest(this.authenticationService);
    }

    /**
     * Test method for {@link AuthenticationRest#login(AuthRequest)}. Verifies that the correct method calls are made
     * for valid input, and that the {@link ResponseEntity} that is returned has the correct response code, body, and
     * header.
     */
    @Test
    void testLogin() {
        final AuthRequest authRequest = mock(AuthRequest.class);
        final UserDetails userDetails = mock(UserDetails.class);
        final AuthResult authResult = mock(AuthResult.class);
        when(authRequest.getUsername()).thenReturn("user");
        when(authRequest.getPassword()).thenReturn("password");
        when(authResult.getUserDetails()).thenReturn(userDetails);
        when(authResult.getJwt()).thenReturn("token");
        when(this.authenticationService.getTokens(any(), any())).thenReturn(authResult);
        final ResponseEntity<UserDetails> actual = this.authenticationRest.login(authRequest);
        verify(this.authenticationService).getTokens(eq("user"), eq("password"));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(userDetails))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200))),
                () -> assertThat(actual.getHeaders().get(HttpHeaders.AUTHORIZATION), is(equalTo(List.of("token"))))
        );
    }

    /**
     * Test method for {@link AuthenticationRest#login(AuthRequest)}. Verifies that the method throws and
     * {@link IllegalArgumentException} when the {@link AuthRequest} is null.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testLoginNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.authenticationRest.login(null));
    }

    /**
     * Tests the method {@link AuthenticationRest#register(UserRegistrationRequest)} with valid input. Verifies that the
     * method calls the correct methods and returns the correct {@link ResponseEntity}, containing the correct response
     * code, body, and header.
     */
    @Test
    void testRegister() {
        final UserRegistrationRequest userRegistrationRequest = mock(UserRegistrationRequest.class);
        final UserDetails userDetails = mock(UserDetails.class);
        final AuthResult authResult = mock(AuthResult.class);
        when(userRegistrationRequest.getUsername()).thenReturn("user");
        when(userRegistrationRequest.getPassword()).thenReturn("password");
        when(authResult.getUserDetails()).thenReturn(userDetails);
        when(authResult.getJwt()).thenReturn("token");
        when(this.authenticationService.register(any(), any())).thenReturn(authResult);
        final ResponseEntity<UserDetails> actual = this.authenticationRest.register(userRegistrationRequest);
        verify(this.authenticationService).register(eq("user"), eq("password"));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(userDetails))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200))),
                () -> assertThat(actual.getHeaders().get(HttpHeaders.AUTHORIZATION), is(equalTo(List.of("token"))))
        );
    }

    /**
     * Tests the method {@link AuthenticationRest#register(UserRegistrationRequest)} with invalid input. Verifies that
     * the method throws an {@link IllegalArgumentException} when the {@link UserRegistrationRequest} is null.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testRegisterNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.authenticationRest.register(null));
    }

    /**
     * Tests the method {@link AuthenticationRest#userInit(String, String, AuthSupplier)} with invalid input. Verifies
     * that the method throws an {@link IllegalArgumentException} when any of the inputs are null.
     * @param username a test username
     * @param password a test password
     * @param authSupplier a test auth supplier
     */
    @ParameterizedTest
    @MethodSource("getTestUserInitNullArgsParams")
    void testUserInitNullArgs(final String username, final String password, final AuthSupplier authSupplier) {
        assertThrows(IllegalArgumentException.class, () -> this.authenticationRest.userInit(username, password, authSupplier));
    }

    public static Stream<Arguments> getTestUserInitNullArgsParams() {
        return Stream.of(
                Arguments.of(null, "password", (AuthSupplier) (u, p) -> null),
                Arguments.of("user", null, (AuthSupplier) (u, p) -> null),
                Arguments.of("user", "password", null)
        );
    }

    /**
     * Tests the method {@link AuthenticationRest#refreshUserView(Principal)} with valid input. Verifies that the
     * correct methods are called and the correct {@link ResponseEntity} is returned.
     */
    @Test
    void testRefreshUserView() {
        final Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("user");
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.authenticationService.performUserSearch(any())).thenReturn(userLogin);
        final ResponseEntity<UserDetails> actual = this.authenticationRest.refreshUserView(principal);
        verify(this.authenticationService).performUserSearch(eq("user"));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(userLogin))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link AuthenticationRest#refreshUserView(Principal)} with invalid input. Verifies that the
     * method throws an {@link IllegalArgumentException} when the {@link Principal} is null.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testRefreshUserViewNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.authenticationRest.refreshUserView(null));
    }
}
