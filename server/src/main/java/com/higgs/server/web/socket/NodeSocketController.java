package com.higgs.server.web.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.kafka.HAKafkaProducer;
import com.higgs.server.util.HASpringConstants;
import com.higgs.server.web.service.dto.ActionRequest;
import com.higgs.server.web.service.util.RestUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import javax.security.auth.login.AccountNotFoundException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
public class NodeSocketController {
    @Value(value = HASpringConstants.NODE_MESSAGE_TOPIC_NAME)
    private String nodeMessageTopicName;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HAKafkaProducer producer;
    private final RestUtils restUtils;
    private final NodeRepository nodeRepository;

    @Autowired
    public NodeSocketController(final HAKafkaProducer producer, final RestUtils restUtils, final NodeRepository nodeRepository) {
        this.producer = producer;
        this.restUtils = restUtils;
        this.nodeRepository = nodeRepository;
    }

    @SneakyThrows
    @MessageMapping("/nodeaction")
    public void receiveMessage(@NonNull final ActionRequest message, final Principal principal) {
        if (message.getActionWithParams() == null) {
            throw new IllegalArgumentException("action not specified on request");
        }
        // ensure that the authentication principal provided has authorization to both relevant nodes
        this.validatePrincipalForFromAndToNodes(principal, Stream.of(message.getFromNodeSeq(), message.getFromNodeSeq()).filter(Objects::nonNull).collect(Collectors.toList()));
        this.producer.send(this.nodeMessageTopicName, null, NodeSocketController.OBJECT_MAPPER.writeValueAsString(message), this.buildHeaderMap(message));
    }

    private Map<String, String> buildHeaderMap(final ActionRequest message) throws JsonProcessingException {
        final Map<String, String> map = new HashMap<>();
        map.put("action_handler_def", String.valueOf(NodeSocketController.OBJECT_MAPPER.writeValueAsString(message.getActionWithParams().getActionHandler())));
        return map;
    }

    private void validatePrincipalForFromAndToNodes(final Principal principal, final List<Long> nodeSeqs) throws AccountNotFoundException {
        final Long accountSeq = this.restUtils.getAccountSeq(principal);
        if (accountSeq == null) {
            throw new AccountNotFoundException();
        }
        final List<Long> ownedNodeSeqs = this.nodeRepository.getByRoomAccountAccountSeq(accountSeq).stream()
                .map(Node::getNodeSeq)
                .collect(Collectors.toList());
        if (!ownedNodeSeqs.containsAll(nodeSeqs)) {
            throw new AccessDeniedException(String.format("principal \"%s\" does not have access to one or more nodes: %s", principal.getName(), nodeSeqs));
        }
    }
}