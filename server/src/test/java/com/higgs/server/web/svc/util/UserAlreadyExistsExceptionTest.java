package com.higgs.server.web.svc.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link UserAlreadyExistsException}.
 */
class UserAlreadyExistsExceptionTest {
    /**
     * Ensures the constructor builds an appropriate message.
     */
    @Test
    void testMessageBuildConstructor() {
        assertThat(new UserAlreadyExistsException("test").getMessage(), is(equalTo("User 'test' already exists")));
    }
}
