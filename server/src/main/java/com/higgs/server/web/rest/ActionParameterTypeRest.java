package com.higgs.server.web.rest;

import com.higgs.server.db.entity.ActionParameterType;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.ActionParameterTypeService;
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
@RequestMapping(value = "actionParameterType")
public class ActionParameterTypeRest {
    private final ActionParameterTypeService actionParameterTypeService;

    @PostMapping(value = "search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<ActionParameterType>> search(@RequestBody final ActionParameterType searchCriteria, @NonNull final Principal principal) {
        return ResponseEntity.ok(this.actionParameterTypeService.performActionParameterTypeSearch(searchCriteria));
    }
}
