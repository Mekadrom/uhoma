package com.higgs.server.kafka.handle;

import org.springframework.util.MultiValueMap;

public interface Handler {

    void handle(final MultiValueMap<String, String> headers, final String message);

}
