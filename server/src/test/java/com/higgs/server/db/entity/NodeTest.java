package com.higgs.server.db.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Node}.
 */
class NodeTest {
    /**
     * Tests the {@link Node#getHomeSeq()} method. Verifies that it returns the home_seq of the node's {@link Home}.
     */
    @Test
    void testGetHomeSeq() {
        final Node node = new Node();
        final Home home = mock(Home.class);
        when(home.getHomeSeq()).thenReturn(1L);
        node.setHome(home);
        assertThat(node.getHomeSeq(), is(equalTo(1L)));
    }
}
