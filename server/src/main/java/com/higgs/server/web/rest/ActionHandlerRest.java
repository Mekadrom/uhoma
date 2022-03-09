package com.higgs.server.web.rest;

import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.web.svc.ActionHandlerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(value = "actionHandler")
public class ActionHandlerRest {
    private final ActionHandlerService actionHandlerService;

    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<ActionHandler>> search(@RequestBody final ActionHandler searchCriteria, @NonNull final Principal principal) {
        return ResponseEntity.ok(this.actionHandlerService.performActionHandlerSearch(searchCriteria));
    }
}
