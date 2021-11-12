package com.higgs.server.web.svc.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersistenceUtils {
    public <T, R> void deleteAllIntersect(final Collection<T> saved, final Collection<T> updated, final Function<T, R> joinProvider, final JpaRepository<T, R> repository) {
        repository.deleteAll(saved.stream().filter(param -> !updated.stream()
                        .map(joinProvider)
                        .collect(Collectors.toList())
                        .contains(joinProvider.apply(param)))
                .collect(Collectors.toList()));
    }
}
