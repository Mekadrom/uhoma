package com.higgs.server.kafka.util;

import com.higgs.server.util.HASpringConstants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@EnableKafka
@Configuration
public class HAKafkaConfig {
    @Value(value = HASpringConstants.VALUE_KAFKA_BOOTSTRAP_ADDRESS)
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress));
    }
}
