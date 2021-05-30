package com.higgs.server.kafka;

import com.higgs.node.common.util.HASpringConstants;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@AllArgsConstructor
class ServerKafkaConsumer {
    @KafkaListener(topics = HASpringConstants.NODE_MESSAGE_TOPIC_NAME)
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        // todo: implement kafka message processing
    }
}
