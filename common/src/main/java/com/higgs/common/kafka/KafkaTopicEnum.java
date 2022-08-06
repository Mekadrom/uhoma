package com.higgs.common.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum KafkaTopicEnum {
    NODE_ACTION(KafkaTopicEnum.NODE_ACTION_TOPIC_KEY, o -> null, new ObjectMapper(), new ObjectMapper()),
    NODE_RESPONSE(KafkaTopicEnum.NODE_RESPONSE_TOPIC_KEY, o -> null, KafkaSerializerProvider.getNodeResponseBodySerializer(), new ObjectMapper()),
    NODE_TELEMETRY(KafkaTopicEnum.NODE_TELEMETRY_TOPIC_KEY, o -> null, new ObjectMapper(), new ObjectMapper());

    public static final String NODE_ACTION_TOPIC_KEY = "node-action";
    public static final String NODE_RESPONSE_TOPIC_KEY = "node-response";
    public static final String NODE_TELEMETRY_TOPIC_KEY = "node-telemetry";

    private final String topicKey;
    private final Function<Object, String> keyMakerFunc;
    private final ObjectMapper bodyMapper;
    private final ObjectMapper headerMapper;
}
