package com.higgs.server.web.service;

import com.higgs.server.config.security.Roles;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.web.service.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@AllArgsConstructor
@RolesAllowed(Roles.ADMIN_AUTH)
@RequestMapping(value = "node")
public class NodeRest {
    private final NodeRepository nodeRepository;
    private final RestUtils restUtils;

    @SneakyThrows
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Node>> search(@RequestParam(required = false, value = "id") final Optional<String> seqOpt,
                                             @RequestParam(required = false, value = "name") final Optional<String> nameOpt,
                                             @RequestParam(required = false, value = "room") final Optional<String> roomOpt) {
        return roomOpt.map(value ->
                nameOpt.map(s ->
                        this.searchNodeByNameAndRoom(s, value)).orElseGet(() ->
                        this.searchNodeByRoom(value))).orElseGet(() ->
                this.restUtils.searchEntity(seqOpt, nameOpt, Node.class, this.nodeRepository));
    }

    private ResponseEntity<List<Node>> searchNodeByRoom(final String roomName) {
        return ResponseEntity.of(Optional.of(this.nodeRepository.findAll()).map(nodes -> nodes.stream()
                .filter(node -> node.getRoom() != null && node.getRoom().getName().equalsIgnoreCase(roomName))
                .collect(Collectors.toList())));
    }

    private ResponseEntity<List<Node>> searchNodeByNameAndRoom(final String nodeName, final String roomName) {
        return ResponseEntity.of(this.nodeRepository.findByNameContainingIgnoreCase(nodeName).map(nodes -> nodes.stream()
                .filter(node -> node.getRoom() != null && node.getRoom().getName().toUpperCase(Locale.ROOT).contains(roomName.toUpperCase(Locale.ROOT)))
                .collect(Collectors.toList())));
    }
}
