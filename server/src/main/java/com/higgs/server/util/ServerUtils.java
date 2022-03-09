package com.higgs.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerUtils {
    public static Optional<String> getSigningKey(final Map<Object, Object> properties, final Map<String, String> env) {
        final String propValue = (String) properties.get("security.auth.jwt.signing-key");
        final String envValue = env.get("HA_SERVER_SIGNING_KEY");
        return Optional.ofNullable(Optional.ofNullable(propValue).orElse(envValue));
    }
}
