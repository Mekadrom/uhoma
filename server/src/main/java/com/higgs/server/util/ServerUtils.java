package com.higgs.server.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerUtils {
    public static final String SIGNING_KEY_PROP_NAME = "security.auth.jwt.signing-key";
    public static final String SIGNING_KEY_ENV_NAME = "HA_SERVER_SIGNING_KEY";

    public static Optional<String> getSigningKey(final Map<Object, Object> properties, final Map<String, String> env) {
        final String propValue = (String) properties.get(ServerUtils.SIGNING_KEY_PROP_NAME);
        final String envValue = env.get(ServerUtils.SIGNING_KEY_ENV_NAME);
        return Optional.ofNullable(Optional.ofNullable(propValue).filter(StringUtils::isNotBlank).orElse(envValue));
    }
}
