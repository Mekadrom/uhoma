package com.higgs.server.web.rest;

import com.higgs.server.web.dto.AuthRequest;
import com.higgs.server.web.dto.AuthResult;
import com.higgs.server.web.svc.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "auth")
public class AuthenticationRest {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetails> login(@NonNull @RequestBody @Valid final AuthRequest request) {
        final AuthResult authResult = this.authenticationService.getTokens(request.getUsername(), request.getPassword());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, authResult.getJwt())
                .body(authResult.getUserDetails());
    }

    @PostMapping(value = "refreshUserView", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetails> refreshUserView(@NonNull final Principal principal) {
        return ResponseEntity.ok(this.authenticationService.performUserSearch(principal.getName()));
    }
}
