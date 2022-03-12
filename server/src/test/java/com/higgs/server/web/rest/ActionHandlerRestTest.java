package com.higgs.server.web.rest;

import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.web.dto.ActionHandlerDto;
import com.higgs.server.web.svc.ActionHandlerService;
import com.higgs.server.web.svc.util.mapper.DtoEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ActionHandlerRest}.
 */
@ExtendWith(MockitoExtension.class)
class ActionHandlerRestTest {
    @Mock
    private ActionHandlerService actionHandlerService;

    @Mock
    private DtoEntityMapper dtoEntityMapper;

    private ActionHandlerRest actionHandlerRest;

    @BeforeEach
    void setUp() {
        this.actionHandlerRest = new ActionHandlerRest(this.actionHandlerService, this.dtoEntityMapper);
    }

    /**
     * Tests the {@link ActionHandlerRest#search(ActionHandlerDto, Principal)} method with valid input. Verifies method
     * calls and the {@link ResponseEntity} returned.
     */
    @Test
    void testSearch() {
        final ActionHandler actionHandler = mock(ActionHandler.class);
        final ActionHandlerDto actionHandlerDto = mock(ActionHandlerDto.class);
        final Principal principal = mock(Principal.class);
        when(this.actionHandlerService.performActionHandlerSearch(any())).thenReturn(Set.of(actionHandler));
        when(this.dtoEntityMapper.map(actionHandlerDto, ActionHandler.class)).thenReturn(actionHandler);
        final ResponseEntity<Set<ActionHandler>> actual = this.actionHandlerRest.search(actionHandlerDto, principal);
        verify(this.actionHandlerService).performActionHandlerSearch(actionHandler);
        verify(this.dtoEntityMapper).map(actionHandlerDto, ActionHandler.class);
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(Set.of(actionHandler)))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the {@link ActionHandlerRest#search(ActionHandler, Principal)} method with invalid input. Verifies that an
     * {@link IllegalArgumentException} is thrown.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testSearchNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.actionHandlerRest.search(mock(ActionHandlerDto.class), null));
    }
}
