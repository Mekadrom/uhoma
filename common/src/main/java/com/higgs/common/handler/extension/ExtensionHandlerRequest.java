package com.higgs.common.handler.extension;

import com.higgs.common.handler.HandlerRequest;

import java.io.Serial;
import java.util.Map;

public class ExtensionHandlerRequest extends HandlerRequest {
    @Serial
    private static final long serialVersionUID = 182L;

    public ExtensionHandlerRequest(final Map<String, Object> input) {
        super(input);
    }
}
