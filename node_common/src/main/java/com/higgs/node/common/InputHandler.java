package com.higgs.node.common;

import org.springframework.util.MultiValueMap;

public interface InputHandler {

    void handle(final MultiValueMap<String, String> headers, final String message);

}
