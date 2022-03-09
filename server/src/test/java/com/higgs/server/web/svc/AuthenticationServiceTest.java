package com.higgs.server.web.svc;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.security.JwtTokenUtils;
import com.higgs.server.web.dto.AuthResult;
import com.higgs.server.web.svc.util.UserAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AuthenticationService}.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserLoginService userLoginService;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        this.authenticationService = new AuthenticationService(this.authenticationManager, this.jwtTokenUtils, this.passwordEncoder, this.userLoginService);
    }

    /**
     * Tests the method {@link AuthenticationService#getTokens(String, String)} with valid input, and the user
     * auths successfully and the token generated is not blank.
     */
    @Test
    void testGetTokensAuthSuccessNonBlankToken() {
        final Authentication authentication = mock(Authentication.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userLogin);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        when(this.jwtTokenUtils.generateToken(any())).thenReturn("token");
        when(userLogin.getUsername()).thenReturn("user");
        when(this.userLoginService.save(any())).thenReturn(userLogin);
        final AuthResult actual = this.authenticationService.getTokens("user", "password");
        verify(this.jwtTokenUtils, times(1)).generateToken(eq(userLogin));
        assertAll(
                () -> assertThat(actual.getJwt(), is(equalTo("token"))),
                () -> assertThat(actual.getUserDetails(), is(equalTo(userLogin)))
        );
    }

    /**
     * Tests the method {@link AuthenticationService#getTokens(String, String)} with valid input, and the user somehow
     * auths successfully but the user cannot be matched anymore.
     */
    @Test
    void testGetTokensAuthSuccessUserNotMatched() {
        final Authentication authentication = mock(Authentication.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userLogin);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> this.authenticationService.getTokens("user", "password"));
    }

    /**
     * Tests the method {@link AuthenticationService#getTokens(String, String)} with valid input, user matches but the
     * JWT token generation service fails for some reason and generates a null or blank token.
     */
    @Test
    void testGetTokensAuthSuccessBlankToken() {
        final Authentication authentication = mock(Authentication.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userLogin);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        when(this.jwtTokenUtils.generateToken(any())).thenReturn("");
        when(userLogin.getUsername()).thenReturn("user");
        assertThrows(JwtException.class, () -> this.authenticationService.getTokens("user", "password"));
    }

    /**
     * Tests the method {@link AuthenticationService#validate(String)} to make sure it delegates to
     * {@link JwtTokenUtils#parseAndValidateToken(String, Function)}
     */
    @Test
    void testValidate() {
        this.authenticationService.validate("token");
        verify(this.jwtTokenUtils, times(1)).parseAndValidateToken(eq("token"), any());
    }

    /**
     * Tests the method {@link AuthenticationService#validate(String)} with valid input, where the user does not already
     * exist.
     */
    @Test
    void testRegisterUserDoesntExist() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.empty());
        when(this.userLoginService.save(any())).thenReturn(userLogin);
        when(this.jwtTokenUtils.generateToken(any())).thenReturn("token");
        this.authenticationService.register("user", "password");
        verify(this.userLoginService, times(1)).save(any());
    }

    /**
     * Tests the method {@link AuthenticationService#register(String, String)} with invalid input, where the user
     * already exists.
     */
    @Test
    void testRegisterUserExists() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        assertThrows(UserAlreadyExistsException.class, () -> this.authenticationService.register("user", "password"));
    }

    /**
     * Tests the method {@link AuthenticationService#performUserSearch(String)} with valid input, where the user exists.
     */
    @Test
    void testPerformUserSearchMatch() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.of(userLogin));
        assertThat(this.authenticationService.performUserSearch("user"), is(equalTo(userLogin)));
    }

    /**
     * Tests the method {@link AuthenticationService#performUserSearch(String)} with invalid input, where the user does
     * not exist.
     */
    @Test
    void testPerformUserSearchNoMatch() {
        when(this.userLoginService.findByUsername(any())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> this.authenticationService.performUserSearch("user"));
    }

    /**
     * Tests the method {@link AuthenticationService#performUserSearch(String)} with invalid input, where the user
     * filter criteria is null.
     */
    @Test
    void testPerformUserSearchNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.authenticationService.performUserSearch(null));
    }
}
