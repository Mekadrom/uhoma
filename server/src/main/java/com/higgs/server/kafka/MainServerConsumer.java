package com.higgs.server.kafka;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MainServerConsumer {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "${kafka.topics." + KafkaTopicEnum.NODE_RESPONSE_TOPIC_KEY + ":node_response}")
    public void listenNodeMessageTopic(@Headers final MessageHeaders headers, @Payload final String message) {
        this.transmit(headers, message);
    }

    void transmit(final MessageHeaders headers, final String message) {
        final String user = this.extractUser(headers);
        MainServerConsumer.log.info("Transmitting response message {} to user: {}", message, user);
        this.simpMessagingTemplate.convertAndSendToUser(user, "queue/reply", message, headers);
    }

    /**
     * Not writing extensive tests for this method because it has changes coming in the future.
     */
    String extractUser(@NonNull final MessageHeaders headers) {
        // TODO: replace with uuids that can identify either nodes or users
        return Optional.ofNullable(headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ))
                .filter(it -> it instanceof byte[])
                .map(it -> (byte[]) it)
                .filter(it -> it.length > 0)
                .map(String::new)
                .orElseGet(() -> Optional.ofNullable(headers.get(HAKafkaConstants.HEADER_RECEIVING_USERNAME))
                        .filter(it -> it instanceof byte[])
                        .map(it -> (byte[]) it)
                        .filter(it -> it.length > 0)
                        .map(String::new)
                        .orElseThrow(() -> new IllegalStateException("No recipient found in headers")));
    }
}
