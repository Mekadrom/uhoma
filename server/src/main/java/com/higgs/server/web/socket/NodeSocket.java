package com.higgs.server.web.socket;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.web.dto.ActionRequest;
import com.higgs.server.web.rest.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@AllArgsConstructor
public class NodeSocket {
    private final ServerProducer producer;
    private final RestUtils restUtils;
    private final NodeRepository nodeRepository;

    @SneakyThrows
    @MessageMapping("/nodeaction")
    public void receiveMessage(@NonNull final ActionRequest message, @NonNull final Principal principal) {
        if (message.getActionWithParams() == null) {
            throw new IllegalArgumentException("action not specified on request");
        }
        // ensure that the authentication principal provided has authorization to both relevant nodes
        this.validatePrincipalForNodes(principal, Stream.of(message.getFromNodeSeq(), message.getFromNodeSeq()).filter(Objects::nonNull).collect(Collectors.toList()));
        this.producer.send(KafkaTopicEnum.NODE_ACTION, message, this.buildHeaderMap(message));
    }

    private Map<String, Object> buildHeaderMap(@NonNull final ActionRequest message) {
        return Map.of(
                HAKafkaConstants.HEADER_ACTION_HANDLER_DEF, message.getActionWithParams().getActionHandler().getDefinition(),
                HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ, message.getToNodeSeq(),
                HAKafkaConstants.HEADER_SENDING_NODE_SEQ, message.getFromNodeSeq(),
                HAKafkaConstants.HEADER_SENDING_USER_NAME, message.getUsername()
        );
    }

    private void validatePrincipalForNodes(final Principal principal, final List<Long> nodeSeqs) {
        final Long accountSeq = this.restUtils.getAccountSeq(principal);
        final List<Long> ownedNodeSeqs = this.nodeRepository.getByRoomAccountAccountSeq(accountSeq).stream()
                .map(Node::getNodeSeq)
                .collect(Collectors.toList());
        if (!ownedNodeSeqs.containsAll(nodeSeqs)) {
            throw new AccessDeniedException(String.format("principal \"%s\" does not have access to one or more nodes: %s", principal.getName(), nodeSeqs));
        }
    }
}