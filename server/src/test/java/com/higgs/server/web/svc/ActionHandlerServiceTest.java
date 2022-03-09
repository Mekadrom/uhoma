package com.higgs.server.web.svc;

import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.db.repo.ActionHandlerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ActionHandlerService}.
 */
@ExtendWith(MockitoExtension.class)
class ActionHandlerServiceTest {
    @Mock
    private ActionHandlerRepository actionHandlerRepository;

    private ActionHandlerService actionHandlerService;

    @BeforeEach
    void setUp() {
        this.actionHandlerService = new ActionHandlerService(this.actionHandlerRepository);
    }

    /**
     * Tests the method {@link ActionHandlerService#performActionHandlerSearch(ActionHandler)} with valid input.
     */
    @Test
    void testPerformActionHandlerSearch() {
        final ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionHandler.getHomeSeq()).thenReturn(2L);
        when(this.actionHandlerRepository.getByHomeHomeSeq(any())).thenReturn(List.of(actionHandler));
        assertThat(this.actionHandlerService.performActionHandlerSearch(actionHandler), is(equalTo(Set.of(actionHandler))));
        verify(this.actionHandlerRepository, times(1)).getByHomeHomeSeq(eq(2L));
        verify(this.actionHandlerRepository, times(1)).getByHomeHomeSeq(eq(1L));
    }

    /**
     * Test method for {@link ActionHandlerService#performActionHandlerSearch(ActionHandler)} with invalid (null) input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testPerformActionParameterTypeSearchNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.actionHandlerService.performActionHandlerSearch(null));
    }
}
