package com.higgs.server.web.service;

import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.db.repo.ActionHandlerRepository;
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
@RequestMapping(value = "actionHandler")
public class ActionHandlerRest {
    private final RestUtils restUtils;

    private final ActionHandlerRepository actionHandlerRepository;

    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<ActionHandler>> search(@RequestBody final ActionHandler searchCriteria,
                                                     final Principal principal) {
        return ResponseEntity.ok(Stream.concat(
                this.actionHandlerRepository.getByAccountAccountSeq(this.restUtils.getAccountSeq(principal)).stream(), // account specific handlers
                this.actionHandlerRepository.getByAccountAccountSeq(1L).stream() // corporate handlers
        ).collect(Collectors.toSet()));
    }
}
