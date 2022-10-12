package com.higgs.server.kafka;

import com.higgs.common.kafka.HAKafkaConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
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
     * Tests that the {@link MainServerConsumer#listenNodeMessageTopic(MessageHeaders, String)} method delegates to
     * {@link MainServerConsumer#transmit(MessageHeaders, String)} with the correct inputs.
     */
    @Test
    void testListenNodeMessageTopic() {
        final MainServerConsumer mainServerConsumerSpy = spy(this.mainServerConsumer);
        final MessageHeaders headers = mock(MessageHeaders.class);
        doReturn("user").when(mainServerConsumerSpy).extractUser(headers);
        mainServerConsumerSpy.listenNodeMessageTopic(headers, "test");
        verify(mainServerConsumerSpy).transmit(headers, "test");
    }

    /**
     * Tests that the {@link MainServerConsumer#transmit(MessageHeaders, String)} method delegates to the
     * {@link SimpMessagingTemplate#convertAndSendToUser(String, String, Object, Map)} method with the correct inputs.
     */
    @Test
    void testTransmit() {
        final MainServerConsumer mainServerConsumerSpy = spy(this.mainServerConsumer);
        final MessageHeaders headers = mock(MessageHeaders.class);
        final Map<String, Object> map = new HashMap<>();
        doReturn("user").when(mainServerConsumerSpy).extractUser(headers);
        mainServerConsumerSpy.transmit(headers, "test");
        verify(this.simpMessagingTemplate, times(1)).convertAndSendToUser("user", "queue/reply", "test", map);
    }

    /**
     * Tests that the {@link MainServerConsumer#transmit(MessageHeaders, String)} method correctly extracts the receiving
     * node from the headers of the kafka event.
     */
    @Test
    void testExtractUser() {
        final MessageHeaders headers = mock(MessageHeaders.class);
        when(headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ)).thenReturn("1".getBytes(StandardCharsets.UTF_8));
        assertThat(this.mainServerConsumer.extractUser(headers), is(equalTo("1")));
    }

    @NullSource
    @ParameterizedTest
    void testExtractUserInvalidArgs(final MessageHeaders nullValue) {
        assertThrows(IllegalArgumentException.class, () -> this.mainServerConsumer.extractUser(nullValue));
    }
}
