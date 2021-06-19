package com.higgs.server.web.service;

import com.higgs.server.config.security.JwtTokenUtil;
import com.higgs.server.web.service.dto.AuthRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(value = "auth")
public class AuthenticationRest {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "login")
    public ResponseEntity<UserDetails> login(@RequestBody @Valid final AuthRequest request) {
        final Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails user = (UserDetails) authenticate.getPrincipal();
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, this.jwtTokenUtil.generateToken(user)).body(user);
    }
}
