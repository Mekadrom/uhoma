package com.higgs.common.handler.extension;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensionHandlerRequestTest {
    @Test
    void testConstructorCallsSuper() {
        final ExtensionHandlerRequest request = new ExtensionHandlerRequest(Map.of("test", "value"));
        assertEquals("value", request.get("test"));
    }
}
