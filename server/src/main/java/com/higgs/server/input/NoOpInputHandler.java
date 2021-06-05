package com.higgs.server.input;

import com.higgs.node.common.InputHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
public class NoOpInputHandler implements InputHandler {
    @Override
    public void handle(final MultiValueMap<String, String> headers, final String message) {
        NoOpInputHandler.log.debug("somebody sent a message to the main server");
    }
}
