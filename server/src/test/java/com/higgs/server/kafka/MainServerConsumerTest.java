package com.higgs.server.kafka;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.util.CommonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private CommonUtils commonUtils;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    private MainServerConsumer mainServerConsumer;

    @BeforeEach
    void setUp() {
        this.mainServerConsumer = new MainServerConsumer(this.commonUtils, this.simpMessagingTemplate);
    }

    /**
     * Tests that the {@link MainServerConsumer#listenNodeMessageTopic(MultiValueMap, String)} method delegates to
     * {@link MainServerConsumer#transmit(MultiValueMap, String)} with the correct inputs.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testListenNodeMessageTopic() {
        final MainServerConsumer mainServerConsumer = new MainServerConsumer(this.commonUtils, this.simpMessagingTemplate);
        final MainServerConsumer mainServerConsumerSpy = spy(mainServerConsumer);
        final MultiValueMap<String, String> headers = (MultiValueMap<String, String>) mock(MultiValueMap.class);
        mainServerConsumerSpy.listenNodeMessageTopic(headers, "test");
        verify(mainServerConsumerSpy).transmit(headers, "test");
    }

    /**
     * Tests that the {@link MainServerConsumer#transmit(MultiValueMap, String)} method delegates to the
     * {@link SimpMessagingTemplate#convertAndSendToUser(String, String, Object, Map)} method with the correct inputs.
     */
    @Test
    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    void testTransmit() {
        final MultiValueMap<String, String> headers = (MultiValueMap<String, String>) mock(MultiValueMap.class);
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
    @SuppressWarnings("unchecked")
    void testExtractUser() {
        final MultiValueMap<String, String> headers = (MultiValueMap<String, String>) mock(MultiValueMap.class);
        when(headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ)).thenReturn(List.of("1"));
        assertThat(this.mainServerConsumer.extractUser(headers), is(equalTo("1")));
    }
}
