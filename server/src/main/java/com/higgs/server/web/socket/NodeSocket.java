package com.higgs.server.web.socket;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.db.repo.NodeRepository;
import com.higgs.server.web.dto.ActionRequest;
import com.higgs.server.web.rest.util.RestUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * todo: figure out what the commented code was meant to do
 */
@Slf4j
@Controller
@AllArgsConstructor
public class NodeSocket {
    private final ServerProducer producer;
//    private final RestUtils restUtils;
//    private final NodeRepository nodeRepository;

    @SneakyThrows
    @MessageMapping("/nodeaction")
    public void receiveMessage(@NonNull final ActionRequest actionRequest, @NonNull final Principal principal) {
        if (actionRequest.getActionWithParams() == null) {
            throw new IllegalArgumentException("action not specified on request");
        }
        // ensure that the authentication principal provided has authorization to both relevant nodes
//        this.validatePrincipalForNodes(principal, Stream.of(actionRequest.getFromNodeSeq(), actionRequest.getFromNodeSeq()).filter(Objects::nonNull).collect(Collectors.toList()));
        this.producer.send(KafkaTopicEnum.NODE_ACTION, actionRequest, this.buildHeaderMap(actionRequest));
    }

    Map<String, Object> buildHeaderMap(@NonNull final ActionRequest actionRequest) {
        final Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(HAKafkaConstants.HEADER_ACTION_HANDLER_DEF, Optional.ofNullable(actionRequest.getActionWithParams()).map(Action::getActionHandler).map(ActionHandler::getDefinition).orElse(StringUtils.EMPTY));
        headerMap.put(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ, actionRequest.getToNodeSeq());
        headerMap.put(HAKafkaConstants.HEADER_SENDING_NODE_SEQ, actionRequest.getFromNodeSeq());
        headerMap.put(HAKafkaConstants.HEADER_RECEIVING_USERNAME, actionRequest.getToUsername());
        headerMap.put(HAKafkaConstants.HEADER_SENDING_USERNAME, actionRequest.getFromUsername());
        return headerMap;
    }

//    private void validatePrincipalForNodes(final Principal principal, final List<Long> nodeSeqs) {
//        final Long accountSeq = this.restUtils.getHomeSeqs(principal);
//        final List<Long> ownedNodeSeqs = this.nodeRepository.getByRoomHomeHomeSeq(accountSeq).stream()
//                .map(Node::getNodeSeq)
//                .collect(Collectors.toList());
//        if (!ownedNodeSeqs.containsAll(nodeSeqs)) {
//            throw new AccessDeniedException(String.format("principal \"%s\" does not have access to one or more nodes: %s", principal.getName(), nodeSeqs));
//        }
//    }
}