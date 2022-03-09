package com.higgs.server.security;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JwtTokenFilter}.
 */
@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {
    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private UserLoginRepository userLoginRepository;

    private JwtTokenFilter jwtTokenFilter;

    @BeforeEach
    void setUp() {
        this.jwtTokenFilter = new JwtTokenFilter(this.jwtTokenUtils, this.userLoginRepository);
    }

    /**
     * Tests for {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}. Only
     * verifies that it delegates to
     * {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain, SecurityContext)}.
     */
    @Test
    @SneakyThrows
    void testDoFilterInternalNoSecurityContext() {
        final JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(this.jwtTokenUtils, this.userLoginRepository);
        final JwtTokenFilter jwtTokenFilterSpy = spy(jwtTokenFilter);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        jwtTokenFilterSpy.doFilterInternal(request, response, filterChain);
        verify(jwtTokenFilterSpy, times(1)).doFilterInternal(eq(request), eq(response), eq(filterChain), any(SecurityContext.class));
    }

    /**
     * Tests for {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)} with
     * invalid inputs. Verifies that it throws an {@link IllegalArgumentException} for these cases.
     * @param request a potentially invalid {@link HttpServletRequest}
     * @param response a potentially invalid {@link HttpServletResponse}
     * @param filterChain a potentially invalid {@link FilterChain}
     */
    @ParameterizedTest
    @MethodSource("getTestDoFilterInternalNoSecurityContextNullArgsParams")
    void testDoFilterInternalNoSecurityContextNullArgs(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) {
        assertThrows(IllegalArgumentException.class, () -> this.jwtTokenFilter.doFilterInternal(request, response, filterChain));
    }

    public static Stream<Arguments> getTestDoFilterInternalNoSecurityContextNullArgsParams() {
        return Stream.of(
                Arguments.of(null, mock(HttpServletResponse.class), mock(FilterChain.class)),
                Arguments.of(mock(HttpServletRequest.class), null, mock(FilterChain.class)),
                Arguments.of(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null)
        );
    }

    /**
     * Tests for
     * {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain, SecurityContext)}.
     * Verifies the method calls and some arguments to method calls coming out of this method. This method sets the
     * {@link Authentication} in the {@link SecurityContext} and verifies that the it is set by this method.
     */
    @Test
    @SneakyThrows
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testDoFilterInternal() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final SecurityContext securityContext = mock(SecurityContext.class);
        final UserLogin userLogin = mock(UserLogin.class);
        final HttpSession session = mock(HttpSession.class);
        when(userLogin.getUsername()).thenReturn("user");
        when(userLogin.getAuthorities()).thenReturn((Collection) Collections.singletonList(mock(GrantedAuthority.class)));
        when(this.jwtTokenUtils.parseAndValidateToken(any(), any())).thenReturn((Optional) Optional.of(userLogin));
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("requestUrl");
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(session.getId()).thenReturn("sessionId");
        final ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        this.jwtTokenFilter.doFilterInternal(request, response, filterChain, securityContext);
        verify(request, times(1)).getHeader(eq(HttpHeaders.AUTHORIZATION));
        verify(securityContext, times(1)).setAuthentication(captor.capture());
        verify(userLogin, times(1)).getUsername();
        verify(userLogin, times(1)).getAuthorities();
        verify(filterChain, times(1)).doFilter(request, response);
        assertAll(
                () -> assertThat(captor.getValue().getName(), is(equalTo("user"))),
                () -> assertNull(captor.getValue().getCredentials()),
                () -> assertThat(captor.getValue().getAuthorities().size(), is(equalTo(1))),
                () -> assertThat(captor.getValue().getDetails().getClass(), is(equalTo(WebAuthenticationDetails.class))),
                () -> assertThat(((WebAuthenticationDetails) captor.getValue().getDetails()).getRemoteAddress(), is(equalTo("requestUrl"))),
                () -> assertThat(((WebAuthenticationDetails) captor.getValue().getDetails()).getSessionId(), is(equalTo("sessionId")))
        );
    }

    /**
     * Tests for
     * {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain, SecurityContext)}.
     * Verifies the method calls and some arguments to method calls coming out of this method when the
     * {@link Authentication} on the {@link SecurityContext} is not null. In this case, most of the method should be
     * skipped.
     */
    @Test
    @SneakyThrows
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void testDoFilterInternalNonNullAuthentication() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final SecurityContext securityContext = mock(SecurityContext.class);
        final UserLogin userLogin = mock(UserLogin.class);
        final Authentication authentication = mock(Authentication.class);
        when(this.jwtTokenUtils.parseAndValidateToken(any(), any())).thenReturn((Optional) Optional.of(userLogin));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        this.jwtTokenFilter.doFilterInternal(request, response, filterChain, securityContext);
        verify(request, times(1)).getHeader(eq(HttpHeaders.AUTHORIZATION));
        verify(securityContext, times(0)).setAuthentication(any());
        verify(userLogin, times(0)).getUsername();
        verify(userLogin, times(0)).getAuthorities();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests for
     * {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain, SecurityContext)}.
     * Verifies the method calls and some arguments to method calls coming out of this method when the username on the
     * request can't be found. In this case, most of the method should be skipped.
     */
    @Test
    @SneakyThrows
    void testDoFilterInternalUserNotFound() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain filterChain = mock(FilterChain.class);
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(this.jwtTokenUtils.parseAndValidateToken(any(), any())).thenReturn(Optional.empty());
        this.jwtTokenFilter.doFilterInternal(request, response, filterChain, securityContext);
        verify(request, times(1)).getHeader(eq(HttpHeaders.AUTHORIZATION));
        verify(securityContext, times(0)).setAuthentication(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests for
     * {@link JwtTokenFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain, SecurityContext)}.
     * With invalid/null arguments. In all cases, an {@link IllegalArgumentException} should be thrown.
     * @param request a potentially null {@link HttpServletRequest}
     * @param response a potentially null {@link HttpServletResponse}
     * @param filterChain a potentially null {@link FilterChain}
     */
    @ParameterizedTest
    @MethodSource("getTestDoFilterInternalNullArgsParams")
    void testDoFilterInternalNullArgs(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) {
        assertThrows(IllegalArgumentException.class, () -> this.jwtTokenFilter.doFilterInternal(request, response, filterChain));
    }

    public static Stream<Arguments> getTestDoFilterInternalNullArgsParams() {
        return Stream.of(
                Arguments.of(null, mock(HttpServletResponse.class), mock(FilterChain.class)),
                Arguments.of(mock(HttpServletRequest.class), null, mock(FilterChain.class)),
                Arguments.of(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null)
        );
    }
}
