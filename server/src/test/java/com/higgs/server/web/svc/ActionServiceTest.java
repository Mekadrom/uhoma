package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Action;
import com.higgs.server.db.repo.ActionRepository;
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
 * Tests for {@link ActionService}.
 */
@ExtendWith(MockitoExtension.class)
class ActionServiceTest {
    @Mock
    private ActionRepository actionRepository;

    @Mock
    private PersistenceUtils persistenceUtils;

    private ActionService actionService;

    @BeforeEach
    void setUp() {
        this.actionService = new ActionService(this.actionRepository, this.persistenceUtils);
    }

    /**
     * Test method for {@link ActionService#deleteAll(Collection, Collection)}. Verifies that the method delegates to
     * {@link PersistenceUtils#deleteAllIntersect(Collection, Collection, Function, JpaRepository)}.
     */
    @Test
    void testDeleteAll() {
        final Action action1 = mock(Action.class);
        final Action action2 = mock(Action.class);
        final Collection<Action> actions = List.of(action1, action2);
        this.actionService.deleteAll(actions, actions);
        verify(this.persistenceUtils, times(1)).deleteAllIntersect(eq(actions), eq(actions), any(), any());
    }

    /**
     * Test method for {@link ActionService#saveAll(Collection)}. Verifies that the method delegates to
     * {@link ActionRepository#saveAll(Iterable)}.
     */
    @Test
    void testSaveAll() {
        final Action action1 = mock(Action.class);
        final Action action2 = mock(Action.class);
        final Collection<Action> actions = List.of(action1, action2);
        this.actionService.saveAll(actions);
        verify(this.actionRepository, times(1)).saveAll(actions);
    }

    /**
     * Test method for {@link ActionService#flush()}. Verifies that the method delegates to
     * {@link ActionRepository#flush()}.
     */
    @Test
    void testFlush() {
        this.actionService.flush();
        verify(this.actionRepository, times(1)).flush();
    }
}
