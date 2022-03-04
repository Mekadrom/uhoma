package com.higgs.server.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String roleName;

    public static Role getByRoleName(final String roleName) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getRoleName().equals(roleName))
                .findFirst()
                .orElse(null);
    }
}
