package com.higgs.server.security;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import com.higgs.server.scv.CheckFailureException;
import com.higgs.server.web.svc.HomeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JwtTokenUtils}.
 */
@ExtendWith(MockitoExtension.class)
class JwtTokenUtilsTest {
    @Mock
    private HomeService homeService;

    @Mock
    private UserLoginRepository userLoginRepository;

    private JwtTokenUtils jwtTokenUtils;

    /**
     * iss: hams
     * iat: 1646538554000
     * exp: 1152921298789464347
     * sub: admin
     * signing_key: devsigningkey
     */
    private static final String TEST_TOKEN_VALID = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzIjoiaGFtcyIsImV4cCI6IjExNTI5MjEyOTg3ODk0NjQzNDciLCJpYXQiOiIxNjQ2NTM4NTU0MDAwIn0.TYbpSoW5YejYUEJiNRoDxjAE1pGioZV7ceiM0XwlfIQ";

    /**
     * iss: hams
     * iat: 1646538554000
     * exp: 1152921298789464347
     */
    private static final String TEST_TOKEN_NO_SUB = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJoYW1zIiwiZXhwIjoiMTE1MjkyMTI5ODc4OTQ2NDM0NyIsImlhdCI6IjE2NDY1Mzg1NTQwMDAifQ.RicOika0Vw1IuYqf9xuIzquxKTcPQzIGaId9Pj-UENo";

    /**
     * iss: hams
     * iat: 1646538554000
     * sub: user
     */
    private static final String TEST_TOKEN_EXPIRED = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzIjoiaGFtcyIsImlhdCI6IjE2NDY1MzE1NTQwMDAifQ.d2rKkGhJ1sQZRVPZR4VLJZ-KH6eogSMQGQGoKv0tVuI";

    @BeforeEach
    void setUp() {
        // sets the signing key for the context of the test
        System.setProperty("security.auth.jwt.signing-key", "devsigningkey");
        this.jwtTokenUtils = new JwtTokenUtils(this.homeService);
    }

    @AfterEach
    void tearDown() {
        // unset the signing key to avoid changing state for other tests
        System.clearProperty("security.auth.jwt.signing-key");
    }

    /**
     * Tests for {@link JwtTokenUtils#validateToken(String, UserDetails)}, verifies that it returns true when given a
     * valid token. Also implicitly tests {@link JwtTokenUtils#removePrefix(String)}.
     */
    @Test
    void testValidateToken() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(userLogin.getUsername()).thenReturn("user");
        final boolean actual = this.jwtTokenUtils.validateToken(this.jwtTokenUtils.removePrefix(JwtTokenUtilsTest.TEST_TOKEN_VALID), userLogin);
        assertThat(actual, is(equalTo(true)));
    }

    /**
     * Tests for {@link JwtTokenUtils#validateToken(String, UserDetails)}, verifies that it returns false when given an
     * invalid token. In this case, the token sub claim is incorrect.
     */
    @Test
    void testValidateTokenWrongUsername() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(userLogin.getUsername()).thenReturn("admin");
        final boolean actual = this.jwtTokenUtils.validateToken(this.jwtTokenUtils.removePrefix(JwtTokenUtilsTest.TEST_TOKEN_VALID), userLogin);
        assertThat(actual, is(equalTo(false)));
    }

    /**
     * Tests for {@link JwtTokenUtils#validateToken(String, UserDetails)}, verifies that it returns false when given an
     * invalid token. In this case, the token either has no expiration date or it is already expired.
     */
    @Test
    void testValidateTokenTokenExpired() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(userLogin.getUsername()).thenReturn("user");
        final boolean actual = this.jwtTokenUtils.validateToken(this.jwtTokenUtils.removePrefix(JwtTokenUtilsTest.TEST_TOKEN_EXPIRED), userLogin);
        assertThat(actual, is(equalTo(false)));
    }

    /**
     * Tests for {@link JwtTokenUtils#validateToken(String, UserDetails)}, verifies that it throws an
     * {@link IllegalArgumentException} when given a null {@link UserDetails}.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testValidateTokenNullUserDetails() {
        assertThrows(IllegalArgumentException.class, () -> this.jwtTokenUtils.validateToken(this.jwtTokenUtils.removePrefix(JwtTokenUtilsTest.TEST_TOKEN_VALID), null));
    }

    /**
     * Tests for {@link JwtTokenUtils#generateToken(UserLogin)}, verifies that it throws a {@link RuntimeException} when
     * the signing key is not set.
     */
    @Test
    void testGenerateTokenNoKey() {
        System.clearProperty("security.auth.jwt.signing-key");
        final UserLogin userLogin = mock(UserLogin.class);
        assertThrows(CheckFailureException.class, () -> this.jwtTokenUtils.generateToken(userLogin));
    }

    /**
     * Tests for {@link JwtTokenUtils#generateToken(UserLogin)}, verifies that it generates any JWT when given valid
     * input.
     */
    @Test
    void testGenerateToken() {
        final UserLogin userLogin = mock(UserLogin.class);
        final Home home = mock(Home.class);
        when(userLogin.getUsername()).thenReturn("user");
        when(home.getHomeSeq()).thenReturn(1L);
        when(this.homeService.getHomesForUser(any())).thenReturn(List.of(home));
        final String actual = this.jwtTokenUtils.generateToken(userLogin);
        assertThat(actual, containsString("eyJ"));
    }

    /**
     * Tests for {@link JwtTokenUtils#ensureSigningKey()}, verifies that it doesn't throw an exception when the signing
     * key is set.
     */
    @Test
    void testEnsureSigningKey() {
        assertDoesNotThrow(() -> this.jwtTokenUtils.ensureSigningKey());
    }

    /**
     * Tests for {@link JwtTokenUtils#ensureSigningKey()}, verifies that it throws an exception when the signing key is
     * not set.
     */
    @Test
    void testEnsureSigningKeyNoKey() {
        System.clearProperty("security.auth.jwt.signing-key");
        assertThrows(CheckFailureException.class, () -> this.jwtTokenUtils.ensureSigningKey());
    }

    /**
     * Tests for {@link JwtTokenUtils#parseAndValidateToken(String, Function)}, verifies that is returns the expected
     * {@link UserDetails} when given a valid token.
     */
    @Test
    void testParseAndValidateToken() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.userLoginRepository.findByUsername("user")).thenReturn(Optional.of(userLogin));
        when(userLogin.getUsername()).thenReturn("user");
        final Optional<? extends UserDetails> actual = this.jwtTokenUtils.parseAndValidateToken(JwtTokenUtilsTest.TEST_TOKEN_VALID, this.userLoginRepository::findByUsername);
        verify(this.userLoginRepository, times(1)).findByUsername(eq("user"));
        assertAll(
                () -> assertThat(actual.isPresent(), is(equalTo(true))),
                () -> assertThat(actual.get().getUsername(), is(equalTo("user")))
        );
    }

    /**
     * Tests for {@link JwtTokenUtils#parseAndValidateToken(String, Function)}, verifies that is returns the expected
     * {@link UserDetails} when given a token with the wrong username on it.
     */
    @Test
    void testParseAndValidateTokenNonMatchingUsernameAndTokenSub() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.userLoginRepository.findByUsername("user")).thenReturn(Optional.of(userLogin));
        when(userLogin.getUsername()).thenReturn("username");
        final Optional<? extends UserDetails> actual = this.jwtTokenUtils.parseAndValidateToken(JwtTokenUtilsTest.TEST_TOKEN_VALID, this.userLoginRepository::findByUsername);
        verify(this.userLoginRepository, times(1)).findByUsername(eq("user"));
        assertThat(actual.isPresent(), is(equalTo(false)));
    }

    /**
     * Tests for {@link JwtTokenUtils#parseAndValidateToken(String, Function)}, verifies that it returns an empty
     * {@link Optional} when given an invalid token. In this case, the token is invalid because it is missing a
     * username.
     */
    @Test
    void testParseAndValidateTokenNoUsernameOnToken() {
        final Optional<? extends UserDetails> actual = this.jwtTokenUtils.parseAndValidateToken(JwtTokenUtilsTest.TEST_TOKEN_NO_SUB, this.userLoginRepository::findByUsername);
        verify(this.userLoginRepository, times(0)).findByUsername(eq("user"));
        assertThat(actual.isPresent(), is(equalTo(false)));
    }

    /**
     * Tests for {@link JwtTokenUtils#parseAndValidateToken(String, Function)}, verifies that it returns an empty
     * {@link Optional} when given an invalid token. In this case, the token is null or blank.
     */
    @Test
    void testParseAndValidateTokenBlankToken() {
        final Optional<? extends UserDetails> actual = this.jwtTokenUtils.parseAndValidateToken("Bearer ", this.userLoginRepository::findByUsername);
        verify(this.userLoginRepository, times(0)).findByUsername(eq("user"));
        assertThat(actual.isPresent(), is(equalTo(false)));
    }

    /**
     * Tests for {@link JwtTokenUtils#removePrefix(String)}, verifies that it returns the expected output for different
     * valid and invalid inputs.
     *
     * @param tokenOrBearer the token or bearer string to test
     * @param expected the expected output
     */
    @ParameterizedTest
    @MethodSource("getTestRemovePrefixParams")
    void testRemovePrefix(final String tokenOrBearer, final String expected) {
        assertThat(this.jwtTokenUtils.removePrefix(tokenOrBearer), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestRemovePrefixParams() {
        return Stream.of(
                Arguments.of("Bearer token", "token"),
                Arguments.of("token", "token"),
                Arguments.of("", ""),
                Arguments.of("Bearer ", ""),
                Arguments.of("Bearer", "Bearer"),
                Arguments.of(null, null)
        );
    }

    /**
     * Tests for {@link JwtTokenUtils#getExpirationDate(long, long)}, verifies that it returns the expected output for different
     * valid inputs.
     *
     * @param startTime the start time to use for the token (probably always System.currentTimeMillis())
     * @param validitySeconds the validity seconds to use for the token
     * @param expected the expected output expiration date
     */
    @ParameterizedTest
    @MethodSource("getTestGetExpirationDateParams")
    void testGetExpirationDate(final long startTime, final long validitySeconds, final long expected) {
        assertThat(this.jwtTokenUtils.getExpirationDate(startTime, validitySeconds), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestGetExpirationDateParams() {
        return Stream.of(
                Arguments.of(0, 0, 0),
                Arguments.of(0, 1, 1000),
                Arguments.of(1000, 1, 2000)
        );
    }

    /**
     * Tests for {@link JwtTokenUtils#isTokenExpired(String)}, verifies that it determines whether the expiration date
     * on the token is before the current time.
     *
     * @param dateFromToken the expiration date on the token
     * @param expected the expected output
     */
    @ParameterizedTest
    @MethodSource("getTestIsTokenExpiredParams")
    void testIsTokenExpired(final Date dateFromToken, final boolean expected) {
        final JwtTokenUtils jwtTokenUtils = mock(JwtTokenUtils.class);
        when(jwtTokenUtils.getExpirationDateFromToken(any())).thenReturn(dateFromToken);
        when(jwtTokenUtils.isTokenExpired(any())).thenCallRealMethod();
        assertThat(jwtTokenUtils.isTokenExpired(null), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestIsTokenExpiredParams() {
        return Stream.of(
                Arguments.of(new Date(0), true),
                Arguments.of(new Date(new Date().getTime() + 3600000), false)
        );
    }

    @Test
    void testIsTokenExpiredNull() {
        assertThrows(IllegalArgumentException.class, () -> this.jwtTokenUtils.isTokenExpired(null));
    }
}
