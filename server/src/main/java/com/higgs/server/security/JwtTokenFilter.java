package com.higgs.server.security;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) throws IOException, ServletException {
        this.doFilterInternal(request, response, chain, SecurityContextHolder.getContext());
    }

    void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, final SecurityContext securityContext) throws IOException, ServletException {
        final Optional<UserLogin> userLoginOpt = this.jwtTokenUtil.parseAndValidateToken(request.getHeader(HttpHeaders.AUTHORIZATION), this.userLoginRepository::findByUsername);
        if (userLoginOpt.isPresent() && securityContext.getAuthentication() == null) {
            final UserLogin userLogin = userLoginOpt.get();
            JwtTokenFilter.log.debug("Successfully validated jwt for {}", userLogin.getUsername());
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userLogin, null, userLogin.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
