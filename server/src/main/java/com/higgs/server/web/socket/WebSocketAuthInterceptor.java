package com.higgs.server.web.socket;

import com.higgs.server.security.JwtTokenUtils;
import com.higgs.server.web.svc.AuthenticationService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final AuthenticationService authenticationService;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    public Message<?> preSend(@NotNull final Message<?> message, @NotNull final MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand()))) {
            final String bearer = this.extractTokenFromMessageChannelHeaders(accessor);
            final String token = this.jwtTokenUtil.removePrefix(bearer);
            if (this.authenticationService.validate(token)) {
                final String username = this.jwtTokenUtil.getUsernameFromToken(token);
                accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, this.authenticationService.performUserSearch(username).getAuthorities()));
            }
        }
        return message;
    }

    private String extractTokenFromMessageChannelHeaders(final StompHeaderAccessor accessor) {
        return Optional.of(accessor)
                .map(it -> it.getNativeHeader(HttpHeaders.AUTHORIZATION))
                .flatMap(it -> it.stream().findAny())
                .orElseThrow(() -> new BadCredentialsException("blank token supplied for web socket request"));
    }
}
