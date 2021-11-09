package com.higgs.server.config.security;

import com.higgs.server.db.entity.UserLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
    private static final long ACCESS_REFRESH_TOKEN_VALIDITY_SECONDS = 15 * 60 * 60;

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

    public String generateRefreshToken(final UserLogin userLogin) {
        return this.generateToken(userLogin, JwtTokenUtil.ACCESS_REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    public String generateToken(final UserLogin userLogin, final long validityMillis) {
        final Claims claims = Jwts.claims().setSubject(userLogin.getUsername());
        claims.put("scopes", Collections.singletonList(new SimpleGrantedAuthority(Roles.ADMIN)));
        claims.put(UserLogin.ACCOUNT_SEQ, userLogin.getAccount().getAccountSeq());
        this.ensureSigningKey();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("hams")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityMillis * 1000))
                .signWith(SignatureAlgorithm.HS256, this.signingKey)
                .setSubject(userLogin.getUsername())
                .compact();
    }

    private void ensureSigningKey() {
        if (this.signingKey == null) {
            final String prop = System.getProperty("security.auth.jwt.signing-key");
            final String env = System.getenv("HA_SERVER_SIGNING_KEY");
            this.signingKey = Optional.ofNullable(Optional.ofNullable(prop).orElse(env)).orElseThrow(() -> new RuntimeException("Signing key cannot be null"));
        }
    }
}
