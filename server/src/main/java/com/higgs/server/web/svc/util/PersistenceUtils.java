package com.higgs.server.web.svc.util;

import com.higgs.server.db.entity.DtoFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class PersistenceUtils {
    public <T, R> void deleteAllIntersect(final Collection<T> saved, final Collection<T> updated, final Function<T, R> joinProvider, final JpaRepository<T, R> repository) {
        repository.deleteAll(saved.stream()
                .filter(param -> !updated.stream()
                        .map(joinProvider)
                        .toList()
                        .contains(joinProvider.apply(param)))
                .toList());
    }

    public <T extends DtoFilter> List<T> getByHomeSeqs(final DtoFilter dtoFilter, final Collection<Long> homeSeqs, final Function<List<Long>, List<T>> provider) {
        return provider.apply(Optional.ofNullable(dtoFilter)
                .map(DtoFilter::getHomeSeq)
                .filter(homeSeqs::contains)
                .map(List::of)
                .orElse(homeSeqs.stream().toList()));
    }
}
