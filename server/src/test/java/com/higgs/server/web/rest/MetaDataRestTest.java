package com.higgs.server.web.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.info.BuildProperties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link MetaDataRest}.
 */
@ExtendWith(MockitoExtension.class)
class MetaDataRestTest {
    @Mock
    private BuildProperties buildProperties;

    private MetaDataRest metaDataRest;

    @BeforeEach
    void setUp() {
        this.metaDataRest = new MetaDataRest(this.buildProperties);
    }

    /**
     * Test for {@link MetaDataRest#getServerVersion()}. Should return the version of the server, as provided by the
     * {@link BuildProperties}.
     */
    @Test
    void testGetServerVersion() {
        when(this.buildProperties.getVersion()).thenReturn("1.0.0");
        assertThat(this.metaDataRest.getServerVersion().getBody(), is(equalTo("1.0.0")));
    }
}
