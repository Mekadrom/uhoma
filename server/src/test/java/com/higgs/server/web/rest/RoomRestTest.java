package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Room;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link RoomRest}.
 */
@ExtendWith(MockitoExtension.class)
class RoomRestTest {
    @Mock
    private RestUtils restUtils;

    @Mock
    private RoomService roomService;

    private RoomRest roomRest;

    @BeforeEach
    void setUp() {
        this.roomRest = new RoomRest(this.restUtils, this.roomService);
    }

    /**
     * Tests the method {@link RoomRest#upsert(Room)}. This method should return a {@link ResponseEntity} with the
     * status code {@code 200} if the upsert was successful, and a body containing the updated {@link Room} DTO.
     */
    @Test
    void testUpsert() {
        final Room room = mock(Room.class);
        when(this.roomService.upsert(any(Room.class))).thenReturn(room);
        final ResponseEntity<Room> actual = this.roomRest.upsert(room);
        verify(this.roomService, times(1)).upsert(eq(room));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(room))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link RoomRest#upsert(Room)} with invalid (null) input. This method should throw an
     * {@link IllegalArgumentException} for invalid input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testUpsertNull() {
        assertThrows(IllegalArgumentException.class, () -> this.roomRest.upsert(null));
    }

    /**
     * Tests the method {@link RoomRest#search(Room, Principal)}. This method should return a list of
     * {@link ResponseEntity} containing all of the matching {@link Room} DTOs for the input search criteria.
     */
    @Test
    void testSearch() {
        final Room room = mock(Room.class);
        final Principal principal = mock(Principal.class);
        final List<Room> roomSingletonList = Collections.singletonList(room);
        final Collection<Long> roomSeqs = Collections.singletonList(1L);
        when(this.roomService.performRoomSearch(any(), any())).thenReturn(roomSingletonList);
        when(this.restUtils.getHomeSeqs(any())).thenReturn(roomSeqs);
        final ResponseEntity<List<Room>> actual = this.roomRest.search(room, principal);
        verify(this.restUtils, times(1)).filterInvalidRequest(eq(principal), eq(room));
        verify(this.restUtils, times(1)).getHomeSeqs(eq(principal));
        verify(this.roomService, times(1)).performRoomSearch(eq(room), eq(roomSeqs));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(roomSingletonList))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link RoomRest#search(Room, Principal)} with invalid (null) input. This method should throw an
     * {@link IllegalArgumentException} for invalid input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testSearchNullPrincipal() {
        assertThrows(IllegalArgumentException.class, () -> this.roomRest.search(mock(Room.class), null));
    }
}
