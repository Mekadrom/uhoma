package com.higgs.server.kafka;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.util.CommonUtils;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@AllArgsConstructor
public class MainServerConsumer {
    private final CommonUtils commonUtils;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @KafkaListener(topics = "${kafka.topics." + KafkaTopicEnum.NODE_RESPONSE_TOPIC_KEY + ":node_response}")
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        this.transmit(headers, message);
    }

    private void transmit(final MultiValueMap<String, String> headers, final String message) {
        this.simpMessagingTemplate.convertAndSendToUser(this.extractUser(headers), "queue/reply", message, this.commonUtils.toObjectMap(this.commonUtils.flattenMap(headers)));
    }

    private String extractUser(final MultiValueMap<String, String> headers) {
        return headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ).stream().findFirst().orElse(null);
    }
}
