package com.higgs.common.handler.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpHandlerResponseTest {
    @Test
    void testConstructor() {
        final HttpHandlerRequest requestFor = new HttpHandlerRequest();
        requestFor.setReturnResponse(true);
        requestFor.setFromNodeSeq(1L);
        requestFor.setToNodeSeq(2L);
        requestFor.setFromUsername("fromUsername");
        requestFor.setToUsername("toUsername");

        final HttpHandlerResponse response = new HttpHandlerResponse(requestFor);

        assertAll(
                () -> assertEquals(1L, response.getToNodeSeq().longValue()),
                () -> assertEquals(2L, response.getFromNodeSeq().longValue()),
                () -> assertEquals("toUsername", response.getFromUsername()),
                () -> assertEquals("fromUsername", response.getToUsername()),
                () -> assertTrue(response.isExpected())
        );
    }
}
