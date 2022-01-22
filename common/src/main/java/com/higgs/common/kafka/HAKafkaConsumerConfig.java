package com.higgs.common.kafka;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaConfig.getBootstrapAddress(),
                ConsumerConfig.GROUP_ID_CONFIG, this.kafkaConfig.getConsumer().getGroupId(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(this.consumerFactory());
        if (this.kafkaConfig.getConsumer().isShouldFilterConsumer()) {
            factory.setRecordFilterStrategy(record -> Arrays.stream(record.headers().toArray())
                    .filter(this::toNodeSeqHeaderName)
                    .anyMatch(this::toNodeSeqHeaderValue));
        }
        return factory;
    }

    private <T extends Header> boolean toNodeSeqHeaderName(final T header) {
        return header.key().equalsIgnoreCase(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ);
    }

    private <T extends Header> boolean toNodeSeqHeaderValue(final T header) {
        return "0".equalsIgnoreCase(new String(header.value()));
    }
}