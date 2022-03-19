package com.higgs.simulator.httpsim.web.rest;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import com.higgs.simulator.httpsim.util.JsonUtils;
import com.higgs.simulator.httpsim.web.svc.ProfileService;
import com.higgs.simulator.httpsim.web.svc.ResponseBodyService;
import com.higgs.simulator.httpsim.web.svc.ResponseGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonProfileHttpRestControllerTest {
    @Mock
    private ProfileService profileService;

    @Mock
    private ResponseBodyService responseBodyService;

    @Mock
    private ResponseGroupService responseGroupService;

    private JsonProfileHttpRestController jsonProfileHttpRestController;

    @BeforeEach
    void setUp() {
        this.jsonProfileHttpRestController = new JsonProfileHttpRestController(this.profileService, this.responseBodyService, this.responseGroupService);
    }

    @Test
    void testGetResponse() {
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final ResponseBody responseBody = mock(ResponseBody.class);
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.of(profile));
        when(this.responseGroupService.findByProfileAndEndpointOrDefault(profile, "endpoint")).thenReturn(responseGroup);
        when(this.responseBodyService.findByResponseGroupAndKeyedFields(responseGroup, Map.of("bruh", "test"))).thenReturn(Optional.of(responseBody));
        when(responseBody.getBody()).thenReturn("{\"bruh1\":\"test1\"}");
        when(responseBody.getHeaders()).thenReturn(Map.of("header1", "header1"));
        when(responseBody.getResponseCode()).thenReturn(HttpStatus.OK.value());
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.getResponse("test", "endpoint", "{\"bruh\":\"test\"}");
        verify(this.profileService, times(1)).findByEndpoint("test");
        verify(this.responseGroupService, times(1)).findByProfileAndEndpointOrDefault(profile, "endpoint");
        verify(this.responseBodyService, times(1)).findByResponseGroupAndKeyedFields(responseGroup, Map.of("bruh", "test"));
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(actual.getBody(), is("{\"bruh1\":\"test1\"}")),
                () -> assertThat(actual.getHeaders().get("header1"), is(List.of("[header1]")))
        );
    }

    @Test
    void testGetResponseResponseBodyNotFound() {
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.of(profile));
        when(this.responseGroupService.findByProfileAndEndpointOrDefault(profile, "endpoint")).thenReturn(responseGroup);
        when(this.responseBodyService.findByResponseGroupAndKeyedFields(responseGroup, Map.of("bruh", "test"))).thenReturn(Optional.empty());
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.getResponse("test", "endpoint", "{\"bruh\":\"test\"}");
        verify(this.profileService, times(1)).findByEndpoint("test");
        verify(this.responseGroupService, times(1)).findByProfileAndEndpointOrDefault(profile, "endpoint");
        verify(this.responseBodyService, times(1)).findByResponseGroupAndKeyedFields(responseGroup, Map.of("bruh", "test"));
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Response body not found at /test/endpoint with request {\"bruh\":\"test\"}\"}"))
        );
    }

    @Test
    void testGetResponseProfileNotFound() {
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.empty());
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.getResponse("test", "endpoint", "{\"bruh\":\"test\"}");
        verify(this.profileService, times(1)).findByEndpoint("test");
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Profile not found at /test\"}"))
        );
    }

    @Test
    void testGetProfile() {
        final Profile profile = new Profile();
        profile.setEndpoint("test");
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.of(profile));
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.getProfile("test");
        verify(this.profileService, times(1)).findByEndpoint("test");
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(actual.getBody(), is(JsonUtils.serialize(profile)))
        );
    }

    @Test
    void testGetProfileNotFound() {
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.empty());
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.getProfile("test");
        verify(this.profileService, times(1)).findByEndpoint("test");
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.NOT_FOUND)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Profile not found at /test\"}"))
        );
    }

    @Test
    void testPutPostProfile() {
        final Profile profile = new Profile();
        profile.setEndpoint("test");
        profile.setKeyedFields(Set.of("bruh"));
        when(this.profileService.createNewProfile(any(), any())).thenReturn(profile);
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.putPostProfile("test", "{\"bruh\":\"test\"}");
        verify(this.profileService, times(1)).createNewProfile("test", Set.of("bruh"));
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(actual.getBody(), is(JsonUtils.serialize(profile)))
        );
    }

    @Test
    void testPutPostProfileAlreadyExists() {
        final Profile profile = new Profile();
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.of(profile));
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.putPostProfile("test", "{\"bruh\":\"test\"}");
        verify(this.profileService, times(1)).findByEndpoint("test");
        verify(this.profileService, times(0)).createNewProfile("test", Set.of("bruh"));
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Profile already exists at /test\"}"))
        );
    }

    @Test
    void testPutPostMapping() {
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final ResponseBody responseBody = new ResponseBody();
        responseBody.setBody("{\"bruh\":\"test\"}");
        responseBody.setResponseCode(HttpStatus.OK.value());
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.of(profile));
        when(this.responseGroupService.findByProfileAndEndpointOrDefault(any(), any())).thenReturn(responseGroup);
        when(this.responseBodyService.findByResponseGroupAndKeyedFieldsOrDefault(any(), any(), any(), any())).thenReturn(responseBody);
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.putPostMapping("test", "endpoint", HttpStatus.OK.value(), Map.of("header", "value"), responseBody.getBody());
        verify(this.profileService, times(1)).findByEndpoint("test");
        verify(this.responseGroupService, times(1)).findByProfileAndEndpointOrDefault(profile, "endpoint");
        verify(this.responseBodyService, times(1)).findByResponseGroupAndKeyedFieldsOrDefault(responseGroup, Map.of("bruh", "test"), Map.of("header", "value"), HttpStatus.OK.value());
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.OK)),
                () -> assertThat(actual.getBody(), is(JsonUtils.serialize(responseBody)))
        );
    }

    @Test
    void testPutPostMappingResponseBodyNotCreatedOrFound() {
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.of(profile));
        when(this.responseGroupService.findByProfileAndEndpointOrDefault(any(), any())).thenReturn(responseGroup);
        when(this.responseBodyService.findByResponseGroupAndKeyedFieldsOrDefault(any(), any(), any(), any())).thenReturn(null);
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.putPostMapping("test", "endpoint", HttpStatus.OK.value(), Map.of("header", "value"), "{\"bruh\":\"test\"}", Map.of("bruh", "test"));
        verify(this.profileService, times(1)).findByEndpoint("test");
        verify(this.responseGroupService, times(1)).findByProfileAndEndpointOrDefault(profile, "endpoint");
        verify(this.responseBodyService, times(1)).findByResponseGroupAndKeyedFieldsOrDefault(responseGroup, Map.of("bruh", "test"), Map.of("header", "value"), HttpStatus.OK.value());
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Unable to set response body for request {\"bruh\":\"test\"}\"}"))
        );
    }

    @Test
    void testPutPostMappingProfileNotFound() {
        when(this.profileService.findByEndpoint(any())).thenReturn(Optional.empty());
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.putPostMapping("test", "endpoint", HttpStatus.OK.value(), Map.of("header", "value"), "{\"bruh\":\"test\"}", Map.of("bruh", "test"));
        verify(this.profileService, times(1)).findByEndpoint("test");
        verify(this.responseGroupService, times(0)).findByProfileAndEndpointOrDefault(any(), any());
        verify(this.responseBodyService, times(0)).findByResponseGroupAndKeyedFieldsOrDefault(any(), any(), any(), any());
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Unable to set response body for request {\"bruh\":\"test\"}\"}"))
        );
    }

    @Test
    void testPutPostMappingKeyedFieldsNull() {
        final ResponseEntity<String> actual = this.jsonProfileHttpRestController.putPostMapping("test", "endpoint", HttpStatus.OK.value(), Map.of("header", "value"), "{\"bruh\":\"test\"}", null);
        verify(this.profileService, times(0)).findByEndpoint("test");
        verify(this.responseGroupService, times(0)).findByProfileAndEndpointOrDefault(any(), any());
        verify(this.responseBodyService, times(0)).findByResponseGroupAndKeyedFieldsOrDefault(any(), any(), any(), any());
        assertAll(
                () -> assertThat(actual.getStatusCode(), is(HttpStatus.BAD_REQUEST)),
                () -> assertThat(actual.getBody(), is("{\"error\":\"Unable to set response body for request {\"bruh\":\"test\"}\"}"))
        );
    }
}
