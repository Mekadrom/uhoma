package com.higgs.server.web.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    @NonNull
    private String username;

    @NonNull
    private String password;
}
