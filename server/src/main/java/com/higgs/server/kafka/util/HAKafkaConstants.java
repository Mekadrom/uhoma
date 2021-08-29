package com.higgs.server.kafka.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HAKafkaConstants {
    public static final String HEADER_RECEIVING_NODE_SEQ = "RECEIVING_NODE_SEQ";

    public static final String HEADER_SENDING_NODE_SEQ = "SENDING_NODE_SEQ";

    public static final String HEADER_PRODUCER_EVENT_KEY = "PRODUCER_EVENT_KEY";
}
