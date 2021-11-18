package com.higgs.server.security;

import com.higgs.server.db.repo.UserLoginRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) throws IOException, ServletException {
        final Optional<? extends UserDetails> userDetailsOpt = this.jwtTokenUtil.parseAndValidateToken(request.getHeader(HttpHeaders.AUTHORIZATION), this.userLoginRepository::findByUsername);
        if (userDetailsOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = userDetailsOpt.get();
            JwtTokenFilter.log.debug("Successfully validated jwt for {}", userDetails.getUsername());
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
