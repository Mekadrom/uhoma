package com.higgs.common.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class KafkaTopicEnumTest {
    @Test
    void testNodeAction() {
        assertAll(
                () -> assertThat(KafkaTopicEnum.NODE_ACTION.getTopicKey(), is(KafkaTopicEnum.NODE_ACTION_TOPIC_KEY)),
                () -> assertNotNull(KafkaTopicEnum.NODE_ACTION.getKeyMakerFunc()),
                () -> assertNull(KafkaTopicEnum.NODE_ACTION.getKeyMakerFunc().apply("")),
                () -> assertThat(KafkaTopicEnum.NODE_ACTION.getBodyMapper().getClass(), is(ObjectMapper.class)),
                () -> assertThat(KafkaTopicEnum.NODE_ACTION.getHeaderMapper().getClass(), is(ObjectMapper.class))
        );
    }

    @Test
    void testNodeResponse() {
        assertAll(
                () -> assertThat(KafkaTopicEnum.NODE_RESPONSE.getTopicKey(), is(KafkaTopicEnum.NODE_RESPONSE_TOPIC_KEY)),
                () -> assertNotNull(KafkaTopicEnum.NODE_RESPONSE.getKeyMakerFunc()),
                () -> assertNull(KafkaTopicEnum.NODE_RESPONSE.getKeyMakerFunc().apply("")),
                () -> assertThat(KafkaTopicEnum.NODE_RESPONSE.getBodyMapper().getClass(), is(ObjectMapper.class)),
                () -> assertThat(KafkaTopicEnum.NODE_RESPONSE.getHeaderMapper().getClass(), is(ObjectMapper.class))
        );
    }

    @Test
    void testNodeTelemetry() {
        assertAll(
                () -> assertThat(KafkaTopicEnum.NODE_TELEMETRY.getTopicKey(), is(KafkaTopicEnum.NODE_TELEMETRY_TOPIC_KEY)),
                () -> assertNotNull(KafkaTopicEnum.NODE_TELEMETRY.getKeyMakerFunc()),
                () -> assertNull(KafkaTopicEnum.NODE_TELEMETRY.getKeyMakerFunc().apply("")),
                () -> assertThat(KafkaTopicEnum.NODE_TELEMETRY.getBodyMapper().getClass(), is(ObjectMapper.class)),
                () -> assertThat(KafkaTopicEnum.NODE_TELEMETRY.getHeaderMapper().getClass(), is(ObjectMapper.class))
        );
    }
}
