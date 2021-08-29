package com.higgs.server.web.socket;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class NodeSocketConfigurer implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(@NotNull final StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/socket")
                .setAllowedOriginPatterns("*");
    }

//    @Override
//    public void configureClientInboundChannel(@NotNull final ChannelRegistration registration) {
//        registration.interceptors(new NodeUserInterceptor());
//    }

    @Override
    public void configureMessageBroker(@NotNull final MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/message");
    }
}