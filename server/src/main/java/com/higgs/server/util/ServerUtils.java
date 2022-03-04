package com.higgs.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerUtils {
    public static Optional<String> getSigningKey() {
        final String prop = System.getProperty("security.auth.jwt.signing-key");
        final String env = System.getenv("HA_SERVER_SIGNING_KEY");
        return Optional.ofNullable(Optional.ofNullable(prop).orElse(env));
    }
}
