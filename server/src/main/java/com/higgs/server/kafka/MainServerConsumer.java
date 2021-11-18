package com.higgs.server.kafka;

import com.higgs.common.kafka.KafkaTopicEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@AllArgsConstructor
public class MainServerConsumer {
    @KafkaListener(topics = "#{kafka.topics." + KafkaTopicEnum.NODE_RESPONSE_TOPIC_KEY + ":node_response}")
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        this.transmit(headers, message);
    }

    private void transmit(final MultiValueMap<String, String> headers, final String message) {

    }
}
