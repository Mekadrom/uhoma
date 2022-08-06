package com.higgs.server.security;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFailureLoggingListener implements ApplicationListener<AbstractAuthenticationEvent> {
    @Override
    public void onApplicationEvent(@NonNull final AbstractAuthenticationEvent authenticationEvent) {
        if (authenticationEvent instanceof AuthenticationSuccessEvent || authenticationEvent instanceof InteractiveAuthenticationSuccessEvent) {
            return;
        }
        final Authentication authentication = authenticationEvent.getAuthentication();
        AuthenticationFailureLoggingListener.log.warn("Failed login attempt for username: {}", authentication.getName());
    }
}
