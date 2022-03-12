package com.higgs.server.security;

import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.scv.CheckFailureException;
import com.higgs.server.util.HAConstants;
import com.higgs.server.util.ServerUtils;
import com.higgs.server.web.svc.HomeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    public static final String ALLOWED_HOMES_CLAIM = "allowed_homes";

    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 6L * 60L * 60L;

    private String signingKey;

    private final HomeService homeService;

    public String getUsernameFromToken(final String token) {
        return this.getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(final String token) {
        return this.getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(this.getAllClaimsFromToken(token));
    }

    private Claims getAllClaimsFromToken(final String token) {
        this.ensureSigningKey();
        return Jwts.parser()
                .setSigningKey(this.signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    Boolean isTokenExpired(final String token) {
        final Date expDate = this.getExpirationDateFromToken(token);
        if (expDate == null) {
            return true;
        }
        return expDate.before(new Date());
    }

    public Boolean validateToken(final String token, @NonNull final UserDetails userDetails) {
        final String username = this.getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
    }

    public String generateToken(final UserLogin userLogin) {
        return this.generateToken(userLogin, JwtTokenUtils.ACCESS_TOKEN_VALIDITY_SECONDS);
    }

    public String generateToken(final UserLogin userLogin, final long validitySeconds) {
        this.ensureSigningKey();
        return this.generateToken(userLogin, validitySeconds, this.signingKey);
    }

    public String generateToken(final UserLogin userLogin, final long validitySeconds, final String signingKey) {
        final Claims claims = Jwts.claims().setSubject(userLogin.getUsername());
        claims.put(JwtTokenUtils.ALLOWED_HOMES_CLAIM, this.homeService.getHomesForUser(userLogin).stream().map(Home::getHomeSeq).collect(Collectors.toSet()));
        final String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuer("hams")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(this.getExpirationDate(System.currentTimeMillis(), validitySeconds)))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .setSubject(userLogin.getUsername())
                .compact();
        JwtTokenUtils.log.info("Successfully generated jwt for {}", userLogin.getUsername());
        return jwt;
    }

    long getExpirationDate(final long startTime, final long validitySeconds) {
        return startTime + validitySeconds * 1000;
    }

    void ensureSigningKey() {
        if (this.signingKey == null) {
            this.signingKey = ServerUtils.getInstance().getSigningKey(System.getProperties(), System.getenv())
                    .orElseThrow(() -> new CheckFailureException("Signing key cannot be null"));
        }
    }

    public Optional<UserLogin> parseAndValidateToken(final String bearer, final Function<String, Optional<UserLogin>> userRetriever) {
        final String tokenNoPrefix = this.removePrefix(bearer);
        if (StringUtils.isNotBlank(tokenNoPrefix)) {
            final String username = this.getUsernameFromToken(tokenNoPrefix);
            if (StringUtils.isNotBlank(username)) {
                return userRetriever.apply(username).filter(userDetails -> this.validateToken(tokenNoPrefix, userDetails));
            }
        }
        return Optional.empty();
    }

    public String removePrefix(final String tokenOrBearer) {
        if (tokenOrBearer != null && tokenOrBearer.startsWith(HAConstants.BEARER_PREFIX)) {
            return tokenOrBearer.substring(HAConstants.BEARER_PREFIX.length());
        }
        return tokenOrBearer;
    }
}
