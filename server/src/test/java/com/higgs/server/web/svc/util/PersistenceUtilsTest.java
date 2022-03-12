package com.higgs.server.web.svc.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link PersistenceUtils}.
 */
class PersistenceUtilsTest {
    private PersistenceUtils persistenceUtils;

    @BeforeEach
    void setUp() {
        this.persistenceUtils = new PersistenceUtils();
    }

    /**
     * Test for {@link PersistenceUtils#deleteAllIntersect(Collection, Collection, Function, JpaRepository)}.
     * @param o1 the first object
     * @param o2 the second object
     * @param saved the saved objects
     * @param updated the updated objects
     * @param id1 the id of the first object
     * @param id2 the id of the second object
     * @param expectedDeletionList the expected deletion list
     */
    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @MethodSource("getTestDeleteAllIntersectParams")
    void testDeleteAllIntersect(final Object o1, final Object o2, final List<Object> saved, final List<Object> updated, final Long id1, final Long id2, final Collection<Long> expectedDeletionList) {
        final Function<Object, Long> function = mock(Function.class);
        when(function.apply(o1)).thenReturn(id1);
        when(function.apply(o2)).thenReturn(id2);

        final JpaRepository<Object, Long> repository = mock(JpaRepository.class);

        this.persistenceUtils.deleteAllIntersect(saved, updated, function, repository);
        verify(repository, times(1)).deleteAll(expectedDeletionList);
    }

    public static Stream<Arguments> getTestDeleteAllIntersectParams() {
        final Object o1 = mock(Object.class);
        final Object o2 = mock(Object.class);
        final List<Object> saved = List.of(o1);
        final List<Object> updated = List.of(o2);
        return Stream.of(
                Arguments.of(o1, o2, saved, updated, 1L, 2L, saved),
                Arguments.of(o1, o2, saved, updated, 1L, 1L, Collections.emptyList())
        );
    }
}
