package com.higgs.server.scv.condition;

import com.higgs.server.scv.CheckFailureType;
import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.VerificationContext;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link SigningKeyCheck}.
 */
class SigningKeyCheckTest {
    /**
     * Test for {@link SigningKeyCheck#check(VerificationContext)}. Verifies the method calls on the
     * {@link VerificationContext}.
     */
    @Test
    void testCheck() {
        final VerificationContext context = mock(VerificationContext.class);
        new SigningKeyCheck().check(context);
        verify(context, times(1)).getSystemEnv();
        verify(context, times(1)).getSystemProperties();
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
     * {@link CheckFailureType#RUNTIME_EXCEPTION}.
     */
    @Test
    void testGetFailureType() {
        assertThat(new SigningKeyCheck().getFailureType(), is(equalTo(CheckFailureType.RUNTIME_EXCEPTION)));
    }
}