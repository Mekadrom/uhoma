package com.higgs.server.web.socket;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.security.JwtTokenUtils;
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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link WebSocketAuthInterceptor}.
 */
@ExtendWith(MockitoExtension.class)
class WebSocketAuthInterceptorTest {
    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtTokenUtils jwtTokenUtil;

    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @BeforeEach
    void setUp() {
        this.webSocketAuthInterceptor = new WebSocketAuthInterceptor(this.authenticationService, this.jwtTokenUtil);
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#preSend(StompHeaderAccessor, Message)} returns the input message.
     */
    @Test
    void testPreSendWithMessageChannel() {
        final Message<?> message = mock(Message.class);
        when(message.getHeaders()).thenReturn(null);
        assertThat(this.webSocketAuthInterceptor.preSend(message, mock(MessageChannel.class)), is(equalTo(message)));
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#preSend(StompHeaderAccessor, Message)} and validates the method
     * calls out of it when the input is valid (CONNECT or SUBSCRIBE event).
     */
    @Test
    void testPreSendConnectEvent() {
        final StompHeaderAccessor accessor = mock(StompHeaderAccessor.class);
        final Message<?> message = mock(Message.class);
        final UserLogin userLogin = mock(UserLogin.class);
        when(accessor.getCommand()).thenReturn(StompCommand.CONNECT);
        when(accessor.getNativeHeader(any())).thenReturn(Collections.singletonList("token"));
        when(this.jwtTokenUtil.removePrefix(any())).thenReturn("token");
        when(this.authenticationService.validate(any())).thenReturn(true);
        when(this.jwtTokenUtil.getUsernameFromToken(any())).thenReturn("user");
        when(this.authenticationService.performUserSearch(any())).thenReturn(userLogin);

        assertThat(this.webSocketAuthInterceptor.preSend(accessor, message), is(equalTo(message)));
        verify(accessor, times(1)).setUser(eq(new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList())));
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#preSend(StompHeaderAccessor, Message)} and validates the method
     * calls out of it when the input is invalid (any other event type than CONNECT or SUBSCRIBE).
     */
    @Test
    void testPreSendNonConnectSubscribeEvent() {
        final StompHeaderAccessor accessor = mock(StompHeaderAccessor.class);
        final Message<?> message = mock(Message.class);
        when(accessor.getCommand()).thenReturn(StompCommand.DISCONNECT);

        assertThat(this.webSocketAuthInterceptor.preSend(accessor, message), is(equalTo(message)));
        verify(accessor, times(0)).setUser(eq(new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList())));
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#preSend(StompHeaderAccessor, Message)} and validates the method
     * just returns the input message and doesn't throw any exceptions.
     */
    @Test
    void testPreSendNullAccessor() {
        final Message<?> message = mock(Message.class);
        assertThat(this.webSocketAuthInterceptor.preSend(null, message), is(equalTo(message)));
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#preSend(StompHeaderAccessor, Message)} and validates the method
     * correctly determines whether the connect event is {@link StompCommand#CONNECT} or {@link StompCommand#DISCONNECT}
     * @param stompCommand the input {@link StompCommand} to test with
     * @param expected whether the event is expected to be determined to be a connect event
     */
    @ParameterizedTest
    @MethodSource("getTestIsConnectInitEventParams")
    void testIsConnectInitEvent(final StompCommand stompCommand, final boolean expected) {
        assertThat(this.webSocketAuthInterceptor.isConnectInitEvent(stompCommand), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestIsConnectInitEventParams() {
        return Stream.of(
                Arguments.of(StompCommand.CONNECT, true),
                Arguments.of(StompCommand.CONNECTED, false),
                Arguments.of(StompCommand.DISCONNECT, false),
                Arguments.of(StompCommand.ERROR, false),
                Arguments.of(StompCommand.MESSAGE, false),
                Arguments.of(StompCommand.RECEIPT, false),
                Arguments.of(StompCommand.SEND, false),
                Arguments.of(StompCommand.SUBSCRIBE, true),
                Arguments.of(StompCommand.UNSUBSCRIBE, false),
                Arguments.of(null, false)
        );
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#attachUserSession(String)} and verifies the method calls out of
     * it when the input is valid (valid token).
     */
    @Test
    void testAttachUserSessionTokenValid() {
        final UserLogin userLogin = mock(UserLogin.class);
        when(this.jwtTokenUtil.getUsernameFromToken(any())).thenReturn("user");
        when(this.authenticationService.validate(any())).thenReturn(true);
        when(this.authenticationService.performUserSearch(any())).thenReturn(userLogin);
        when(userLogin.getAuthorities()).thenReturn(new ArrayList<>());

        final UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken("user", "password", userLogin.getAuthorities());
        final UsernamePasswordAuthenticationToken actual = (UsernamePasswordAuthenticationToken) this.webSocketAuthInterceptor.attachUserSession("Bearer token");
        verify(this.authenticationService, times(1)).performUserSearch("user");
        assertAll(
                () -> assertThat(actual.getName(), is(equalTo(upaToken.getName()))),
                () -> assertThat(actual.getAuthorities(), is(equalTo(new ArrayList<>())))
        );
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#attachUserSession(String)} and verifies the method calls out of
     * it when the input is invalid (invalid token).
     */
    @Test
    void testAttachUserSessionTokenInvalid() {
        when(this.authenticationService.validate(any())).thenReturn(false);
        final UsernamePasswordAuthenticationToken actual = (UsernamePasswordAuthenticationToken) this.webSocketAuthInterceptor.attachUserSession("Bearer token");
        verify(this.jwtTokenUtil, times(0)).getUsernameFromToken("token");
        verify(this.authenticationService, times(0)).performUserSearch("user");
        assertNull(actual);
    }

    /**
     * Test the method {@link WebSocketAuthInterceptor#extractTokenFromMessageChannelHeaders(StompHeaderAccessor)}
     * and verifies the method calls out of it when the input is valid (valid token).
     */
    @Test
    void testExtractTokenFromMessageChannelHeaders() {
        final StompHeaderAccessor accessor = mock(StompHeaderAccessor.class);
        when(accessor.getNativeHeader(HttpHeaders.AUTHORIZATION)).thenReturn(Collections.singletonList("Bearer token"));
        when(this.jwtTokenUtil.removePrefix(any())).thenReturn("token");
        final String actual = this.webSocketAuthInterceptor.extractTokenFromMessageChannelHeaders(accessor);
        verify(this.jwtTokenUtil, times(1)).removePrefix(any());
        assertThat(actual, is(equalTo("token")));
    }

    /**
     * Test that {@link WebSocketAuthInterceptor#extractTokenFromMessageChannelHeaders(StompHeaderAccessor)} throws a
     * {@link BadCredentialsException} when the token is invalid.
     */
    @Test
    void testExtractTokenFromMessageChannelHeadersNoToken() {
        final StompHeaderAccessor accessor = mock(StompHeaderAccessor.class);
        assertThrows(BadCredentialsException.class, () -> this.webSocketAuthInterceptor.extractTokenFromMessageChannelHeaders(accessor));
    }
}
