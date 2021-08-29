package com.higgs.server.web.service;

import com.higgs.server.config.security.JwtTokenUtil;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import com.higgs.server.web.service.dto.AuthRequest;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "auth")
public class AuthenticationRest {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetails> login(@RequestBody @Valid final AuthRequest request) {
        final Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails user = (UserDetails) authenticate.getPrincipal();
        final Optional<UserLogin> userLogin = this.userLoginRepository.findByUsername(user.getUsername());
        if (userLogin.isPresent()) {
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, this.jwtTokenUtil.generateToken(userLogin.get())).body(user);
        }
        throw new UsernameNotFoundException(user.getUsername());
    }

    @PostMapping(value = "refreshToken", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetails> refreshToken(final HttpServletRequest request, final Principal principal) {
        final Optional<UserLogin> userLogin = this.userLoginRepository.findByUsername(principal.getName());
        if (userLogin.isPresent() && userLogin.get().getUsername() != null) {
            final DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");
            final String token = this.jwtTokenUtil.generateRefreshToken(claims, userLogin.get().getUsername());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(userLogin.get());
        }
        throw new UsernameNotFoundException(principal.getName());
    }

    @PostMapping(value = "refreshUserView", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetails> refreshUserView(final Principal principal) {
        if (principal == null) {
            AuthenticationRest.log.error("Principal is null");
        }
        final Optional<UserLogin> userLogin = this.userLoginRepository.findByUsername(principal.getName());
        if (userLogin.isPresent()) {
            return ResponseEntity.ok(userLogin.get());
        }
        throw new UsernameNotFoundException(principal.getName());
    }
}
