package com.higgs.server.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum KafkaTopicEnum {
    NODE_MESSAGE("node-message", (o) -> null, new ObjectMapper(), new ObjectMapper()),
    NODE_TELEMETRY("node-telemetry", (o) -> null, new ObjectMapper(), new ObjectMapper());

    private final String topicKey;
    private final Function<Object, String> keyMakerFunc;
    private final ObjectMapper bodySerializer;
    private final ObjectMapper headerSerializer;
}
