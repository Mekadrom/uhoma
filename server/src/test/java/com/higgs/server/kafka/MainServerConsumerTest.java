package com.higgs.server.kafka;

import com.higgs.common.kafka.HAKafkaConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link MainServerConsumer}.
 */
@ExtendWith(MockitoExtension.class)
class MainServerConsumerTest {
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    private MainServerConsumer mainServerConsumer;

    @BeforeEach
    void setUp() {
        this.mainServerConsumer = new MainServerConsumer(this.simpMessagingTemplate);
    }

    /**
     * Tests that the {@link MainServerConsumer#listenNodeMessageTopic(MultiValueMap, String)} method delegates to
     * {@link MainServerConsumer#transmit(MultiValueMap, String)} with the correct inputs.
     */
    @Test
    void testListenNodeMessageTopic() {
        final MainServerConsumer mainServerConsumer = new MainServerConsumer(this.simpMessagingTemplate);
        final MainServerConsumer mainServerConsumerSpy = spy(mainServerConsumer);
        final MessageHeaders headers = mock(MessageHeaders.class);
        mainServerConsumerSpy.listenNodeMessageTopic(headers, "test");
        verify(mainServerConsumerSpy).transmit(headers, "test");
    }

    /**
     * Tests that the {@link MainServerConsumer#transmit(MultiValueMap, String)} method delegates to the
     * {@link SimpMessagingTemplate#convertAndSendToUser(String, String, Object, Map)} method with the correct inputs.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testTransmit() {
        final MessageHeaders headers = mock(MessageHeaders.class);
        final Map<String, Object> map = new HashMap<>();
        when(this.commonUtils.toObjectMap(any())).thenReturn(map);
        this.mainServerConsumer.transmit(headers, "test");
        verify(this.simpMessagingTemplate, times(1)).convertAndSendToUser(null, "queue/reply", "test", map);
    }

    /**
     * Tests that the {@link MainServerConsumer#transmit(MultiValueMap, String)} method correctly extracts the receiving
     * node from the headers of the kafka event.
     */
    @Test
    void testExtractUser() {
        final MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ)).thenReturn(List.of("1"));
        assertThat(this.mainServerConsumer.extractUser(headers), is(equalTo("1")));
    }
}
