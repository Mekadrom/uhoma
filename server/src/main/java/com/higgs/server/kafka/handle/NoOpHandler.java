package com.higgs.server.kafka.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class NoOpHandler implements Handler {
    @Override
    public void handle(final MultiValueMap<String, String> headers, final String message) {
        NoOpHandler.log.debug("somebody sent a message to the main server");
    }
}
