package com.higgs.server.db.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link Home}
 */
class HomeTest {
    /**
     * Tests the method {@link Home#populateCreated()} and verifies that it sets the created date on the {@link Home}
     */
    @Test
    void testPopulateCreates() {
        final Home home = new Home();
        home.populateCreated();
        assertNotNull(home.getCreated());
    }
}
