package com.higgs.node.common.kafka;

import com.higgs.node.common.InputHandler;
import com.higgs.node.common.util.HASpringConstants;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
@AllArgsConstructor
public class HAKafkaConsumer {
    private final List<? extends InputHandler> handlers;

    @KafkaListener(topics = HASpringConstants.NODE_MESSAGE_TOPIC_NAME)
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        this.handlers.forEach(it -> it.handle(headers, message));
    }
}
