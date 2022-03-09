package com.higgs.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@AllArgsConstructor
public class AuthResult {
    @NonNull
    private String jwt;

    @NonNull
    private UserDetails userDetails;
}
