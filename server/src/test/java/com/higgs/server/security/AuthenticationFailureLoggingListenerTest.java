package com.higgs.server.security;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AuthenticationFailureLoggingListener.class)
class AuthenticationFailureLoggingListenerTest {
    @Autowired
    private AuthenticationFailureLoggingListener authenticationFailureLoggingListener;

    public static Stream<Arguments> getTestOnApplicationEventParams() {
        return Stream.of(
                Arguments.of(mock(AuthenticationSuccessEvent.class), false),
                Arguments.of(mock(InteractiveAuthenticationSuccessEvent.class), false),
                Arguments.of(mock(AbstractAuthenticationEvent.class), true),
                Arguments.of(mock(AuthenticationFailureBadCredentialsEvent.class), true)
        );
    }

    /**
     * Tests to make sure the logger only logs the name of the authentication when the authentication event is not some
     * type of success event.
     * @param testEvent the mocked {@link AbstractAuthenticationEvent}
     * @param logs whether the authentication name is logged
     */
    @ParameterizedTest
    @MethodSource("getTestOnApplicationEventParams")
    void testOnApplicationEvent(final AbstractAuthenticationEvent testEvent, final boolean logs) {
        final Authentication mockAuth = mock(Authentication.class);
        when(testEvent.getAuthentication()).thenReturn(mockAuth);
        this.authenticationFailureLoggingListener.onApplicationEvent(testEvent);
        verify(mockAuth, times(logs ? 1 : 0)).getName();
    }

    @NullSource
    @ParameterizedTest
    void testOnApplicationEventInvalidArgs(final AbstractAuthenticationEvent nullValue) {
        assertThrows(IllegalArgumentException.class, () -> this.authenticationFailureLoggingListener.onApplicationEvent(nullValue));
    }
}
