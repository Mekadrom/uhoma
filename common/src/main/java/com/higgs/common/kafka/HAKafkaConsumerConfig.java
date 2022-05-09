package com.higgs.common.kafka;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Arrays;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class HAKafkaConsumerConfig {
    private final HAKafkaConfig kafkaConfig;

    @Bean
    public ConsumerFactory<String, String> consumerFactory(final Map<String, Object> configMap) {
        return new DefaultKafkaConsumerFactory<>(configMap);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        return this.configureKafkaListenerContainerFactory(new ConcurrentKafkaListenerContainerFactory<>());
    }

    ConcurrentKafkaListenerContainerFactory<String, String> configureKafkaListenerContainerFactory(final ConcurrentKafkaListenerContainerFactory<String, String> factory) {
        factory.setConsumerFactory(this.consumerFactory(this.getConfigMap()));
        if (this.kafkaConfig.getConsumer().isShouldFilterConsumer()) {
            factory.setRecordFilterStrategy(this::recordFilterStrategy);
        }
        return factory;
    }

    boolean recordFilterStrategy(final ConsumerRecord<String, String> consumerRecord) {
        if (consumerRecord.headers() == null) {
            return false;
        }
        return Arrays.stream(consumerRecord.headers().toArray())
                .filter(this::toNodeSeqHeaderName)
                .anyMatch(this::toNodeSeqHeaderValue);
    }

    Map<String, Object> getConfigMap() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaConfig.getBootstrapAddress(),
                ConsumerConfig.GROUP_ID_CONFIG, this.kafkaConfig.getConsumer().getGroupId(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
    }

    <T extends Header> boolean toNodeSeqHeaderName(final T header) {
        return header.key().equalsIgnoreCase(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ);
    }

    <T extends Header> boolean toNodeSeqHeaderValue(final T header) {
        return "0".equalsIgnoreCase(new String(header.value()));
    }
}
