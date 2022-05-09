package com.higgs.common.handler;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class HandlerRequestTest {
    @Test
    void testConstructor() {
        assertThat(new HandlerRequest(Map.of("key", "value")) {}.get("key"), is("value"));
    }
}
