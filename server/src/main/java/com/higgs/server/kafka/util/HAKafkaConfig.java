package com.higgs.server.kafka.util;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Getter
@Setter
@EnableKafka
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class HAKafkaConfig {
    private String bootstrapAddress;
    private Map<String, String> topics;
    private ConsumerConfig consumer;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapAddress));
    }

    public String resolveTopicKeyReference(final String topicKey) {
        return this.topics.get(topicKey);
    }

    @Getter
    @Setter
    public static final class ConsumerConfig {
        private String groupId;
        private boolean shouldFilterConsumer;
    }
}
