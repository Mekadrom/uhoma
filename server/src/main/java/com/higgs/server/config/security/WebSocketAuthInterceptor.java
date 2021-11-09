package com.higgs.server.config.security;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import com.higgs.server.web.socket.NodeSocketBrokerConfigurer;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;

    @Override
    public Message<?> preSend(@NotNull final Message<?> message, @NotNull final MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            final Optional<String> tokenOpt = Optional.ofNullable(accessor.getNativeHeader(HttpHeaders.AUTHORIZATION)).flatMap(it -> it.stream().findAny());
            if (tokenOpt.isPresent()) {
                final String token = tokenOpt.get().substring(7);
                final String username = this.jwtTokenUtil.getUsernameFromToken(token);
                final Optional<UserLogin> userLoginOpt = this.userLoginRepository.findByUsername(username);
                if (userLoginOpt.isPresent()) {
                    final UserLogin userLogin = userLoginOpt.get();
                    if (this.jwtTokenUtil.validateToken(token, userLogin)) {
                        accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, userLogin.getAuthorities()));
                    }
                }
            }
        }
        return message;
    }
}
