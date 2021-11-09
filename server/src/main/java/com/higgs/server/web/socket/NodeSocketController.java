package com.higgs.server.web.socket;

import com.higgs.server.kafka.HAKafkaProducer;
import com.higgs.server.util.HASpringConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class NodeSocketController {
    private static final String SESSION_ID = "sessionId";

    @Value(value = HASpringConstants.NODE_MESSAGE_TOPIC_NAME)
    private String nodeMessageTopicName;

    private final HAKafkaProducer producer;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NodeSocketController(final HAKafkaProducer producer, final SimpMessagingTemplate messagingTemplate) {
        this.producer = producer;
        this.messagingTemplate = messagingTemplate;
    }

//    @SendTo("/topic/node")
//    @MessageMapping("/node")
//    public void processMessage(@Payload final String message, final SimpMessageHeaderAccessor headerAccessor) {
//        NodeSocketController.log.debug("hello, message for you: " + message);
//
//        Optional.ofNullable(headerAccessor.getSessionAttributes())
//                .map(it -> it.get(NodeSocketController.SESSION_ID))
//                .map(Object::toString)
//                .ifPresent(headerAccessor::setSessionId);
//
//        final JsonElement root = JsonParser.parseString(message);
//        final String receivingNodeName = root.getAsJsonObject().get(HAKafkaConstants.HEADER_RECEIVING_NODE_NAME).getAsString();
//
//        final Map<String, String> headers = Map.of(
//                HAKafkaConstants.HEADER_SENDING_NODE_NAME, this.nodeName,
//                HAKafkaConstants.HEADER_RECEIVING_NODE_NAME, receivingNodeName
//        );
//        this.producer.send(this.nodeMessageTopicName, String.valueOf(new Date().toInstant().getNano()), message, headers);
//    }

    @MessageMapping("/nodeaction")
    public void receiveMessage(final String message) {
        NodeSocketController.log.error(message);
//        this.messagingTemplate.convertAndSendToUser(user, "/message", message);
    }

//    public void send(final String message, final Map<String, Object> headers) {
//        this.messagingTemplate.convertAndSendToUser("", message, "/topic/reply", headers);
//    }
}