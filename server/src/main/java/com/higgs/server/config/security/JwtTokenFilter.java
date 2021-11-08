package com.higgs.server.config.security;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        try {
            final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isNotBlank(header) && header.startsWith("Bearer ")) {
                final String token = header.split(" ")[1].trim();

                final Optional<? extends UserLogin> userDetailsOpt = this.userLoginRepository.findByUsername(this.jwtTokenUtil.getUsernameFromToken(token));
                if (userDetailsOpt.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    final UserLogin userDetails = userDetailsOpt.get();
                    if (this.jwtTokenUtil.validateToken(token, userDetails) && this.tokenClaimsHasCorrectAccount(token, userDetails)) {
                        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (final ExpiredJwtException e) {
            final String isRefreshToken = request.getHeader("isRefreshToken");
            final String requestUrl = request.getRequestURL().toString();
            if (isRefreshToken != null && Boolean.getBoolean(isRefreshToken) && requestUrl.contains("refreshToken")) {
                this.allowForRefreshToken(e, request);
            }
        }
        chain.doFilter(request, response);
    }

    private void allowForRefreshToken(final ExpiredJwtException e, final HttpServletRequest request) {
        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(null, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        request.setAttribute("claims", e.getClaims());
    }

    private boolean tokenClaimsHasCorrectAccount(final String token, final UserLogin userLogin) {
        final Long accountSeq = this.jwtTokenUtil.getClaimFromToken(token, claims -> claims.get(UserLogin.ACCOUNT_SEQ, Long.class));
        return accountSeq != null && accountSeq.equals(userLogin.getAccount().getAccountSeq());
    }
}
