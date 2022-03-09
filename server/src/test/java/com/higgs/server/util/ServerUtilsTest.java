package com.higgs.server.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ServerUtils}.
 */
class ServerUtilsTest {
    /**
     * Test for {@link ServerUtils#getSigningKey(Map, Map)}. Should return the signing key from the input property maps.
     */
    @Test
    void testGetSigningKey() {
        final Map<Object, Object> properties = new HashMap<>();
        final Map<String, String> env = new HashMap<>();
        assertFalse(ServerUtils.getSigningKey(properties, env).isPresent());
        env.put("HA_SERVER_SIGNING_KEY", "test2");
        final Optional<String> signingKeyEnvOnly = ServerUtils.getSigningKey(properties, env);
        assertTrue(signingKeyEnvOnly.isPresent());
        assertThat(signingKeyEnvOnly.get(), is(equalTo("test2")));
        properties.put("security.auth.jwt.signing-key", "test1");
        final Optional<String> signingKeyPropertiesAndEnv = ServerUtils.getSigningKey(properties, env);
        assertTrue(signingKeyPropertiesAndEnv.isPresent());
        assertThat(signingKeyPropertiesAndEnv.get(), is(equalTo("test1")));
        env.remove("HA_SERVER_SIGNING_KEY");
        final Optional<String> signingKeyPropertiesOnly = ServerUtils.getSigningKey(properties, env);
        assertTrue(signingKeyPropertiesOnly.isPresent());
        assertThat(signingKeyPropertiesOnly.get(), is(equalTo("test1")));
    }
}
