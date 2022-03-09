package com.higgs.server.db.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link Room}.
 */
class RoomTest {
    /**
     * Tests the {@link Room#getHomeSeq()} method. Verifies that it returns the home_seq of the room's {@link Home}.
     */
    @Test
    void testGetHomeSeq() {
        final Room room = new Room();
        final Home home = mock(Home.class);
        when(home.getHomeSeq()).thenReturn(1L);
        room.setHome(home);
        assertThat(room.getHomeSeq(), is(equalTo(1L)));
    }
}
