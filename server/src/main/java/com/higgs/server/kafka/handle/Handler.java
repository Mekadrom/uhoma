package com.higgs.server.kafka.handle;

import java.util.List;
import java.util.Map;

/**
 * todo: move out when consumer gets moved out
 */
public interface Handler {

    void handle(final Map<String, List<String>> headers, final String message);

}
