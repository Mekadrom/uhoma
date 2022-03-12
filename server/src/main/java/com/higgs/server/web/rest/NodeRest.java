package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Node;
import com.higgs.server.web.dto.NodeDto;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.NodeService;
import com.higgs.server.web.svc.util.mapper.DtoEntityMapper;
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

@RestController
@AllArgsConstructor
@RequestMapping(value = "node")
public class NodeRest {
    private final DtoEntityMapper dtoEntityMapper;
    private final NodeService nodeService;
    private final RestUtils restUtils;

    @PostMapping(value = "upsertNode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Node> upsertNode(@NonNull @RequestBody final NodeDto nodeDto, @NonNull final Principal principal) {
        final Node node = this.dtoEntityMapper.map(nodeDto, Node.class);
        this.restUtils.filterInvalidRequest(principal, node);
        return ResponseEntity.ok(this.nodeService.upsert(node));
    }

    @PostMapping(value = "upsertNodes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> upsertNodes(@NonNull @RequestBody final List<NodeDto> nodeDtos, @NonNull final Principal principal) {
        final List<Node> nodes = nodeDtos.stream().map(nodeDto -> this.dtoEntityMapper.map(nodeDto, Node.class)).toList();
        nodes.forEach(node -> this.restUtils.filterInvalidRequest(principal, node));
        return ResponseEntity.ok(nodes.stream().map(this.nodeService::upsert).toList());
    }

    @PostMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> search(@RequestBody(required = false) final NodeDto searchCriteria, @NonNull final Principal principal) {
        return ResponseEntity.ok(this.nodeService.performNodeSearch(this.dtoEntityMapper.map(searchCriteria, Node.class), this.restUtils.getHomeSeqs(principal)));
    }
}
