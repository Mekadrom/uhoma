package com.higgs.actionserver.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.higgs.common.handler.HandlerHandler;
import com.higgs.common.handler.HandlerResponse;
import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import com.higgs.common.util.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ActionServerConsumer {
    private final ServerProducer serverProducer;
    private final HandlerHandler handlerHandler;

    @KafkaListener(topics = "#{kafka.topics." + KafkaTopicEnum.NODE_ACTION_TOPIC_KEY + ":node_action}")
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        try {
            this.handleResponses(this.handlerHandler.process(headers, message));
        } catch (final IOException e) {
            ActionServerConsumer.log.error(String.format("Error processing handlers for message: %s", message), e);
        }
    }

    private void handleResponses(final List<? extends HandlerResponse> responses) {
        responses.forEach(response -> {
            try {
                if (response.isExpected()) {
                    this.serverProducer.send(KafkaTopicEnum.NODE_RESPONSE, response, this.buildResponseHeaders(response));
                }
            } catch (final IOException e) {
                ActionServerConsumer.log.error(String.format("Error occurred during response handling: %s", response), e);
            }
        });
    }

    private Map<String, Object> buildResponseHeaders(final HandlerResponse response) {
        return Map.of(
                HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ, response.getToNodeSeq(),
                HAKafkaConstants.HEADER_SENDING_NODE_SEQ, response.getFromNodeSeq(),
                HAKafkaConstants.HEADER_SENDING_USER_NAME, response.getFromUserName()
        );
    }
}
