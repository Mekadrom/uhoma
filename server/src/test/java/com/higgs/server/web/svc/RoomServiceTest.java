package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Room;
import com.higgs.server.db.repo.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RoomService}.
 */
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    private RoomService roomService;

    @BeforeEach
    public void setUp() {
        this.roomService = new RoomService(this.roomRepository);
    }

    /**
     * Tests the {@link RoomService#upsert(Room)} method with valid input.
     */
    @Test
    void testUpsert() {
        final Room room = new Room();
        when(this.roomRepository.save(any())).thenReturn(room);
        assertThat(this.roomService.upsert(room), is(equalTo(room)));
        verify(this.roomRepository, times(1)).save(room);
    }

    /**
     * Tests the {@link RoomService#upsert(Room)} method with a null input, expecting an
     * {@link IllegalArgumentException}.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testUpsertNull() {
        assertThrows(IllegalArgumentException.class, () -> this.roomService.upsert(null));
    }

    /**
     * Tests the {@link RoomService#performRoomSearch(Room, Collection)} method with valid input, filtering on
     * {@link Room#getRoomSeq()}.
     */
    @Test
    void testPerformRoomSearchRoomSeqFilter() {
        final Room room = mock(Room.class);
        when(room.getRoomSeq()).thenReturn(1L);
        when(room.getHomeSeq()).thenReturn(2L);
        when(this.roomRepository.getByRoomSeqAndHomeHomeSeq(any(), any())).thenReturn(room);
        assertThat(this.roomService.performRoomSearch(room, Collections.singletonList(1L)), contains(room));
        verify(this.roomRepository, times(1)).getByRoomSeqAndHomeHomeSeq(1L, 2L);
    }

    /**
     * Tests the {@link RoomService#performRoomSearch(Room, Collection)} method with valid input, filtering on
     * {@link Room#getName()}.
     */
    @Test
    void testPerformRoomSearchRoomNameFilter() {
        final Room room = mock(Room.class);
        when(room.getRoomSeq()).thenReturn(null);
        when(room.getName()).thenReturn("test");
        when(room.getHomeSeq()).thenReturn(2L);
        when(this.roomRepository.getByNameContainingIgnoreCaseAndHomeHomeSeq(any(), any())).thenReturn(List.of(room));
        assertThat(this.roomService.performRoomSearch(room, Collections.singletonList(1L)), contains(room));
        verify(this.roomRepository, times(1)).getByNameContainingIgnoreCaseAndHomeHomeSeq("test", 2L);
    }

    /**
     * Tests the {@link RoomService#performRoomSearch(Room, Collection)} method with valid input, filtering on
     * {@link Room#getHomeSeq()}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformRoomSearchRoomHomeSeqFilter() {
        final Room room = mock(Room.class);
        when(room.getName()).thenReturn(null);
        when(room.getRoomSeq()).thenReturn(null);
        when(this.roomRepository.getByHomeHomeSeqIn(any())).thenReturn(List.of(room));
        assertThat(this.roomService.performRoomSearch(room, Collections.singletonList(1L)), contains(room));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.roomRepository, times(1)).getByHomeHomeSeqIn(captor.capture());
        assertThat(captor.getValue(), contains(1L));
    }

    /**
     * Tests the {@link RoomService#performRoomSearch(Room, Collection)} method with valid input, filtering on the
     * user's allowable homeSeqs.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformRoomSearchNullFilter() {
        this.roomService.performRoomSearch(null, Collections.singletonList(1L));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.roomRepository, times(1)).getByHomeHomeSeqIn(captor.capture());
        assertThat(captor.getValue(), contains(1L));
    }
}
