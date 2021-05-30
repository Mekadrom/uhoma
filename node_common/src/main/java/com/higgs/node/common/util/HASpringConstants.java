package com.higgs.node.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HASpringConstants {
    // region topic names
    public static final String NODE_MESSAGE_TOPIC_NAME = "${kafka.topics.node-message}";
    // endregion

    // region kafka config
    public static final String VALUE_KAFKA_BOOTSTRAP_ADDRESS = "${kafka.bootstrap-address}";
    public static final String VALUE_KAFKA_CONSUMER_GROUP_ID = "${kafka.consumer.group-id}";
    // endregion

    // region server metadata
    public static final String NODE_NAME = "${node.name}";
    public static final String SERVER_VERSION = "${server.version}";
    //endregion

    // region variables
    public static final String SHOULD_FILTER_CONSUMER = "${kafka.consumer.should-filter-consumer}";
    // endregion
}
