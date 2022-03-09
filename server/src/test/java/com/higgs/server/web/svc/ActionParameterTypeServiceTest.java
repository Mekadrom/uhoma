package com.higgs.server.web.svc;

import com.higgs.server.db.entity.ActionParameterType;
import com.higgs.server.db.repo.ActionParameterTypeRepository;
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
 * Tests for {@link ActionParameterTypeService}.
 */
@ExtendWith(MockitoExtension.class)
class ActionParameterTypeServiceTest {
    @Mock
    private ActionParameterTypeRepository actionParameterTypeRepository;

    private ActionParameterTypeService actionParameterTypeService;

    @BeforeEach
    void setUp() {
        this.actionParameterTypeService = new ActionParameterTypeService(this.actionParameterTypeRepository);
    }

    /**
     * Test method for {@link ActionParameterTypeService#performActionParameterTypeSearch(ActionParameterType)} with
     * valid input.
     */
    @Test
    void testPerformActionParameterTypeSearch() {
        final ActionParameterType actionParameterType = mock(ActionParameterType.class);
        when(actionParameterType.getHomeSeq()).thenReturn(2L);
        when(this.actionParameterTypeRepository.getByHomeHomeSeq(any())).thenReturn(List.of(actionParameterType));
        assertThat(this.actionParameterTypeService.performActionParameterTypeSearch(actionParameterType), is(equalTo(Set.of(actionParameterType))));
        verify(this.actionParameterTypeRepository, times(1)).getByHomeHomeSeq(eq(2L));
        verify(this.actionParameterTypeRepository, times(1)).getByHomeHomeSeq(eq(1L));
    }

    /**
     * Test method for {@link ActionParameterTypeService#performActionParameterTypeSearch(ActionParameterType)} with
     * invalid (null) input.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testPerformActionParameterTypeSearchNullArg() {
        assertThrows(IllegalArgumentException.class, () -> this.actionParameterTypeService.performActionParameterTypeSearch(null));
    }
}
