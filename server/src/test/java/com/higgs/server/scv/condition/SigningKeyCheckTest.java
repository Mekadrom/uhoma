package com.higgs.server.scv.condition;

import com.higgs.server.scv.CheckFailureType;
import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.VerificationContext;
import com.higgs.server.util.ServerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SigningKeyCheck}.
 */
class SigningKeyCheckTest {
    /**
     * Test for {@link SigningKeyCheck#check(VerificationContext)}. Verifies the method calls on the
     * {@link VerificationContext} and the return value.
     * @param propValue The signing key for the properties map to contain
     * @param envValue The signing key for the environment map to contain
     * @param expected The expected return value
     */
    @ParameterizedTest
    @MethodSource("getTestCheckParams")
    void testCheck(final String propValue, final String envValue, final boolean expected) {
        final VerificationContext context = mock(VerificationContext.class);
        final Map<Object, Object> properties = Collections.singletonMap(ServerUtils.SIGNING_KEY_PROP_NAME, propValue);
        final Map<String, String> env = Collections.singletonMap(ServerUtils.SIGNING_KEY_ENV_NAME, envValue);
        when(context.getSystemProperties()).thenReturn(properties);
        when(context.getSystemEnv()).thenReturn(env);
        final boolean actual = new SigningKeyCheck().check(context);
        verify(context, times(1)).getSystemEnv();
        verify(context, times(1)).getSystemProperties();
        assertThat(actual, is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestCheckParams() {
        return Stream.of(
                Arguments.of("", "test", true),
                Arguments.of("test", "", true),
                Arguments.of("test", "test", true),
                Arguments.of(null, "test", true),
                Arguments.of("test", null, true),
                Arguments.of("", "", false),
                Arguments.of(null, null, false)
        );
    }

    /**
     * Test for {@link SigningKeyCheck#getType()}. Verifies the method returns {@link CheckType#PRE_INITIALIZE}.
     */
    @Test
    void testGetType() {
        assertThat(new SigningKeyCheck().getType(), is(equalTo(CheckType.PRE_INITIALIZE)));
    }

    /**
     * Test for {@link SigningKeyCheck#getFailureType()}. Verifies the method returns
     * {@link CheckFailureType#CHECK_FAILURE_RUNTIME_EXCEPTION}.
     */
    @Test
    void testGetFailureType() {
        assertThat(new SigningKeyCheck().getFailureType(), is(equalTo(CheckFailureType.CHECK_FAILURE_RUNTIME_EXCEPTION)));
    }
}
