package com.higgs.node.common.kafka;

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
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Future<SendResult<String, String>> send(final String topic, final String key, final String message, final Map<String, String> headers) {
        final ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(new ProducerRecord<>(topic, 0, key, message, this.convertHeaders(headers)));
        future.addCallback(this::successCallback, this::failureCallback);
        return future;
    }

    private Collection<Header> convertHeaders(final Map<String, String> headers) {
        return headers.entrySet().stream()
                .map(this::convertHeader)
                .collect(Collectors.toList());
    }

    private Header convertHeader(final Map.Entry<String, String> header) {
        return new Header() {
            @Override
            public String key() {
                return header.getKey();
            }

            @Override
            public byte[] value() {
                return header.getValue().getBytes(StandardCharsets.UTF_8);
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
