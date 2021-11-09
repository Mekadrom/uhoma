package com.higgs.server.web.socket;

import com.higgs.server.config.security.JwtTokenUtil;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

@Configuration
@AllArgsConstructor
public class AuthenticationInterceptor implements ChannelInterceptor {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;

    @Override
    public Message<?> preSend(@NotNull final Message<?> message, @NotNull final MessageChannel channel) {
        final String token = Optional.of(StompHeaderAccessor.wrap(message))
                .filter(it -> StompCommand.CONNECT.equals(it.getCommand()) || StompCommand.SUBSCRIBE.equals(it.getCommand()))
                .map(it -> it.getMessageHeaders().get("Authorization"))
                .map(String::valueOf)
                .orElseThrow(() -> new BadCredentialsException("blank token supplied for web socket request"));

        final String username = this.jwtTokenUtil.getUsernameFromToken(token);
        if (StringUtils.isNotBlank(username)) {
            final Optional<UserLogin> userLoginOpt = this.userLoginRepository.findByUsername(username);
            if (userLoginOpt.isPresent()) {
                if (this.jwtTokenUtil.validateToken(token, userLoginOpt.get())) {
                    return message;
                }
            }
        }
        throw new BadCredentialsException("incorrect or blank token supplied for web socket request");
    }
}

