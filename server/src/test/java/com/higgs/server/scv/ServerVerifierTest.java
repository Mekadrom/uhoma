package com.higgs.server.scv;

import com.higgs.server.scv.condition.ServerCheck;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ServerVerifier}.
 */
class ServerVerifierTest {
    /**
     * Tests that the {@link ServerVerifier#check(CheckType)} method behaves correctly for different valid inputs.
     * @param serverCheckType The {@link CheckType} to test.
     * @param checkTypeFilter The {@link CheckType} to filter on.
     * @param serverCheckResult The result of the {@link ServerCheck} to test.
     * @param serverCheckFailureType The {@link CheckFailureType} to test.
     * @param expectedResult The expected result of the {@link ServerVerifier#check(CheckType)} method.
     * @param shouldServerCheckBeCalled Whether or not the {@link ServerCheck} should be called.
     */
    @SneakyThrows
    @ParameterizedTest
    @MethodSource("getTestCheckParams")
    void testCheck(final CheckType serverCheckType, final CheckType checkTypeFilter, final boolean serverCheckResult, final CheckFailureType serverCheckFailureType, final boolean expectedResult, final boolean shouldServerCheckBeCalled) {
        final ServerVerifier serverVerifier = ServerVerifier.getInstance();
        final ServerCheck serverCheck = mock(ServerCheck.class);
        final Set<ServerCheck> testCheckSet = Set.of(serverCheck);
        final Field conditionsField = ServerVerifier.class.getDeclaredField("conditions");
        ReflectionUtils.makeAccessible(conditionsField);
        ReflectionUtils.setField(conditionsField, serverVerifier, testCheckSet);
        when(serverCheck.getType()).thenReturn(serverCheckType);
        when(serverCheck.check(any())).thenReturn(serverCheckResult);
        when(serverCheck.getFailureType()).thenReturn(serverCheckFailureType);
        assertThat(serverVerifier.check(checkTypeFilter), is(equalTo(expectedResult)));
        verify(serverCheck, times(shouldServerCheckBeCalled ? 1 : 0)).check(any());
    }

    public static Stream<Arguments> getTestCheckParams() {
        return Stream.of(
                Arguments.of(CheckType.PRE_INITIALIZE, CheckType.PRE_INITIALIZE, true, CheckFailureType.LOG_AND_CONTINUE, true, true),
                Arguments.of(CheckType.PRE_INITIALIZE, CheckType.PRE_INITIALIZE, false, CheckFailureType.LOG_AND_CONTINUE, true, true),
                Arguments.of(CheckType.PRE_INITIALIZE, CheckType.PRE_INITIALIZE, true, CheckFailureType.LOG_AND_RETURN, true, true),
                Arguments.of(CheckType.PRE_INITIALIZE, CheckType.PRE_INITIALIZE, false, CheckFailureType.LOG_AND_RETURN, false, true),
                Arguments.of(CheckType.POST_INITIALIZE, CheckType.POST_INITIALIZE, false, CheckFailureType.UNHANDLED, true, true),
                Arguments.of(CheckType.PRE_INITIALIZE, CheckType.POST_INITIALIZE, true, CheckFailureType.LOG_AND_CONTINUE, true, false),
                Arguments.of(CheckType.PRE_INITIALIZE, CheckType.POST_INITIALIZE, false, CheckFailureType.LOG_AND_CONTINUE, true, false),
                Arguments.of(CheckType.POST_INITIALIZE, CheckType.PRE_INITIALIZE, true, CheckFailureType.LOG_AND_CONTINUE, true, false),
                Arguments.of(CheckType.POST_INITIALIZE, CheckType.PRE_INITIALIZE, false, CheckFailureType.LOG_AND_CONTINUE, true, false)
        );
    }

    /**
     * Tests that the {@link ServerVerifier#check(CheckType)} method behaves correctly for different valid inputs.
     * Specifically, that it throws a {@link RuntimeException} when the check fails, if it is a
     * {@link CheckFailureType#RUNTIME_EXCEPTION} check.
     */
    @Test
    @SneakyThrows
    void testCheckRuntimeException() {
        final ServerVerifier serverVerifier = ServerVerifier.getInstance();
        final ServerCheck serverCheck = mock(ServerCheck.class);
        final Set<ServerCheck> testCheckSet = Set.of(serverCheck);
        final Field conditionsField = ServerVerifier.class.getDeclaredField("conditions");
        ReflectionUtils.makeAccessible(conditionsField);
        ReflectionUtils.setField(conditionsField, serverVerifier, testCheckSet);
        when(serverCheck.getType()).thenReturn(CheckType.PRE_INITIALIZE);
        when(serverCheck.check(any())).thenReturn(false);
        when(serverCheck.getFailureType()).thenReturn(CheckFailureType.RUNTIME_EXCEPTION);
        assertThrows(RuntimeException.class, () -> serverVerifier.check(CheckType.PRE_INITIALIZE));
    }

    /**
     * Tests that the {@link ServerVerifier#getVerificationContext(CheckType)} method builds a valid
     * {@link VerificationContext} for the given inputs.
     */
    @Test
    void testGetVerificationContext() {
        final ServerVerifier serverVerifier = ServerVerifier.getInstance();
        final VerificationContext verificationContext = serverVerifier.getVerificationContext(CheckType.PRE_INITIALIZE);
        assertThat(verificationContext.getType(), is(equalTo(CheckType.PRE_INITIALIZE)));
        assertNotNull(verificationContext.getSystemEnv());
        assertNotNull(verificationContext.getSystemProperties());
    }

    /**
     * Tests that the {@link ServerVerifier#getFailureMessage(VerificationContext, ServerCheck)} method builds a valid
     * failure message for the given inputs.
     */
    @Test
    void testGetFailureMessage() {
        final VerificationContext context = mock(VerificationContext.class);
        final ServerCheck serverCheck = mock(ServerCheck.class);
        final String message = "test";
        when(serverCheck.getType()).thenReturn(CheckType.PRE_INITIALIZE);
        when(context.getFailureMessages()).thenReturn(Map.of(serverCheck, message));
        assertThat(ServerVerifier.getInstance().getFailureMessage(context, serverCheck), containsString(message));
    }

    /**
     * Tests that the {@link ServerVerifier#getVerificationContext(CheckType)} method builds a valid failure message
     * for the given inputs.
     */
    @Test
    void testGetFailureNoMessage() {
        final VerificationContext context = mock(VerificationContext.class);
        final ServerCheck serverCheck = mock(ServerCheck.class);
        when(serverCheck.getType()).thenReturn(CheckType.PRE_INITIALIZE);
        when(context.getFailureMessages()).thenReturn(Collections.emptyMap());
        assertThat(ServerVerifier.getInstance().getFailureMessage(context, serverCheck), containsString("[no message]"));
    }
}