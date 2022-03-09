package com.higgs.server.db.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ActionHandler}.
 */
class ActionHandlerTest {
    /**
     * Tests the {@link ActionHandler#getHomeSeq()} method. Verifies that it returns the home_seq of the action
     * handler's {@link Home}.
     */
    @Test
    void testGetHomeSeq() {
        final ActionHandler actionHandler = new ActionHandler();
        final Home home = mock(Home.class);
        when(home.getHomeSeq()).thenReturn(1L);
        actionHandler.setHome(home);
        assertThat(actionHandler.getHomeSeq(), is(equalTo(1L)));
    }
}
