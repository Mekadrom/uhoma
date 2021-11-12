package com.higgs.server.kafka;

import com.higgs.server.kafka.handle.Handler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * todo: move consumer/handler logic out to another microservice
 */
@Service
@Profile("none")
@AllArgsConstructor
public class HAKafkaConsumer {
    private final List<? extends Handler> handlers;

    @KafkaListener(topics = "${kafka.topics.node-message}")
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        this.handlers.forEach(it -> it.handle(headers, message));
    }
}
