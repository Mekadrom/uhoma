package com.higgs.server.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.higgs.server.web.service.dto.ActionRequest;
import com.higgs.server.web.socket.NodeSocketController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class HAKafkaProducer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String nodeMessageTopicName;

    public Future<SendResult<String, String>> sendNodeMessage(final ActionRequest message, final Map<String, Object> headers) throws JsonProcessingException {
        return this.send(this.nodeMessageTopicName, null, HAKafkaProducer.OBJECT_MAPPER.writeValueAsString(message), headers);
    }

    public Future<SendResult<String, String>> send(final String topic, final String key, final String message, final Map<String, Object> headers) {
        final ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(new ProducerRecord<>(topic, 0, key, message, this.convertHeaders(headers)));
        future.addCallback(this::successCallback, this::failureCallback);
        return future;
    }

    private Collection<Header> convertHeaders(final Map<String, Object> headers) {
        return headers.entrySet().stream()
                .map(this::convertHeader)
                .collect(Collectors.toList());
    }

    private Header convertHeader(final Map.Entry<String, Object> header) {
        return new Header() {
            @Override
            public String key() {
                return header.getKey();
            }

            @Override
            public byte[] value() {
                try {
                    return String.valueOf(HAKafkaProducer.OBJECT_MAPPER.writeValueAsString(header.getValue())).getBytes(StandardCharsets.UTF_8);
                } catch (final JsonProcessingException e) {
                    HAKafkaProducer.log.error(String.format("error serializing header: %s", header.getKey()), e);
                }
                return new byte[0]; // fail soft
            }
        };
    }

    private void successCallback(final SendResult<String, String> sendResult) {
        HAKafkaProducer.log.debug(String.format("Kafka event successfully posted on topic \"%s\", partition %s at offset %s", sendResult.getProducerRecord().topic(), sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset()));
    }

    private void failureCallback(final Throwable throwable) {
        HAKafkaProducer.log.error(throwable.getMessage(), throwable);
    }
}
