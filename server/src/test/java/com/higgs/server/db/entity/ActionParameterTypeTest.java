package com.higgs.server.db.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ActionParameterType}.
 */
class ActionParameterTypeTest {
    /**
     * Tests the {@link ActionParameterType#getHomeSeq()} method. Verifies that it returns the home_seq of the action
     * parameter type's {@link Home}.
     */
    @Test
    void testGetHomeSeq() {
        final ActionParameterType actionParameterType = new ActionParameterType();
        final Home home = mock(Home.class);
        when(home.getHomeSeq()).thenReturn(1L);
        actionParameterType.setHome(home);
        assertThat(actionParameterType.getHomeSeq(), is(equalTo(1L)));
    }
}
