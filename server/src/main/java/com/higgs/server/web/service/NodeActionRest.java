package com.higgs.server.web.service;

import com.higgs.server.config.security.Roles;
import com.higgs.server.db.entity.Action;
import com.higgs.server.db.repo.ActionParameterRepository;
import com.higgs.server.db.repo.ActionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
@RolesAllowed(Roles.ADMIN)
@RequestMapping(value = "node/action")
public class NodeActionRest {
    private final ActionRepository actionRepository;
    private final ActionParameterRepository actionParameterRepository;

    @PostMapping(value = "upsert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Action> upsert(@NotNull @RequestBody(required = true) final Action action) {
        this.actionParameterRepository.saveAllAndFlush(action.getParameters());
        return ResponseEntity.ok(this.actionRepository.saveAndFlush(action));
    }
}
