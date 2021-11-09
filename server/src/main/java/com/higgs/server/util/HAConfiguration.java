package com.higgs.server.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Constants and variables
 */
@Configuration
public class HAConfiguration {
    // region topic names
    public static final String NODE_MESSAGE_TOPIC_NAME = "${kafka.topics.node-message}";
    public static final String NODE_TELEMETRY_TOPIC_NAME = "${kafka.topics.node-telemetry}";
    public static final String VALUE_KAFKA_BOOTSTRAP_ADDRESS = "${kafka.bootstrap-address}";
    public static final String VALUE_KAFKA_CONSUMER_GROUP_ID = "${kafka.consumer.group-id}";
    // endregion

    // region variables
    public static final String SHOULD_FILTER_CONSUMER = "${kafka.consumer.should-filter-consumer}";
    // endregion

    // region bean values
    @Value(value = HAConfiguration.NODE_MESSAGE_TOPIC_NAME)
    private String nodeMessageTopicName;
    // endregion

    // region beans
    @Bean
    public String nodeMessageTopicName() {
        return this.nodeMessageTopicName;
    }
    // endregion
}
