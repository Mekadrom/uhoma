package com.higgs.server.web.service;

import com.higgs.server.config.security.Roles;
import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.ActionParameter;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RolesAllowed(Roles.ADMIN)
@RequestMapping(value = "node")
public class NodeRest {
    private final RestUtils restUtils;

    private final NodeRepository nodeRepository;
    private final ActionRepository actionRepository;
    private final ActionParameterRepository actionParameterRepository;

    @PostMapping(value = "upsertNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Node> upsertNode(@RequestBody(required = false) final Node node, final Principal principal) {
        return ResponseEntity.ok(this.upsert(node, this.restUtils.getAccountSeq(principal)));
    }

    @PostMapping(value = "upsertNodes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Node[]> upsertNodes(@RequestBody(required = false) final Node[] nodes, final Principal principal) {
        final Long accountSeq = this.restUtils.getAccountSeq(principal);
        Arrays.stream(nodes).forEach(node -> this.upsert(node, accountSeq));
        return ResponseEntity.ok(Stream.of(nodes).map(node -> this.upsert(node, accountSeq)).collect(Collectors.toList()).toArray(Node[]::new));
    }

    // TODO: convert to batch statements to reduce connections to DB
    @Transactional
    private Node upsert(final Node node, final Long accountSeq) {
        // grab existing state of node in DB in order to remove deleted child entities
        final Node saved = this.nodeRepository.getByNodeSeqAndRoomAccountAccountSeq(node.getNodeSeq(), accountSeq);

        // remove orphaned ActionParameter entities
        this.actionParameterRepository.deleteAll(saved.getActions().stream()
                .map(Action::getParameters)
                .flatMap(Collection::stream)
                .filter(param -> !node.getActions().stream()
                        .map(Action::getParameters)
                        .flatMap(Collection::stream)
                        .map(ActionParameter::getActionParameterSeq)
                        .collect(Collectors.toList())
                        .contains(param.getActionParameterSeq()))
                .collect(Collectors.toList()));

        // remove orphaned Action entities
        this.actionRepository.deleteAll(saved.getActions().stream()
                .filter(action -> !node.getActions().stream()
                        .map(Action::getActionSeq)
                        .collect(Collectors.toList())
                        .contains(action.getActionSeq()))
                .collect(Collectors.toList()));

        // save but not flush
        this.actionParameterRepository.saveAll(node.getActions().stream()
                .map(Action::getParameters)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));

        // save but not flush
        this.actionRepository.saveAll(node.getActions());

        // finally, flush all
        this.actionParameterRepository.flush();
        this.actionRepository.flush();
        return this.nodeRepository.saveAndFlush(node);
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
