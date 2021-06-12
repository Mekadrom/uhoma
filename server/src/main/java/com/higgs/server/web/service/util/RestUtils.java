package com.higgs.server.web.service.util;

import com.higgs.server.db.repo.SimpleNamedSequenceRepository;
import com.higgs.server.db.util.PersistenceUtils;
import com.higgs.server.web.HASResponse;
import com.higgs.server.web.exception.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public final class RestUtils {
    @SneakyThrows
    public <T> ResponseEntity<List<T>> searchEntity(final Optional<String> seqOpt, final Class<T> entityType, final JpaRepository<T, Long> repository) {
        final Optional<List<T>> entityOpt = seqOpt.map(s -> Optional.of(repository.getById(Long.valueOf(s)))
                .map(Collections::singletonList))
                .orElseGet(() -> Optional.of(repository.findAll()));
        return ResponseEntity.of(entityOpt);
    }

    @SneakyThrows
    public <T> ResponseEntity<List<T>> searchEntity(final Optional<String> seqOpt, final Optional<String> desc, final Class<T> entityType, final SimpleNamedSequenceRepository<T, Long> repository) {
        final Optional<List<T>> entityOpt;
        if (seqOpt.isPresent()) {
            entityOpt = Optional.of(repository.getById(Long.valueOf(seqOpt.get()))).map(Collections::singletonList);
        } else if (desc.isPresent()) {
            entityOpt = repository.findByNameLike(PersistenceUtils.getLikeString(desc.get()));
        } else {
            entityOpt = Optional.of(repository.findAll());
        }
        return ResponseEntity.of(entityOpt);
    }
}
