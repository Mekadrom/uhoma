package com.higgs.server.kafka.util;

import com.higgs.server.util.HASpringConstants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Arrays;
import java.util.Map;

@Configuration
public class HAKafkaConsumerConfig {
    @Value(value = HASpringConstants.VALUE_KAFKA_BOOTSTRAP_ADDRESS)
    private String bootstrapAddress;

    @Value(value = HASpringConstants.VALUE_KAFKA_CONSUMER_GROUP_ID)
    private String groupId;

    @Value(value = HASpringConstants.SHOULD_FILTER_CONSUMER)
    private boolean shouldFilterConsumer;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG, this.groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(this.consumerFactory());
        if (this.shouldFilterConsumer) {
            factory.setRecordFilterStrategy(record -> Arrays.stream(record.headers().toArray()).filter(this::recipientKey).anyMatch(this::recipientValue));
        }
        return factory;
    }

    private <T extends Header> boolean recipientKey(final T header) {
        return header.key().equalsIgnoreCase(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ);
    }

    private <T extends Header> boolean recipientValue(final T header) {
        return new String(header.value()).equalsIgnoreCase("0");
    }
}