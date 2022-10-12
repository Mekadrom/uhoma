package com.higgs.common.handler;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandlerRequestTest {
    @Test
    void testConstructor() {
        assertThat(new HandlerRequest(Map.of("key", "value")) {}.get("key"), is("value"));
    }

    @Test
    void testIsReturnResponse() {
        assertTrue(new HandlerRequest(Map.of("return_response", true)) {}.isReturnResponse());
        assertFalse(new HandlerRequest(Map.of("return_response", false)) {}.isReturnResponse());
    }
}
