package com.higgs.server.kafka.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * todo: maybe unnecessary after pulling the consumer out?
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HAKafkaConstants {
    public static final String HEADER_SENDING_NODE_SEQ = "from_node_seq";

    public static final String HEADER_RECEIVING_NODE_SEQ = "to_node_seq";
}
