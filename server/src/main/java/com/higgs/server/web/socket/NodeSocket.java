package com.higgs.server.web.socket;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import com.higgs.server.db.entity.Node;
import com.higgs.server.web.dto.ActionDto;
import com.higgs.server.web.dto.ActionHandlerDto;
import com.higgs.server.web.dto.ActionRequest;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.NodeService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Controller
@AllArgsConstructor
public class NodeSocket {
    private final NodeService nodeService;
    private final RestUtils restUtils;
    private final ServerProducer producer;

    @SneakyThrows
    @MessageMapping("/nodeaction")
    public void receiveMessage(@NonNull final ActionRequest actionRequest, @NonNull final Principal principal) {
        if (actionRequest.getActionWithParams() == null) {
            throw new IllegalArgumentException("action not specified on request");
        }
        // ensure that the authentication principal provided has authorization to both relevant nodes
        this.validatePrincipalForNodes(principal, Stream.of(actionRequest.getToNodeSeq(), actionRequest.getFromNodeSeq()).filter(Objects::nonNull).toList());
        this.producer.send(KafkaTopicEnum.NODE_ACTION, actionRequest, this.buildHeaderMap(actionRequest));
    }

    Map<String, Object> buildHeaderMap(@NonNull final ActionRequest actionRequest) {
        final Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(HAKafkaConstants.HEADER_ACTION_HANDLER_DEF, Optional.ofNullable(actionRequest.getActionWithParams()).map(ActionDto::getActionHandler).map(ActionHandlerDto::getDefinition).orElse(StringUtils.EMPTY));
        headerMap.put(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ, actionRequest.getToNodeSeq());
        headerMap.put(HAKafkaConstants.HEADER_SENDING_NODE_SEQ, actionRequest.getFromNodeSeq());
        headerMap.put(HAKafkaConstants.HEADER_RECEIVING_USERNAME, actionRequest.getToUsername());
        headerMap.put(HAKafkaConstants.HEADER_SENDING_USERNAME, actionRequest.getFromUsername());
        return headerMap;
    }

    void validatePrincipalForNodes(final Principal principal, final List<Long> nodeSeqs) {
        final Collection<Long> homeSeqs = this.restUtils.getHomeSeqs(principal);
        final List<Long> ownedNodeSeqs = this.nodeService.getNodesForHomeSeqs(homeSeqs).stream()
                .map(Node::getNodeSeq)
                .toList();
        if (!ownedNodeSeqs.containsAll(nodeSeqs)) {
            throw new AccessDeniedException(String.format("principal \"%s\" does not have access to one or more nodes: %s", principal.getName(), nodeSeqs));
        }
    }
}
