package com.higgs.server.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.higgs.server.kafka.producer.KafkaTopicEnum;
import com.higgs.server.kafka.util.HAKafkaConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
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
    private final HAKafkaConfig kafkaConfig;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Future<SendResult<String, String>> send(final KafkaTopicEnum kafkaProducerEnum, final Object message, final Map<String, Object> headers) throws JsonProcessingException {
        return this.send(
                this.kafkaConfig.resolveTopicKeyReference(kafkaProducerEnum.getTopicKey()),
                kafkaProducerEnum.getKeyMakerFunc().apply(message),
                kafkaProducerEnum.getBodySerializer().writeValueAsString(message),
                headers,
                kafkaProducerEnum.getHeaderSerializer()
        );
    }

    public Future<SendResult<String, String>> send(final String topic, final String key, final String message, final Map<String, Object> headers, final ObjectMapper headerValueSerializer) {
        final ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(new ProducerRecord<>(topic, 0, key, message, this.convertHeaders(headers, headerValueSerializer)));
        future.addCallback(this::successCallback, this::failureCallback);
        return future;
    }

    private Collection<Header> convertHeaders(final Map<String, Object> headers, final ObjectMapper headerValueSerializer) {
        return headers.entrySet().stream()
                .map(it -> this.convertHeader(it, headerValueSerializer))
                .collect(Collectors.toList());
    }

    private void successCallback(final SendResult<String, String> sendResult) {
        HAKafkaProducer.log.debug("Kafka event successfully posted on topic \"{}\", partition {} at offset {}", sendResult.getProducerRecord().topic(), sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset());
    }

    private void failureCallback(final Throwable throwable) {
        HAKafkaProducer.log.error(throwable.getMessage(), throwable);
    }

    private Header convertHeader(final Map.Entry<String, Object> header, final ObjectMapper headerValueSerializer) {
        return new Header() {
            @Override
            public String key() {
                return header.getKey();
            }

            @Override
            public byte[] value() {
                try {
                    return String.valueOf(headerValueSerializer.writeValueAsString(header.getValue())).getBytes(StandardCharsets.UTF_8);
                } catch (final JsonProcessingException e) {
                    HAKafkaProducer.log.error(String.format("error serializing header: %s", header.getKey()), e);
                }
                return new byte[0]; // fail soft
            }
        };
    }
}
