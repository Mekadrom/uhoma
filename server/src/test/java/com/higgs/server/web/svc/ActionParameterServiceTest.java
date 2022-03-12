package com.higgs.server.web.svc;

import com.higgs.server.db.entity.ActionParameter;
import com.higgs.server.db.repo.ActionParameterRepository;
import com.higgs.server.web.svc.util.PersistenceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link ActionParameterService}.
 */
@ExtendWith(MockitoExtension.class)
class ActionParameterServiceTest {
    @Mock
    private ActionParameterRepository actionParameterRepository;

    @Mock
    private PersistenceUtils persistenceUtils;

    private ActionParameterService actionParameterService;

    @BeforeEach
    void setUp() {
        this.actionParameterService = new ActionParameterService(this.actionParameterRepository, this.persistenceUtils);
    }

    /**
     * Tests the method {@link ActionParameterService#deleteAll(Collection, Collection)} with valid input, makes sure
     * that it delegates to {@link PersistenceUtils#deleteAllIntersect(Collection, Collection, Function, JpaRepository)}
     */
    @Test
    void testDeleteAll() {
        final ActionParameter actionParameter1 = mock(ActionParameter.class);
        final ActionParameter actionParameter2 = mock(ActionParameter.class);
        final Collection<ActionParameter> actionParameters = List.of(actionParameter1, actionParameter2);
        this.actionParameterService.deleteAll(actionParameters, actionParameters);
        verify(this.persistenceUtils, times(1)).deleteAllIntersect(eq(actionParameters), eq(actionParameters), any(), any());
    }

    /**
     * Tests the method {@link ActionParameterService#saveAll(Collection)} with valid input, makes sure that it
     * delegates to {@link ActionParameterRepository#saveAll(Iterable)}.
     */
    @Test
    void testSaveAll() {
        final ActionParameter actionParameter1 = mock(ActionParameter.class);
        final ActionParameter actionParameter2 = mock(ActionParameter.class);
        final Collection<ActionParameter> actionParameters = List.of(actionParameter1, actionParameter2);
        this.actionParameterService.saveAll(actionParameters);
        verify(this.actionParameterRepository, times(1)).saveAll(actionParameters);
    }

    /**
     * Tests the method {@link ActionParameterService#flush()} with valid input, makes sure that it
     * delegates to {@link ActionParameterRepository#flush()}.
     */
    @Test
    void testFlush() {
        this.actionParameterService.flush();
        verify(this.actionParameterRepository, times(1)).flush();
    }
}
