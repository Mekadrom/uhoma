package com.higgs.server.kafka;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MainServerConsumer {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "${kafka.topics." + KafkaTopicEnum.NODE_RESPONSE_TOPIC_KEY + ":node_response}")
    public void listenNodeMessageTopic(@Headers final MessageHeaders headers, @Payload final String message) {
        this.transmit(headers, message);
    }

    void transmit(final MessageHeaders headers, final String message) {
        this.simpMessagingTemplate.convertAndSendToUser(this.extractUser(headers), "queue/reply", message, headers);
    }

    String extractUser(final MessageHeaders headers) {
        return Optional.ofNullable(headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ))
                .filter(it -> it instanceof byte[])
                .map(it -> new String((byte[]) it))
                .orElse(null);
    }
}
