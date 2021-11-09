package com.higgs.server.web.socket;

import com.higgs.server.config.security.JwtTokenUtil;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Optional;

@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class NodeSocketBrokerConfigurer implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;
    private final AuthenticationManager authenticationManager;
    private final NodeUserInterceptor nodeUserInterceptor;

    @Override
    public void registerStompEndpoints(@NotNull final StompEndpointRegistry registry) {
        registry.addEndpoint("/socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(@NotNull final ChannelRegistration registration) {
        registration.interceptors(this.nodeUserInterceptor, new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NotNull final Message<?> message, @NotNull final MessageChannel channel) {
                final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    final Optional<String> tokenOpt = Optional.ofNullable(accessor.getNativeHeader("Authorization")).flatMap(it -> it.stream().findAny());
                    if (tokenOpt.isPresent()) {
                        final String token = tokenOpt.get().substring(7);
                        final String username = NodeSocketBrokerConfigurer.this.jwtTokenUtil.getUsernameFromToken(token);
                        final Optional<UserLogin> userLoginOpt = NodeSocketBrokerConfigurer.this.userLoginRepository.findByUsername(username);
                        if (userLoginOpt.isPresent()) {
                            final UserLogin userLogin = userLoginOpt.get();
                            if (NodeSocketBrokerConfigurer.this.jwtTokenUtil.validateToken(token, userLogin)) {
                                accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, userLogin.getAuthorities()));
                            }
                        }
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(@NotNull final MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic");
    }
}
