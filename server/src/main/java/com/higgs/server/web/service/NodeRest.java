package com.higgs.server.web.service;

import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.web.service.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(value = "node")
public class NodeRest {
    private final NodeRepository nodeRepository;
    private final RestUtils restUtils;

    @SneakyThrows
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> search(@RequestParam(required = false, value = "id") final Optional<String> seqOpt,
                                             @RequestParam(required = false, value = "name") final Optional<String> nameOpt) {
        return this.restUtils.searchEntity(seqOpt, nameOpt, Node.class, this.nodeRepository);
    }
}
