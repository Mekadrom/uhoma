package com.higgs.common.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

@Slf4j
@Service
@AllArgsConstructor
public class ServerProducer {
    private final HAKafkaConfig haKafkaConfig;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Future<SendResult<String, String>> send(@NonNull final KafkaTopicEnum kafkaProducerEnum, final Object message, @NonNull final Map<String, Object> headers) throws IOException {
        return this.send(
                this.haKafkaConfig.resolveTopicKeyReference(kafkaProducerEnum.getTopicKey()),
                kafkaProducerEnum.getKeyMakerFunc().apply(message),
                kafkaProducerEnum.getBodyMapper().writeValueAsString(message),
                headers,
                kafkaProducerEnum.getHeaderMapper()
        );
    }

    public Future<SendResult<String, String>> send(final String topic, final String key, final String message, @NonNull final Map<String, Object> headers, @NonNull final ObjectMapper headerValueSerializer) {
        final ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(new ProducerRecord<>(topic, null, key, message, this.convertHeaders(headers, headerValueSerializer)));
        future.addCallback(this::successCallback, this::failureCallback);
        return future;
    }

    Collection<Header> convertHeaders(@NonNull final Map<String, Object> headers, @NonNull final ObjectMapper headerValueSerializer) {
        return headers.entrySet().stream()
                .map(it -> this.convertHeader(it, headerValueSerializer))
                .toList();
    }

    void successCallback(final SendResult<String, String> sendResult) {
        ServerProducer.log.debug("Kafka event successfully posted on topic \"{}\", partition {} at offset {}", sendResult.getProducerRecord().topic(), sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset());
    }

    void failureCallback(final Throwable throwable) {
        ServerProducer.log.error(throwable.getMessage(), throwable);
    }

    @NonNull
    Header convertHeader(final Map.Entry<String, Object> header, final ObjectMapper headerMapper) {
        return new Header() {
            @Override
            public String key() {
                return header.getKey();
            }

            @NonNull
            @Override
            public byte[] value() {
                try {
                    final Object value = header.getValue();
                    if (value == null) {
                        return new byte[0];
                    }

                    if (value instanceof String str) {
                        return str.getBytes(StandardCharsets.UTF_8);
                    }

                    return headerMapper.writeValueAsString(header.getValue()).getBytes(StandardCharsets.UTF_8);
                } catch (final JsonProcessingException e) {
                    ServerProducer.log.error(String.format("error serializing header: %s", header.getKey()), e);
                }
                return new byte[0]; // fail soft
            }
        };
    }
}
