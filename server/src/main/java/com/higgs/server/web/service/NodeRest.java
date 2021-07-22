package com.higgs.server.web.service;

import com.higgs.server.config.security.Roles;
import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.ActionParameterRepository;
import com.higgs.server.db.repo.ActionRepository;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.web.service.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RolesAllowed(Roles.ADMIN)
@RequestMapping(value = "node")
public class NodeRest {
    private final RestUtils restUtils;
    private final NodeRepository nodeRepository;
    private final ActionRepository actionRepository;
    private final ActionParameterRepository actionParameterRepository;

    @Transactional
    @PostMapping(value = "upsert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Node> upsert(@RequestBody(required = false) final Node node) {
        this.actionRepository.saveAll(node.getPublicActions());
        this.actionParameterRepository.saveAll(node.getPublicActions().stream()
                .map(Action::getParameters)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        this.actionRepository.flush();
        this.actionParameterRepository.flush();
        return ResponseEntity.ok(this.nodeRepository.save(node));
    }

    @SneakyThrows
    @PostMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> search(@NotNull @RequestBody(required = false) final Node searchCriteria,
                                             final Principal principal) {
        final Long accountSeq = this.restUtils.getAccountSeq(principal);

        if (searchCriteria != null) {
            if (searchCriteria.getNodeSeq() != null) {
                return ResponseEntity.ok(List.of(this.nodeRepository.getByNodeSeqAndRoomAccountAccountSeq(searchCriteria.getNodeSeq(), accountSeq)));
            }

            if (searchCriteria.getRoom() != null && searchCriteria.getRoom().getRoomSeq() != null) {
                if (searchCriteria.getName() != null) {
                    return ResponseEntity.ok(this.nodeRepository.getByNameContainingIgnoreCaseAndRoomRoomSeqAndRoomAccountAccountSeq(searchCriteria.getName(), searchCriteria.getRoom().getRoomSeq(), accountSeq));
                }
                return ResponseEntity.ok(this.nodeRepository.getByRoomRoomSeqAndRoomAccountAccountSeq(searchCriteria.getRoom().getRoomSeq(), accountSeq));
            } else {
                if (searchCriteria.getName() != null) {
                    return ResponseEntity.ok(this.nodeRepository.getByNameAndRoomAccountAccountSeq(searchCriteria.getName(), accountSeq));
                }
            }
        }
        return ResponseEntity.ok(this.nodeRepository.getByRoomAccountAccountSeq(accountSeq));
    }
}
