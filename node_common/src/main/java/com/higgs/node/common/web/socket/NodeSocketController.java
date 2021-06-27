package com.higgs.node.common.web.socket;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.higgs.node.common.kafka.HAKafkaProducer;
import com.higgs.node.common.kafka.util.HAKafkaConstants;
import com.higgs.node.common.util.HASpringConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Controller
public class NodeSocketController {
    private static final String SESSION_ID = "sessionId";

    @Value(value = HASpringConstants.NODE_MESSAGE_TOPIC_NAME)
    private String nodeMessageTopicName;

    @Value(value = HASpringConstants.NODE_NAME)
    private String nodeName;

    private final HAKafkaProducer producer;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Autowired
    public NodeSocketController(final HAKafkaProducer producer, final SimpMessageSendingOperations messageSendingOperations) {
        this.producer = producer;
        this.messageSendingOperations = messageSendingOperations;
    }

    @MessageMapping("/message")
    public void processMessage(@Payload final String message, final SimpMessageHeaderAccessor headerAccessor) {
        Optional.ofNullable(headerAccessor.getSessionAttributes())
                .map(it -> it.get(NodeSocketController.SESSION_ID))
                .map(Object::toString)
                .ifPresent(headerAccessor::setSessionId);

        final JsonElement root = JsonParser.parseString(message);
        final String receivingNodeName = root.getAsJsonObject().get(HAKafkaConstants.HEADER_RECEIVING_NODE_NAME).getAsString();

        final Map<String, String> headers = Map.of(
                HAKafkaConstants.HEADER_SENDING_NODE_NAME, this.nodeName,
                HAKafkaConstants.HEADER_RECEIVING_NODE_NAME, receivingNodeName
        );
        this.producer.send(this.nodeMessageTopicName, String.valueOf(new Date().toInstant().getNano()), message, headers);
    }

    public void send(final String message, final Map<String, Object> headers) {
        this.messageSendingOperations.convertAndSendToUser("", message, "/topic/reply", headers);
    }
}