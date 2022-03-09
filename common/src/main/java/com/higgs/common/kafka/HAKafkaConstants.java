package com.higgs.common.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HAKafkaConstants {
    public static final String HEADER_ACTION_HANDLER_DEF = "action_handler_def";

    public static final String HEADER_RECEIVING_NODE_SEQ = "to_node_seq";

    public static final String HEADER_SENDING_NODE_SEQ = "from_node_seq";

    public static final String HEADER_RECEIVING_USERNAME = "to_username";

    public static final String HEADER_SENDING_USERNAME = "from_username";
}
