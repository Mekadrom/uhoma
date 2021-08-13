package com.higgs.server.web.service;

import com.higgs.server.db.entity.ActionParameterType;
import com.higgs.server.db.repo.ActionParameterTypeRepository;
import com.higgs.server.web.service.util.RestUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RequestMapping(value = "actionParameterType")
public class ActionParameterTypeRest {
    private final RestUtils restUtils;

    private final ActionParameterTypeRepository actionParameterTypeRepository;

    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<ActionParameterType>> search(@RequestBody final ActionParameterType searchCriteria,
                                                           final Principal principal) {
        return ResponseEntity.ok(Stream.concat(
                this.actionParameterTypeRepository.getByAccountAccountSeq(this.restUtils.getAccountSeq(principal)).stream(), // account specific action parameter types
                this.actionParameterTypeRepository.getByAccountAccountSeq(1L).stream() // corporate action parameter types
        ).collect(Collectors.toSet()));
    }
}
