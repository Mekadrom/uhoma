package com.higgs.common.kafka;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerProducerTest {
    @Mock
    private HAKafkaConfig haKafkaConfig;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ServerProducer serverProducer;

    @BeforeEach
    void setUp() {
        this.serverProducer = new ServerProducer(this.haKafkaConfig, this.kafkaTemplate);
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testSendKafkaProducerEnum() {
        final ServerProducer serverProducerSpy = spy(this.serverProducer);
        final Future<SendResult<String, String>> future = mock(Future.class);
        final KafkaTopicEnum kafkaTopicEnum = mock(KafkaTopicEnum.class);
        final String message = "body";
        final Map<String, Object> headers = Map.of("header", "value");
        final ObjectMapper objectMapper = mock(ObjectMapper.class);
        final Function<Object, String> keyMakerFunc = mock(Function.class);
        doReturn(future).when(serverProducerSpy).send(any(), any(), any(), any(), any());
        when(this.haKafkaConfig.resolveTopicKeyReference(any())).thenReturn("topic");
        when(kafkaTopicEnum.getTopicKey()).thenReturn("topic");
        when(kafkaTopicEnum.getBodySerializer()).thenReturn(objectMapper);
        when(kafkaTopicEnum.getHeaderSerializer()).thenReturn(objectMapper);
        when(kafkaTopicEnum.getKeyMakerFunc()).thenReturn(keyMakerFunc);
        when(objectMapper.writeValueAsString(any())).thenReturn(message);
        final Future<SendResult<String, String>> actual = serverProducerSpy.send(kafkaTopicEnum, message, headers);
        verify(kafkaTopicEnum, times(1)).getTopicKey();
        verify(kafkaTopicEnum, times(1)).getKeyMakerFunc();
        verify(keyMakerFunc, times(1)).apply(any());
        verify(kafkaTopicEnum, times(1)).getBodySerializer();
        verify(objectMapper, times(1)).writeValueAsString(message);
        verify(kafkaTopicEnum, times(1)).getHeaderSerializer();
        verify(serverProducerSpy).send("topic", null, message, headers, objectMapper);
        assertThat(actual, is(future));
    }

    @ParameterizedTest
    @MethodSource("getTestSendKafkaProducerEnumInvalidArgsParams")
    void testSendKafkaProducerEnumInvalidArgs(final KafkaTopicEnum kafkaTopicEnum, final Map<String, Object> headers) {
        assertThrows(IllegalArgumentException.class, () -> this.serverProducer.send(kafkaTopicEnum, null, headers));
    }

    public static Stream<Arguments> getTestSendKafkaProducerEnumInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(mock(KafkaTopicEnum.class), null),
                Arguments.of(null, Collections.emptyMap())
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSend() {
        final ServerProducer serverProducerSpy = spy(this.serverProducer);
        final ObjectMapper objectMapper = mock(ObjectMapper.class);
        final ListenableFuture<SendResult<String, String>> future = mock(ListenableFuture.class);
        doReturn(new ArrayList<>()).when(serverProducerSpy).convertHeaders(any(), any());
        when(this.kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        final Future<SendResult<String, String>> actual = serverProducerSpy.send("topic", null, "body", Collections.emptyMap(), objectMapper);
        verify(this.kafkaTemplate, times(1)).send(any(ProducerRecord.class));
        verify(future, times(1)).addCallback(any(), any());
        verify(serverProducerSpy, times(1)).convertHeaders(any(), any());
        assertThat(actual, is(future));
    }

    @ParameterizedTest
    @MethodSource("getTestSendInvalidArgsParams")
    void testSendInvalidArgs(final Map<String, Object> headers, final ObjectMapper objectMapper) {
        assertThrows(IllegalArgumentException.class, () -> this.serverProducer.send("topic", null, "body", headers, objectMapper));
    }

    public static Stream<Arguments> getTestSendInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Collections.emptyMap(), null),
                Arguments.of(null, mock(ObjectMapper.class))
        );
    }

    @Test
    @SneakyThrows
    void testConvertHeaders() {
        final Map<String, Object> headers = Map.of("header", "value");
        final ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(any())).thenReturn("value");
        final Collection<Header> actual = this.serverProducer.convertHeaders(headers, objectMapper);
        assertThat(actual.size(), is(1));
        final Header firstActual = actual.iterator().next();
        assertThat(firstActual.key(), is("header"));
        assertThat(firstActual.value(), is("value".getBytes(StandardCharsets.UTF_8)));
    }

    @ParameterizedTest
    @MethodSource("getTestConvertHeadersInvalidArgsParams")
    void testConvertHeadersInvalidArgs(final Map<String, Object> headers, final ObjectMapper objectMapper) {
        assertThrows(IllegalArgumentException.class, () -> this.serverProducer.convertHeaders(headers, objectMapper));
    }

    public static Stream<Arguments> getTestConvertHeadersInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Collections.emptyMap(), null),
                Arguments.of(null, mock(ObjectMapper.class))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSuccessCallback() {
        final SendResult<String, String> sendResult = mock(SendResult.class);
        final RecordMetadata recordMetadata = mock(RecordMetadata.class);
        final ProducerRecord<String, String> producerRecord = mock(ProducerRecord.class);
        when(producerRecord.topic()).thenReturn("topic");
        when(sendResult.getProducerRecord()).thenReturn(producerRecord);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        this.serverProducer.successCallback(sendResult);
        verify(producerRecord, times(1)).topic();
        verify(sendResult, times(2)).getRecordMetadata();
        verify(recordMetadata, times(1)).partition();
        verify(recordMetadata, times(1)).offset();
    }

    @Test
    void testFailureCallback() {
        final Throwable throwable = mock(Throwable.class);
        this.serverProducer.failureCallback(throwable);
        verify(throwable, times(2)).getMessage();
    }

    @Test
    @SneakyThrows
    void testInnerHeaderClassFailsSoft() {
        final Map.Entry<String, Object> entry = Map.entry("header", "value");
        final ObjectMapper objectMapper = mock(ObjectMapper.class);

        // json mapping exception is only subclass of JsonProcessingException that can be instantiated outside its package
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonMappingException(null, ""));
        assertThat(this.serverProducer.convertHeader(entry, objectMapper).value(), is(new byte[0]));
    }
}
