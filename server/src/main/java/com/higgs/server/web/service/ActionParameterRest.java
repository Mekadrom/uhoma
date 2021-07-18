package com.higgs.server.web.service;

import com.higgs.server.db.entity.ActionParameterType;
import com.higgs.server.db.repo.ActionParameterTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "actionParameter")
public class ActionParameterRest {
    private final ActionParameterTypeRepository actionParameterTypeRepository;

    @PostMapping(value = "actionParameterType/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActionParameterType>> getActionParameterTypes(@RequestBody final ActionParameterType searchCriteria,
                                                                             final Principal principal) {
        final List<ActionParameterType> list = this.actionParameterTypeRepository.findAll();
        return ResponseEntity.ok(list);
    }
}
