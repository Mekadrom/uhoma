package com.higgs.simulator.httpsim.web.svc;

import com.higgs.simulator.httpsim.db.entity.Profile;
import com.higgs.simulator.httpsim.db.entity.ResponseBody;
import com.higgs.simulator.httpsim.db.entity.ResponseGroup;
import com.higgs.simulator.httpsim.db.repo.ResponseBodyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseBodyServiceTest {
    @Mock
    private ResponseBodyRepository responseBodyRepository;

    private ResponseBodyService responseBodyService;

    @BeforeEach
    void setUp() {
        this.responseBodyService = new ResponseBodyService(this.responseBodyRepository);
    }

    @Test
    void testFindByResponseGroupAndKeyedFieldsOrDefault() {
        final Profile profile = new Profile();
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        final Map<String, Object> body = Map.of("hurb", "tset");
        final Map<String, Object> headers = Map.of("test", "bruh");
        profile.setKeyedFields(Set.of("bruh"));
        doReturn(Optional.of(responseBody)).when(responseBodyServiceSpy).findByResponseGroupAndKeyedFields(any(), any());
        assertThat(responseBodyServiceSpy.findByResponseGroupAndKeyedFieldsOrDefault(profile, responseGroup, keyedFields, body, headers, 200), is(equalTo(responseBody)));
        verify(responseBodyServiceSpy, times(1)).findByResponseGroupAndKeyedFields(responseGroup, keyedFields);
        verify(responseBodyServiceSpy, times(0)).createResponseBody(profile, responseGroup, keyedFields, body, headers, 200);
    }

    @Test
    void testFindByResponseGroupAndKeyedFieldsOrDefaultDefaults() {
        final Profile profile = new Profile();
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        final Map<String, Object> body = Map.of("hurb", "tset");
        final Map<String, Object> headers = Map.of("test", "bruh");
        profile.setKeyedFields(Set.of("bruh"));
        when(responseGroup.getResponseGroupSeq()).thenReturn(1L);
        doReturn(responseBody).when(responseBodyServiceSpy).createResponseBody(any(), any(), any(), any(), any(), any());
        assertThat(responseBodyServiceSpy.findByResponseGroupAndKeyedFieldsOrDefault(profile, responseGroup, keyedFields, body, headers, 200), is(equalTo(responseBody)));
        verify(responseBodyServiceSpy, times(1)).findByResponseGroupAndKeyedFields(responseGroup, keyedFields);
        verify(responseBodyServiceSpy, times(1)).createResponseBody(profile, responseGroup, keyedFields, body, headers, 200);
    }

    @Test
    void testFindByResponseGroupAndKeyedFields() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        when(responseGroup.getResponseGroupSeq()).thenReturn(1L);
        doReturn(Optional.of(responseBody)).when(responseBodyServiceSpy).determineResponseBody(any(), any());
        when(this.responseBodyRepository.findByResponseGroupSeq(any())).thenReturn(List.of(responseBody));
        assertThat(responseBodyServiceSpy.findByResponseGroupAndKeyedFields(responseGroup, keyedFields), is(equalTo(Optional.of(responseBody))));
        verify(responseBodyServiceSpy, times(1)).determineResponseBody(List.of(responseBody), keyedFields);
        verify(this.responseBodyRepository, times(1)).findByResponseGroupSeq(1L);
    }

    @Test
    void testCreateResponseBody() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final Profile profile = new Profile();
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        final Map<String, Object> body = Map.of("bruh", "test", "bruh1", "test1");
        final Map<String, Object> headers = Map.of("test", "bruh");
        profile.setProfileSeq(1L);
        profile.setKeyedFields(Set.of("bruh"));
        when(this.responseBodyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doReturn(headers).when(responseBodyServiceSpy).filterHeaders(any());
        doReturn(Map.of("bruh1", "test1")).when(responseBodyServiceSpy).removeKeyedFields(any(), any());
        final ResponseBody actual = responseBodyServiceSpy.createResponseBody(profile, responseGroup, keyedFields, body, headers, 200);
        verify(this.responseBodyRepository, times(1)).save(any());
        verify(responseBodyServiceSpy, times(1)).removeKeyedFields(body, Set.of("bruh"));
        verify(responseBodyServiceSpy, times(1)).filterHeaders(headers);
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo("{\"bruh1\":\"test1\"}"))),
                () -> assertThat(actual.getHeaders(), is(equalTo(headers))),
                () -> assertThat(actual.getResponseCode(), is(equalTo(200)))
        );
    }

    @Test
    void testUpdateResponseBody() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final Profile profile = mock(Profile.class);
        final ResponseBody responseBody = new ResponseBody();
        final Map<String, Object> headers = Map.of("bruh", "test");
        final Map<String, Object> body = Map.of("bruh", "test");

        doReturn("{\"bruh\":\"test\"}").when(responseBodyServiceSpy).getCleanBody(any(), any());
        doReturn(headers).when(responseBodyServiceSpy).filterHeaders(any());

        responseBodyServiceSpy.updateResponseBody(profile, responseBody, 200, headers, body);

        verify(this.responseBodyRepository, times(1)).save(responseBody);
        verify(responseBodyServiceSpy, times(1)).getCleanBody(any(), any());
        verify(responseBodyServiceSpy, times(1)).filterHeaders(any());

        assertEquals("{\"bruh\":\"test\"}", responseBody.getBody());
        assertEquals(200, responseBody.getResponseCode());
        assertEquals(headers, responseBody.getHeaders());
    }

    @NullSource
    @ParameterizedTest
    void testUpdateResponseBodyInvalidArgs(final Map<String, Object> nullValue) {
        final Profile profile = mock(Profile.class);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final Map<String, Object> headers = Collections.emptyMap();
        assertThrows(IllegalArgumentException.class, () -> this.responseBodyService.updateResponseBody(profile, responseBody, 200, headers, nullValue));
    }

    public static Stream<Arguments> getTestGetCleanBodyInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Collections.emptyMap(), null),
                Arguments.of(null, Collections.emptySet())
        );
    }

    @ParameterizedTest
    @MethodSource("getTestGetCleanBodyInvalidArgsParams")
    void testGetCleanBodyInvalidArgs(final Map<String, Object> body, final Set<String> keyedFields) {
        assertThrows(IllegalArgumentException.class, () -> this.responseBodyService.getCleanBody(body, keyedFields));
    }

    @ParameterizedTest
    @MethodSource("getTestRemoveKeyedFieldsParams")
    void testRemoveKeyedFields(final Map<String, Object> body, final Set<String> keyedFields, final Map<String, Object> expected) {
        assertThat(this.responseBodyService.removeKeyedFields(body, keyedFields), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestRemoveKeyedFieldsParams() {
        return Stream.of(
                Arguments.of(Map.of("bruh", "test"), Set.of("bruh"), Map.of()),
                Arguments.of(Map.of("bruh", "test", "bruh1", "test1"), Set.of("bruh"), Map.of("bruh1", "test1")),
                Arguments.of(Map.of("bruh", "test", "bruh1", "test1"), Set.of("bruh", "bruh1"), Map.of())
        );
    }

    public static Stream<Arguments> getTestRemoveKeyedFieldsInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Collections.emptyMap(), null),
                Arguments.of(null, Collections.emptySet())
        );
    }

    @ParameterizedTest
    @MethodSource("getTestRemoveKeyedFieldsInvalidArgsParams")
    void testRemoveKeyedFieldsInvalidArgs(final Map<String, Object> body, final Set<String> keyedFields) {
        assertThrows(IllegalArgumentException.class, () -> this.responseBodyService.removeKeyedFields(body, keyedFields));
    }

    @ParameterizedTest
    @MethodSource("getTestFilterHeadersParams")
    void testFilterHeaders(final Map<String, Object> headers, final Map<String, Object> expected) {
        assertThat(this.responseBodyService.filterHeaders(headers), is(equalTo(expected)));
    }

    public static Stream<Arguments> getTestFilterHeadersParams() {
        return Stream.of(
                Arguments.of(Map.of("test", "bruh"), Map.of("test", "bruh")),
                Arguments.of(Map.of("user-agent", "bruh", "test1", "bruh1"), Map.of("test1", "bruh1")),
                Arguments.of(Map.of("accept-encoding", "bruh", "host", "bruh1", "user-agent", "bruh2"), Map.of())
        );
    }

    @Test
    void testDetermineResponseBody() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        when(responseBody.getKeyedFieldValues()).thenReturn(keyedFields);
        doReturn(true).when(responseBodyServiceSpy).keyedFieldsMatch(any(), any());
        final Optional<ResponseBody> actual = responseBodyServiceSpy.determineResponseBody(List.of(responseBody), keyedFields);
        verify(responseBodyServiceSpy, times(1)).keyedFieldsMatch(keyedFields, keyedFields);
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(equalTo(responseBody)));
    }

    @Test
    void testDetermineResponseBodyNoMatch() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        when(responseBody.getKeyedFieldValues()).thenReturn(keyedFields);
        doReturn(false).when(responseBodyServiceSpy).keyedFieldsMatch(any(), any());
        final Optional<ResponseBody> actual = responseBodyServiceSpy.determineResponseBody(List.of(responseBody), keyedFields);
        verify(responseBodyServiceSpy, times(1)).keyedFieldsMatch(keyedFields, keyedFields);
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    void testDetermineResponseBodyNoResponseBodies() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        final Optional<ResponseBody> actual = responseBodyServiceSpy.determineResponseBody(List.of(), keyedFields);
        verify(responseBodyServiceSpy, times(0)).keyedFieldsMatch(any(), any());
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    void testDetermineResponseBodyNoKeyedFields() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final Optional<ResponseBody> actual = responseBodyServiceSpy.determineResponseBody(Collections.emptyList(), Map.of());
        verify(responseBodyServiceSpy, times(0)).keyedFieldsMatch(any(), any());
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    void testKeyedFieldsMatch() {
        final Map<String, Object> control = Map.of("bruh", "test");
        final Map<String, Object> correct = Map.of("bruh", "test");
        final Map<String, Object> wrong = Map.of("bruh", "test1");
        assertThat(this.responseBodyService.keyedFieldsMatch(control, correct), is(true));
        assertThat(this.responseBodyService.keyedFieldsMatch(control, wrong), is(false));
    }
}
