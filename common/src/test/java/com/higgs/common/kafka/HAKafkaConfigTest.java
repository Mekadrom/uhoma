package com.higgs.common.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HAKafkaConfigTest {
    @Test
    void testKafkaAdmin() {
        final HAKafkaConfig haKafkaConfig = new HAKafkaConfig();
        haKafkaConfig.setBootstrapAddress("localhost:9092");
        final HAKafkaConfig haKafkaConfigSpy = spy(haKafkaConfig);
        final KafkaAdmin actual = haKafkaConfigSpy.kafkaAdmin();
        verify(haKafkaConfigSpy, times(1)).getConfigMap();
        assertNotNull(actual);
    }

    @Test
    void testResolveTopicKeyReference() {
        final HAKafkaConfig haKafkaConfig = new HAKafkaConfig();
        haKafkaConfig.setTopics(Map.of("node-action", "node_action"));
        assertThat(haKafkaConfig.resolveTopicKeyReference("node-action"), is("node_action"));
    }

    @Test
    void testConsumerConfig() {
        final HAKafkaConfig haKafkaConfig = new HAKafkaConfig();
        final HAKafkaConfig.ConsumerConfig consumerConfig = new HAKafkaConfig.ConsumerConfig();
        haKafkaConfig.setConsumer(consumerConfig);
        assertNotNull(haKafkaConfig.getConsumer());
    }
}
