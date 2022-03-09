package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Node;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.NodeService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RequestMapping(value = "node")
public class NodeRest {
    private final NodeService nodeService;
    private final RestUtils restUtils;

    @PostMapping(value = "upsertNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Node> upsertNode(@NonNull @RequestBody final Node node, @NonNull final Principal principal) {
        this.restUtils.filterInvalidRequest(principal, node);
        return ResponseEntity.ok(this.nodeService.upsert(node));
    }

    @PostMapping(value = "upsertNodes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> upsertNodes(@NonNull @RequestBody final List<Node> nodes, @NonNull final Principal principal) {
        nodes.forEach(node -> this.restUtils.filterInvalidRequest(principal, node));
        return ResponseEntity.ok(nodes.stream()
                .map(this.nodeService::upsert)
                .collect(Collectors.toList()));
    }

    @PostMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> search(@RequestBody(required = false) final Node searchCriteria, @NonNull final Principal principal) {
        return ResponseEntity.ok(this.nodeService.performNodeSearch(searchCriteria, this.restUtils.getHomeSeqs(principal)));
    }
}
