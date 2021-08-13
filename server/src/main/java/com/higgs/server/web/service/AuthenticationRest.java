package com.higgs.server.web.service;

import com.higgs.server.config.security.JwtTokenUtil;
import com.higgs.server.db.entity.UserLogin;
import com.higgs.server.db.repo.UserLoginRepository;
import com.higgs.server.web.service.dto.AuthRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
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

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(value = "auth")
public class AuthenticationRest {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRepository userLoginRepository;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "login")
    public ResponseEntity<UserDetails> login(@RequestBody @Valid final AuthRequest request) {
        final Authentication authenticate = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails user = (UserDetails) authenticate.getPrincipal();
        final Optional<UserLogin> userLogin = this.userLoginRepository.findByUsername(user.getUsername());
        if (userLogin.isPresent()) {
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, this.jwtTokenUtil.generateToken(userLogin.get())).body(user);
        } else {
            throw new UsernameNotFoundException(user.getUsername());
        }
    }

    @PostMapping(value = "refresh")
    public ResponseEntity<UserDetails> refresh(final Principal principal) {
//        final Authentication authenticate = this.authenticationManager.authenticate()
        return ResponseEntity.ok(null);
    }
}
