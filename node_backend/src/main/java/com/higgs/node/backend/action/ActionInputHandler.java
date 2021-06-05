package com.higgs.node.backend.action;

import com.higgs.node.common.InputHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class ActionInputHandler implements InputHandler {
    @Override
    public void handle(final MultiValueMap<String, String> headers, final String message) {

    }
}
