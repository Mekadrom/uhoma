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

import java.security.Principal;
import java.util.Optional;

@Component
@AllArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final AuthenticationService authenticationService;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    public Message<?> preSend(@NotNull final Message<?> message, @NotNull final MessageChannel channel) {
        return this.preSend(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class), message);
    }

    Message<?> preSend(final StompHeaderAccessor accessor, final Message<?> message) {
        if (accessor != null && this.isConnectInitEvent(accessor.getCommand())) {
            accessor.setUser(this.attachUserSession(this.extractTokenFromMessageChannelHeaders(accessor)));
        }
        return message;
    }

    boolean isConnectInitEvent(final StompCommand command) {
        return StompCommand.CONNECT.equals(command) || StompCommand.SUBSCRIBE.equals(command);
    }

    Principal attachUserSession(final String token) {
        if (this.authenticationService.validate(token)) {
            final String username = this.jwtTokenUtil.getUsernameFromToken(token);
            return new UsernamePasswordAuthenticationToken(username, null, this.authenticationService.performUserSearch(username).getAuthorities());
        }
        return null;
    }

    String extractTokenFromMessageChannelHeaders(final StompHeaderAccessor accessor) {
        return Optional.of(accessor)
                .map(it -> it.getNativeHeader(HttpHeaders.AUTHORIZATION))
                .flatMap(it -> it.stream().findAny())
                .map(this.jwtTokenUtil::removePrefix)
                .orElseThrow(() -> new BadCredentialsException("No valid token found in headers"));
    }
}
