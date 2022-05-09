package com.higgs.common.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HAKafkaProducerConfigTest {
    @Mock
    private HAKafkaConfig haKafkaConfig;

    private HAKafkaProducerConfig haKafkaProducerConfig;

    @BeforeEach
    void setUp() {
        this.haKafkaProducerConfig = new HAKafkaProducerConfig(this.haKafkaConfig);
    }

    @Test
    void testProducerFactory() {
        assertNotNull(this.haKafkaProducerConfig.producerFactory(Collections.emptyMap()));
    }

    @Test
    void testKafkaTemplate() {
        final HAKafkaProducerConfig haKafkaProducerConfigSpy = spy(this.haKafkaProducerConfig);
        doReturn(Collections.emptyMap()).when(haKafkaProducerConfigSpy).getConfigMap();
        final KafkaTemplate<String, String> actual = haKafkaProducerConfigSpy.kafkaTemplate();
        verify(haKafkaProducerConfigSpy, times(1)).producerFactory(Collections.emptyMap());
        assertNotNull(actual);
    }

    @Test
    void testGetConfigMap() {
        when(this.haKafkaConfig.getBootstrapAddress()).thenReturn("localhost:9092");
        final Map<String, Object> actual = this.haKafkaProducerConfig.getConfigMap();
        assertAll(
                () -> assertNotNull(actual),
                () -> assertThat(actual.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG), is("localhost:9092")),
                () -> assertThat(actual.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG), is(StringSerializer.class)),
                () -> assertThat(actual.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG), is(StringSerializer.class))
        );
    }
}
