package com.higgs.server.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {
    public NotFoundException(final Class<?> entityType, final Optional<String> seqOpt, final Optional<String> nameOpt) {
        super(String.format("Entity of type '%s' not found - %s", entityType.getSimpleName(), NotFoundException.getNotFoundString(seqOpt, nameOpt)));
    }

    private static String getNotFoundString(final Optional<String> seqOpt, final Optional<String> nameOpt) {
        final List<String> criteria = new LinkedList<>();
        seqOpt.ifPresent(it -> criteria.add(String.format("id: %s", it)));
        nameOpt.ifPresent(it -> criteria.add(String.format("name: %s", it)));
        return criteria.isEmpty() ? "no criteria provided" : String.join(", ", criteria);
    }
}
