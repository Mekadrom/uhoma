package com.higgs.server.config.security;

import com.higgs.server.db.repo.UserLoginRepository;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
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

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;

    public JwtTokenFilter(final JwtTokenUtil jwtTokenUtil, final UserLoginRepository userLoginRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(header) && header.startsWith("Bearer ")) {
            final String token = header.split(" ")[1].trim();

            final Optional<? extends UserDetails> userDetailsOpt = this.userLoginRepository.findByUsername(this.jwtTokenUtil.getUsernameFromToken(token));
            if (userDetailsOpt.isPresent()) {
                final UserDetails userDetails = userDetailsOpt.get();
                if (this.jwtTokenUtil.validateToken(token, userDetails)) {
                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
