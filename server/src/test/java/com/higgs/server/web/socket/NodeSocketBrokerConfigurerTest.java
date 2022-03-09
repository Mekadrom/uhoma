package com.higgs.server.web.socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NodeSocketBrokerConfigurer}.
 */
@ExtendWith(MockitoExtension.class)
class NodeSocketBrokerConfigurerTest {
    @Mock
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    private NodeSocketBrokerConfigurer nodeSocketBrokerConfigurer;

    @BeforeEach
    void setUp() {
        this.nodeSocketBrokerConfigurer = new NodeSocketBrokerConfigurer(this.webSocketAuthInterceptor);
    }

    /**
     * Tests for {@link NodeSocketBrokerConfigurer#registerStompEndpoints(StompEndpointRegistry)}. Verifies the method
     * calls going out of this method are correct for valid input.
     */
    @Test
    void testRegisterStompEndpoints() {
        final StompEndpointRegistry messageBrokerRegistry = mock(StompEndpointRegistry.class);
        final StompWebSocketEndpointRegistration stompWebSocketEndpointRegistration = mock(StompWebSocketEndpointRegistration.class);
        when(messageBrokerRegistry.addEndpoint(any())).thenReturn(stompWebSocketEndpointRegistration);
        when(stompWebSocketEndpointRegistration.setAllowedOriginPatterns(any())).thenReturn(stompWebSocketEndpointRegistration);
        this.nodeSocketBrokerConfigurer.registerStompEndpoints(messageBrokerRegistry);
        verify(messageBrokerRegistry, times(1)).addEndpoint(eq("/socket"));
        verify(stompWebSocketEndpointRegistration, times(1)).setAllowedOriginPatterns(eq("*"));
        verify(stompWebSocketEndpointRegistration, times(1)).withSockJS();
    }

    /**
     * Tests for {@link NodeSocketBrokerConfigurer#configureMessageBroker(MessageBrokerRegistry)}. Verifies the method
     * throws an {@link IllegalArgumentException} for invalid input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testRegisterStompEndpointsNull() {
        assertThrows(IllegalArgumentException.class, () -> this.nodeSocketBrokerConfigurer.registerStompEndpoints(null));
    }

    /**
     * Tests for {@link NodeSocketBrokerConfigurer#configureClientInboundChannel(ChannelRegistration)}. Verifies the
     * method calls going out of this method are correct for valid input.
     */
    @Test
    void testConfigureClientInboundChannel() {
        final ChannelRegistration channelRegistration = mock(ChannelRegistration.class);
        this.nodeSocketBrokerConfigurer.configureClientInboundChannel(channelRegistration);
        verify(channelRegistration, times(1)).interceptors(this.webSocketAuthInterceptor);
    }

    /**
     * Tests for {@link NodeSocketBrokerConfigurer#configureClientOutboundChannel(ChannelRegistration)}. Verifies the
     * method throws an {@link IllegalArgumentException} for invalid (null) input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testConfigureClientInboundChannelNull() {
        assertThrows(IllegalArgumentException.class, () -> this.nodeSocketBrokerConfigurer.configureClientInboundChannel(null));
    }

    /**
     * Tests for {@link NodeSocketBrokerConfigurer#configureMessageBroker(MessageBrokerRegistry)}. Verifies the method
     * calls going out of this method are correct for valid input.
     */
    @Test
    void testConfigureMessageBroker() {
        final MessageBrokerRegistry messageBrokerRegistry = mock(MessageBrokerRegistry.class);
        when(messageBrokerRegistry.setApplicationDestinationPrefixes(any())).thenReturn(messageBrokerRegistry);
        this.nodeSocketBrokerConfigurer.configureMessageBroker(messageBrokerRegistry);

        verify(messageBrokerRegistry, times(1)).setApplicationDestinationPrefixes(eq("/app"));
        verify(messageBrokerRegistry, times(1)).enableSimpleBroker("/topic");
    }

    /**
     * Tests for {@link NodeSocketBrokerConfigurer#configureMessageBroker(MessageBrokerRegistry)}. Verifies the method
     * throws an {@link IllegalArgumentException} for invalid (null) input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testConfigureMessageBrokerNull() {
        assertThrows(IllegalArgumentException.class, () -> this.nodeSocketBrokerConfigurer.configureMessageBroker(null));
    }
}
