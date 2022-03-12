package com.higgs.server.security;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.web.svc.UserLoginService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SecurityConfig}.
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {
    @Mock
    private JwtTokenFilter jwtTokenFilter;

    @Mock
    private UserLoginService userLoginService;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        this.securityConfig = new SecurityConfig(this.jwtTokenFilter, this.userLoginService);
    }

    /**
     * Tests that the {@link SecurityConfig#configure(AuthenticationManagerBuilder)} method correctly configures the
     * {@link AuthenticationManagerBuilder} and verifies the method calls out of this method. Also verifies the
     * functionality of the returned {@link AuthenticationManagerBuilder}.
     */
    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testConfigureAuthenticationManagerBuilder() {
        final AuthenticationManagerBuilder authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
        when(this.userLoginService.findByUsername("user")).thenReturn(Optional.of(mock(UserLogin.class)));
        final DaoAuthenticationConfigurer<AuthenticationManagerBuilder, UserDetailsService> daoAuthenticationConfigurer = mock(DaoAuthenticationConfigurer.class);
        when(authenticationManagerBuilder.userDetailsService(any(UserDetailsService.class))).thenReturn(daoAuthenticationConfigurer);
        this.securityConfig.configure(authenticationManagerBuilder);
        final ArgumentCaptor<UserDetailsService> captor = ArgumentCaptor.forClass(UserDetailsService.class);
        verify(authenticationManagerBuilder, times(1)).userDetailsService(captor.capture());
        verify(daoAuthenticationConfigurer, times(1)).passwordEncoder(any(BCryptPasswordEncoder.class));
        final UserDetailsService actual = captor.getValue();
        assertAll(
                () -> assertDoesNotThrow(() -> actual.loadUserByUsername("user")),
                () -> assertNotNull(actual.loadUserByUsername("user")),
                () -> assertThrows(UsernameNotFoundException.class, () -> actual.loadUserByUsername("username"))
        );
    }

    /**
     * Unfortunately, the {@link SecurityConfig#configure(HttpSecurity)} method is not easily testable.
     * {@link HttpSecurity} is a final class without a public no-arg constructor, meaning it can't be mocked or spied.
     * Therefore, this test simply verifies that the method doesn't throw any exceptions when the required inputs are
     * given.
     */
    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testConfigureHttpSecurity() {
        final ObjectPostProcessor<Object> objectPostProcessor = mock(ObjectPostProcessor.class);
        final AuthenticationManagerBuilder authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
        final HttpSecurity httpSecurity = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, Collections.emptyMap());
        assertDoesNotThrow(() -> this.securityConfig.configure(httpSecurity));
    }

    /**
     * Tests that the
     * {@link SecurityConfig#authEntryPoint(HttpServletRequest, HttpServletResponse, AuthenticationException)}
     * sets the status code to {@link HttpServletResponse#SC_UNAUTHORIZED} and does nothing else.
     */
    @Test
    void testAuthEntryPoint() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final AuthenticationException exception = mock(AuthenticationException.class);
        this.securityConfig.authEntryPoint(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoMoreInteractions(request, response, exception);
    }

    /**
     * Tests that the {@link SecurityConfig#corsConfigurer()} configures an input {@link CorsRegistry} object with the
     * correct method calls.
     */
    @Test
    void testCorsConfigurer() {
        final CorsRegistry corsRegistry = mock(CorsRegistry.class);
        final CorsRegistration corsRegistration = mock(CorsRegistration.class);
        final WebMvcConfigurer webMvcConfigurer = this.securityConfig.corsConfigurer();
        when(corsRegistry.addMapping(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOriginPatterns(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(any())).thenReturn(corsRegistration);
        when(corsRegistration.exposedHeaders(any())).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        webMvcConfigurer.addCorsMappings(corsRegistry);
        verify(corsRegistry, times(1)).addMapping("/**");
        verify(corsRegistration, times(1)).allowedOriginPatterns("*");
        verify(corsRegistration, times(1)).allowedMethods(any());
        verify(corsRegistration, times(1)).allowedHeaders(any());
        verify(corsRegistration, times(1)).exposedHeaders(HttpHeaders.AUTHORIZATION);
        verify(corsRegistration, times(1)).allowCredentials(true);
    }

    /**
     * Tests that the {@link SecurityConfig#passwordEncoder()} method returns a
     * {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder} object.
     */
    @Test
    void testPasswordEncoder() {
        final PasswordEncoder actual = this.securityConfig.passwordEncoder();
        assertThat(actual.getClass(), is(equalTo(BCryptPasswordEncoder.class)));
    }

    /**
     * Tests that the {@link SecurityConfig#authenticationManagerBean()} method does not throw any exceptions, and that
     * it returns a {@link AuthenticationManager} object.
     */
    @Test
    @SneakyThrows
    void testAuthenticationManagerBean() {
        ReflectionTestUtils.setField(this.securityConfig, "authenticationBuilder", mock(AuthenticationManagerBuilder.class));
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeanNamesForType(any(Class.class))).thenReturn(new String[]{""});
        ReflectionTestUtils.setField(this.securityConfig, "context", applicationContext);
        assertDoesNotThrow(() -> assertNotNull(this.securityConfig.authenticationManagerBean()));
    }
}
