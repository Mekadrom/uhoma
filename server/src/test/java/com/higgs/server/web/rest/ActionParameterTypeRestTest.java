package com.higgs.server.web.rest;

import com.higgs.server.db.entity.ActionParameterType;
import com.higgs.server.web.svc.ActionParameterTypeService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ActionParameterTypeRest}.
 */
@ExtendWith(MockitoExtension.class)
class ActionParameterTypeRestTest {
    @Mock
    private ActionParameterTypeService actionParameterTypeService;

    private ActionParameterTypeRest actionParameterTypeRest;

    @BeforeEach
    void setUp() {
        this.actionParameterTypeRest = new ActionParameterTypeRest(this.actionParameterTypeService);
    }

    /**
     * Tests the {@link ActionParameterTypeRest#search(ActionParameterType, Principal)} method with valid input.
     * Verifies method calls and the {@link ResponseEntity} returned.
     */
    @Test
    void testSearch() {
        final ActionParameterType actionParameterType = mock(ActionParameterType.class);
        final Principal principal = mock(Principal.class);
        when(this.actionParameterTypeService.performActionParameterTypeSearch(any())).thenReturn(Set.of(actionParameterType));
        final ResponseEntity<Set<ActionParameterType>> actual = this.actionParameterTypeRest.search(actionParameterType, principal);
        verify(this.actionParameterTypeService).performActionParameterTypeSearch(eq(actionParameterType));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(Set.of(actionParameterType)))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the {@link ActionParameterTypeRest#search(ActionParameterType, Principal)} method with invalid input.
     * Verifies that an {@link IllegalArgumentException} is thrown.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testSearchNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.actionParameterTypeRest.search(mock(ActionParameterType.class), null));
    }
}
