package com.higgs.server.web.service.util;

import com.higgs.server.db.repo.SimpleNamedSequenceRepository;
import com.higgs.server.web.HASResponse;
import com.higgs.server.web.exception.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class RestUtils {
    @SneakyThrows
    public <T> ResponseEntity<String> searchEntity(final Optional<String> seqOpt, final Class<T> entityType, final JpaRepository<T, Long> repository) {
        Optional<T> entityOpt = Optional.empty();
        if (seqOpt.isPresent()) {
            entityOpt = repository.findById(Long.valueOf(seqOpt.get()));
        }
        return entityOpt.map(it -> HASResponse.builder(it).status(HttpStatus.OK).error(null).build().toResponseEntity()).orElseThrow(() -> new NotFoundException(entityType, seqOpt, Optional.empty()));
    }

    @SneakyThrows
    public <T> ResponseEntity<String> searchEntity(final Optional<String> seqOpt, final Optional<String> desc, final Class<T> entityType, final SimpleNamedSequenceRepository<T, Long> repository) {
        Optional<T> entityOpt = Optional.empty();
        if (seqOpt.isPresent()) {
            entityOpt = repository.findById(Long.valueOf(seqOpt.get()));
        } else if (desc.isPresent()) {
            entityOpt = repository.findByName(desc.get());
        }
        return entityOpt.map(it -> HASResponse.builder(it).status(HttpStatus.OK).error(null).build().toResponseEntity()).orElseThrow(() -> new NotFoundException(entityType, seqOpt, desc));
    }
}
