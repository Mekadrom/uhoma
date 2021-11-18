package com.higgs.server.security;

import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.util.HAConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil implements Serializable {
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 6 * 60 * 60;

    private String signingKey;

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

    private Boolean isTokenExpired(final String token) {
        return this.getExpirationDateFromToken(token).before(new Date());
    }

    public Boolean validateToken(final String token, @NonNull final UserDetails userDetails) {
        final String username = this.getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !this.isTokenExpired(token);
    }

    public String generateToken(final UserLogin userLogin) {
        return this.generateToken(userLogin, JwtTokenUtil.ACCESS_TOKEN_VALIDITY_SECONDS);
    }

    public String generateToken(final UserLogin userLogin, final long validityMillis) {
        this.ensureSigningKey();
        return this.generateToken(userLogin, validityMillis, this.signingKey);
    }

    public String generateToken(final UserLogin userLogin, final long validityMillis, final String signingKey) {
        final Claims claims = Jwts.claims().setSubject(userLogin.getUsername());
        claims.put(UserLogin.ACCOUNT_SEQ, userLogin.getAccount().getAccountSeq());
        final String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuer("hams")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityMillis * 1000))
                .signWith(SignatureAlgorithm.HS256, signingKey)
                .setSubject(userLogin.getUsername())
                .compact();
        JwtTokenUtil.log.info("Successfully generated jwt for {}", userLogin.getUsername());
        return jwt;
    }

    private void ensureSigningKey() {
        if (this.signingKey == null) {
            final String prop = System.getProperty("security.auth.jwt.signing-key");
            final String env = System.getenv("HA_SERVER_SIGNING_KEY");
            this.signingKey = Optional.ofNullable(Optional.ofNullable(prop).orElse(env)).orElseThrow(() -> new RuntimeException("Signing key cannot be null"));
        }
    }

    public String removePrefix(final String tokenOrBearer) {
        if (tokenOrBearer != null && tokenOrBearer.startsWith(HAConstants.BEARER_PREFIX)) {
            return tokenOrBearer.substring(HAConstants.BEARER_PREFIX.length());
        }
        return tokenOrBearer;
    }

    public Optional<? extends UserDetails> parseAndValidateToken(final String bearer, final Function<String, Optional<UserLogin>> userRetriever) {
        final String tokenNoPrefix = this.removePrefix(bearer);
        if (StringUtils.isNotBlank(tokenNoPrefix)) {
            final String username = this.getUsernameFromToken(tokenNoPrefix);
            if (StringUtils.isNotBlank(username)) {
                return userRetriever.apply(username)
                        .filter(userDetails -> this.validateToken(tokenNoPrefix, userDetails))
                        .filter(userLogin -> this.tokenClaimsHasCorrectAccount(tokenNoPrefix, userLogin));
            }
        }
        return Optional.empty();
    }

    private boolean tokenClaimsHasCorrectAccount(final String token, final UserLogin userLogin) {
        final Long accountSeq = this.getClaimFromToken(token, claims -> claims.get(UserLogin.ACCOUNT_SEQ, Long.class));
        return accountSeq != null && accountSeq.equals(userLogin.getAccount().getAccountSeq());
    }
}
