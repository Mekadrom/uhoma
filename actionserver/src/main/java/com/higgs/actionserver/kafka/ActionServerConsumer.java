package com.higgs.actionserver.kafka;

import com.higgs.common.handler.HandlerHandler;
import com.higgs.common.handler.HandlerResponse;
import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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

    @KafkaListener(topics = "${kafka.topics." + KafkaTopicEnum.NODE_ACTION_TOPIC_KEY + ":node_action}")
    public void listenNodeMessageTopic(@Headers final MultiValueMap<String, String> headers, @Payload final String message) {
        try {
            this.handleResponses(this.handlerHandler.process(headers, message));
        } catch (final IOException e) {
            ActionServerConsumer.log.error(String.format("Error processing handlers for message: %s", message), e);
        }
    }

    void handleResponses(@NonNull final List<? extends HandlerResponse> responses) {
        responses.stream().filter(HandlerResponse::isExpected).forEach(this::sendResponse);
    }

    void sendResponse(@NonNull final HandlerResponse response) {
        try {
            this.serverProducer.send(KafkaTopicEnum.NODE_RESPONSE, response, this.buildResponseHeaders(response));
        } catch (final IOException e) {
            ActionServerConsumer.log.error(String.format("Error occurred during response handling: %s", response), e);
        }
    }

    Map<String, Object> buildResponseHeaders(@NonNull final HandlerResponse response) {
        return Map.of(
                HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ, response.getToNodeSeq(),
                HAKafkaConstants.HEADER_SENDING_NODE_SEQ, response.getFromNodeSeq(),
                HAKafkaConstants.HEADER_RECEIVING_USERNAME, response.getToUsername(),
                HAKafkaConstants.HEADER_SENDING_USERNAME, response.getFromUsername()
        );
    }
}
