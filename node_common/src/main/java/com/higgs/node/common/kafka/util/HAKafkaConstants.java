package com.higgs.node.common.kafka.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HAKafkaConstants {
    public static final String HEADER_RECEIVING_NODE_NAME = "RECEIVING_NODE_NAME";

    public static final String HEADER_SENDING_NODE_NAME = "SENDING_NODE_NAME";

    public static final String HEADER_PRODUCER_EVENT_KEY = "PRODUCER_EVENT_KEY";
}
