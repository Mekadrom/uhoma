package com.higgs.actionserver.kafka;

import com.higgs.common.handler.HandlerHandler;
import com.higgs.common.handler.HandlerResponse;
import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionServerConsumerTest {
    @Mock
    private ServerProducer serverProducer;

    @Mock
    private HandlerHandler handlerHandler;

    private ActionServerConsumer actionServerConsumer;

    @BeforeEach
    void setUp() {
        this.actionServerConsumer = new ActionServerConsumer(this.serverProducer, this.handlerHandler);
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testListenNodeMessageTopic() {
        final ActionServerConsumer actionServerConsumerSpy = spy(this.actionServerConsumer);
        final MultiValueMap<String, String> headers = mock(MultiValueMap.class);

        actionServerConsumerSpy.listenNodeMessageTopic(headers, "testmessage");

        verify(this.handlerHandler, times(1)).process(headers, "testmessage");
        verify(actionServerConsumerSpy, times(1)).handleResponses(any());
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testListenNodeMessageTopicSwallowsException() {
        final ActionServerConsumer actionServerConsumerSpy = spy(this.actionServerConsumer);
        final MultiValueMap<String, String> headers = mock(MultiValueMap.class);

        doThrow(IOException.class).when(this.handlerHandler).process(headers, "testmessage");

        assertDoesNotThrow(() -> actionServerConsumerSpy.listenNodeMessageTopic(headers, "testmessage"));

        verify(this.handlerHandler, times(1)).process(headers, "testmessage");
        verify(actionServerConsumerSpy, times(0)).handleResponses(any());
    }

    @Test
    void testHandleResponses() {
        final ActionServerConsumer actionServerConsumerSpy = spy(this.actionServerConsumer);
        final HandlerResponse notExpected = mock(HandlerResponse.class);
        final HandlerResponse expected = mock(HandlerResponse.class);
        final List<? extends HandlerResponse> responses = List.of(notExpected, expected);

        when(notExpected.isExpected()).thenReturn(false);
        when(expected.isExpected()).thenReturn(true);

        doNothing().when(actionServerConsumerSpy).sendResponse(any());

        actionServerConsumerSpy.handleResponses(responses);

        verify(notExpected, times(1)).isExpected();
        verify(expected, times(1)).isExpected();
        verify(actionServerConsumerSpy, times(1)).sendResponse(expected);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testHandleResponsesNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> this.actionServerConsumer.handleResponses(null));
    }

    @Test
    @SneakyThrows
    void testSendResponse() {
        final ActionServerConsumer actionServerConsumerSpy = spy(this.actionServerConsumer);
        final HandlerResponse response = mock(HandlerResponse.class);

        doReturn(Map.of()).when(actionServerConsumerSpy).buildResponseHeaders(any());

        actionServerConsumerSpy.sendResponse(response);

        verify(this.serverProducer, times(1)).send(eq(KafkaTopicEnum.NODE_RESPONSE), eq(response), any());
    }

    @Test
    @SneakyThrows
    void testSendResponseSwallowsException() {
        final ActionServerConsumer actionServerConsumerSpy = spy(this.actionServerConsumer);
        final HandlerResponse response = mock(HandlerResponse.class);

        doReturn(Map.of()).when(actionServerConsumerSpy).buildResponseHeaders(any());
        doThrow(IOException.class).when(this.serverProducer).send(eq(KafkaTopicEnum.NODE_RESPONSE), eq(response), any());

        assertDoesNotThrow(() -> actionServerConsumerSpy.sendResponse(response));

        verify(this.serverProducer, times(1)).send(eq(KafkaTopicEnum.NODE_RESPONSE), eq(response), any());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testSendResponseNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> this.actionServerConsumer.sendResponse(null));
    }

    @Test
    void testBuildResponseHeaders() {
        final ActionServerConsumer actionServerConsumerSpy = spy(this.actionServerConsumer);
        final HandlerResponse response = mock(HandlerResponse.class);

        when(response.getToNodeSeq()).thenReturn(1L);
        when(response.getFromNodeSeq()).thenReturn(2L);
        when(response.getToUsername()).thenReturn("recuser");
        when(response.getFromUsername()).thenReturn("senduser");

        final Map<String, Object> headers = actionServerConsumerSpy.buildResponseHeaders(response);

        assertEquals(4, headers.size());
        assertEquals(1L, headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ));
        assertEquals(2L, headers.get(HAKafkaConstants.HEADER_SENDING_NODE_SEQ));
        assertEquals("recuser", headers.get(HAKafkaConstants.HEADER_RECEIVING_USERNAME));
        assertEquals("senduser", headers.get(HAKafkaConstants.HEADER_SENDING_USERNAME));
        assertThrows(UnsupportedOperationException.class, () -> headers.put("test", "test"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testBuildResponseHeadersNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> this.actionServerConsumer.buildResponseHeaders(null));
    }
}
