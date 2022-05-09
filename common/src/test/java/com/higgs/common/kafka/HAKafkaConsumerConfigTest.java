package com.higgs.common.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HAKafkaConsumerConfigTest {
    @Mock
    private HAKafkaConfig haKafkaConfig;

    private HAKafkaConsumerConfig haKafkaConsumerConfig;

    @BeforeEach
    void setUp() {
        this.haKafkaConsumerConfig = new HAKafkaConsumerConfig(this.haKafkaConfig);
    }

    @Test
    void testConsumerFactory() {
        assertNotNull(this.haKafkaConsumerConfig.consumerFactory(Collections.emptyMap()));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testKafkaListenerContainerFactoryDelegates() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory = mock(ConcurrentKafkaListenerContainerFactory.class);
        doReturn(kafkaListenerContainerFactory).when(haKafkaConsumerConfigSpy).configureKafkaListenerContainerFactory(any());
        final ConcurrentKafkaListenerContainerFactory<String, String> actual = haKafkaConsumerConfigSpy.kafkaListenerContainerFactory();
        verify(haKafkaConsumerConfigSpy, times(1)).configureKafkaListenerContainerFactory(any());
        assertThat(actual, is(kafkaListenerContainerFactory));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConfigureKafkaListenerContainerFactoryShouldNotFilter() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = mock(ConcurrentKafkaListenerContainerFactory.class);
        final DefaultKafkaConsumerFactory<String, String> defaultKafkaConsumerFactory = mock(DefaultKafkaConsumerFactory.class);
        final HAKafkaConfig.ConsumerConfig consumerConfig = mock(HAKafkaConfig.ConsumerConfig.class);
        when(this.haKafkaConfig.getConsumer()).thenReturn(consumerConfig);
        when(consumerConfig.isShouldFilterConsumer()).thenReturn(false);
        doReturn(Collections.emptyMap()).when(haKafkaConsumerConfigSpy).getConfigMap();
        doReturn(defaultKafkaConsumerFactory).when(haKafkaConsumerConfigSpy).consumerFactory(any());
        final ConcurrentKafkaListenerContainerFactory<String, String> actual = haKafkaConsumerConfigSpy.configureKafkaListenerContainerFactory(concurrentKafkaListenerContainerFactory);
        verify(concurrentKafkaListenerContainerFactory, times(1)).setConsumerFactory(defaultKafkaConsumerFactory);
        verify(this.haKafkaConfig, times(1)).getConsumer();
        verify(haKafkaConsumerConfigSpy, times(1)).consumerFactory(Collections.emptyMap());
        verify(concurrentKafkaListenerContainerFactory, times(0)).setRecordFilterStrategy(any());
        assertThat(actual, is(concurrentKafkaListenerContainerFactory));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConfigureKafkaListenerContainerFactoryShouldFilter() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = mock(ConcurrentKafkaListenerContainerFactory.class);
        final DefaultKafkaConsumerFactory<String, String> defaultKafkaConsumerFactory = mock(DefaultKafkaConsumerFactory.class);
        final HAKafkaConfig.ConsumerConfig consumerConfig = mock(HAKafkaConfig.ConsumerConfig.class);
        when(this.haKafkaConfig.getConsumer()).thenReturn(consumerConfig);
        when(consumerConfig.isShouldFilterConsumer()).thenReturn(true);
        doReturn(Collections.emptyMap()).when(haKafkaConsumerConfigSpy).getConfigMap();
        doReturn(defaultKafkaConsumerFactory).when(haKafkaConsumerConfigSpy).consumerFactory(any());
        final ConcurrentKafkaListenerContainerFactory<String, String> actual = haKafkaConsumerConfigSpy.configureKafkaListenerContainerFactory(concurrentKafkaListenerContainerFactory);
        verify(concurrentKafkaListenerContainerFactory, times(1)).setConsumerFactory(defaultKafkaConsumerFactory);
        verify(this.haKafkaConfig, times(1)).getConsumer();
        verify(haKafkaConsumerConfigSpy, times(1)).consumerFactory(Collections.emptyMap());
        verify(concurrentKafkaListenerContainerFactory, times(1)).setRecordFilterStrategy(any());
        assertThat(actual, is(concurrentKafkaListenerContainerFactory));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRecordFilterStrategyNameAndValueMatch() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        final Headers headers = mock(Headers.class);
        final Header header = mock(Header.class);
        when(record.headers()).thenReturn(headers);
        when(headers.toArray()).thenReturn(new Header[] { header });
        doReturn(true).when(haKafkaConsumerConfigSpy).toNodeSeqHeaderName(any());
        doReturn(true).when(haKafkaConsumerConfigSpy).toNodeSeqHeaderValue(any());
        final boolean actual = haKafkaConsumerConfigSpy.recordFilterStrategy(record);
        verify(haKafkaConsumerConfigSpy, times(1)).toNodeSeqHeaderName(header);
        verify(haKafkaConsumerConfigSpy, times(1)).toNodeSeqHeaderValue(header);
        assertTrue(actual);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRecordFilterStrategyNameMatches() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        final Headers headers = mock(Headers.class);
        final Header header = mock(Header.class);
        when(record.headers()).thenReturn(headers);
        when(headers.toArray()).thenReturn(new Header[] { header });
        doReturn(true).when(haKafkaConsumerConfigSpy).toNodeSeqHeaderName(any());
        doReturn(false).when(haKafkaConsumerConfigSpy).toNodeSeqHeaderValue(any());
        final boolean actual = haKafkaConsumerConfigSpy.recordFilterStrategy(record);
        verify(haKafkaConsumerConfigSpy, times(1)).toNodeSeqHeaderName(header);
        verify(haKafkaConsumerConfigSpy, times(1)).toNodeSeqHeaderValue(header);
        assertFalse(actual);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRecordFilterStrategyValueMatches() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        final Headers headers = mock(Headers.class);
        final Header header = mock(Header.class);
        when(record.headers()).thenReturn(headers);
        when(headers.toArray()).thenReturn(new Header[] { header });
        doReturn(false).when(haKafkaConsumerConfigSpy).toNodeSeqHeaderName(any());
        final boolean actual = haKafkaConsumerConfigSpy.recordFilterStrategy(record);
        verify(haKafkaConsumerConfigSpy, times(1)).toNodeSeqHeaderName(header);
        verify(haKafkaConsumerConfigSpy, times(0)).toNodeSeqHeaderValue(header);
        assertFalse(actual);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRecordFilterStrategyNoHeaders() {
        final HAKafkaConsumerConfig haKafkaConsumerConfigSpy = spy(this.haKafkaConsumerConfig);
        final ConsumerRecord<String, String> record = mock(ConsumerRecord.class);
        when(record.headers()).thenReturn(null);
        final boolean actual = haKafkaConsumerConfigSpy.recordFilterStrategy(record);
        assertFalse(actual);
    }

    @Test
    void testGetConfigMap() {
        final HAKafkaConfig.ConsumerConfig consumerConfig = mock(HAKafkaConfig.ConsumerConfig.class);
        when(this.haKafkaConfig.getConsumer()).thenReturn(consumerConfig);
        when(this.haKafkaConfig.getBootstrapAddress()).thenReturn("localhost:9092");
        when(consumerConfig.getGroupId()).thenReturn("groupId");
        final Map<String, Object> actual = this.haKafkaConsumerConfig.getConfigMap();
        assertAll(
                () -> assertThat(actual.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG), is("localhost:9092")),
                () -> assertThat(actual.get(ConsumerConfig.GROUP_ID_CONFIG), is("groupId")),
                () -> assertThat(actual.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG), is(StringDeserializer.class)),
                () -> assertThat(actual.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG), is(StringDeserializer.class))
        );
    }

    @Test
    void testToNodeSeqHeaderName() {
        final Header header1 = mock(Header.class);
        final Header header2 = mock(Header.class);
        when(header1.key()).thenReturn(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ);
        when(header2.key()).thenReturn(HAKafkaConstants.HEADER_SENDING_NODE_SEQ);
        assertTrue(this.haKafkaConsumerConfig.toNodeSeqHeaderName(header1));
        assertFalse(this.haKafkaConsumerConfig.toNodeSeqHeaderName(header2));
    }

    @Test
    void testToNodeSeqHeaderValue() {
        final Header header1 = mock(Header.class);
        final Header header2 = mock(Header.class);
        when(header1.value()).thenReturn("0".getBytes(StandardCharsets.UTF_8));
        when(header2.value()).thenReturn("1".getBytes(StandardCharsets.UTF_8));
        assertTrue(this.haKafkaConsumerConfig.toNodeSeqHeaderValue(header1));
        assertFalse(this.haKafkaConsumerConfig.toNodeSeqHeaderValue(header2));
    }
}
