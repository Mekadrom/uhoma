package com.higgs.server.security;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link NoSecurityConfig}.
 */
class NoSecurityConfigTest {
    private NoSecurityConfig noSecurityConfig;

    @BeforeEach
    void setUp() {
        this.noSecurityConfig = new NoSecurityConfig();
    }

    /**
     * Tests that the {@link NoSecurityConfig#configure(HttpSecurity)} method does not throw an exception. Can't test
     * further because {@link HttpSecurity} is final and can't be mocked or spied.
     */
    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testConfigureHttpSecurity() {
        // this test does nothing; can't mock or spy HttpSecurity to verify calls. todo: figure out a way to test this properly
        final ObjectPostProcessor<Object> objectPostProcessor = mock(ObjectPostProcessor.class);
        final AuthenticationManagerBuilder authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
        final HttpSecurity httpSecurity = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, Collections.emptyMap());
        assertDoesNotThrow(() -> this.noSecurityConfig.configure(httpSecurity));
    }
}