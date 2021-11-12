package com.higgs.server.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
public class AuthResult {
    @NonNull
    private String jwt;

    @NonNull
    private UserDetails userDetails;
}
