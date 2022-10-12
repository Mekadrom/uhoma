package com.higgs.common.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandlerResponseTest {

    @Test
    void testIsExpected() {
        final HandlerResponse handlerResponse = new HandlerResponse();
        handlerResponse.put("expected", true);
        assertTrue(handlerResponse.isExpected());
        handlerResponse.put("expected", false);
        assertFalse(handlerResponse.isExpected());
    }
}
