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
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        final Map<String, Object> headers = Map.of("test", "bruh");
        doReturn(Optional.of(responseBody)).when(responseBodyServiceSpy).findByResponseGroupAndKeyedFields(any(), any());
        assertThat(responseBodyServiceSpy.findByResponseGroupAndKeyedFieldsOrDefault(responseGroup, keyedFields, headers, 200), is(equalTo(responseBody)));
        verify(responseBodyServiceSpy, times(1)).findByResponseGroupAndKeyedFields(responseGroup, keyedFields);
        verify(responseBodyServiceSpy, times(0)).createResponseBody(responseGroup, keyedFields, headers, 200);
    }

    @Test
    void testFindByResponseGroupAndKeyedFieldsOrDefaultDefaults() {
        final ResponseBodyService responseBodyServiceSpy = spy(this.responseBodyService);
        final ResponseBody responseBody = mock(ResponseBody.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> keyedFields = Map.of("bruh", "test");
        final Map<String, Object> headers = Map.of("test", "bruh");
        when(responseGroup.getResponseGroupSeq()).thenReturn(1L);
        doReturn(responseBody).when(responseBodyServiceSpy).createResponseBody(any(), any(), any(), any());
        assertThat(responseBodyServiceSpy.findByResponseGroupAndKeyedFieldsOrDefault(responseGroup, keyedFields, headers, 200), is(equalTo(responseBody)));
        verify(responseBodyServiceSpy, times(1)).findByResponseGroupAndKeyedFields(responseGroup, keyedFields);
        verify(responseBodyServiceSpy, times(1)).createResponseBody(responseGroup, keyedFields, headers, 200);
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
        final Profile profile = mock(Profile.class);
        final ResponseGroup responseGroup = mock(ResponseGroup.class);
        final Map<String, Object> body = Map.of("bruh", "test", "bruh1", "test1");
        final Map<String, Object> headers = Map.of("test", "bruh");
        when(responseGroup.getProfileSeq()).thenReturn(profile);
        when(profile.getKeyedFields()).thenReturn(Set.of("bruh"));
        when(this.responseBodyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doReturn(headers).when(responseBodyServiceSpy).filterHeaders(any());
        doReturn(Map.of("bruh1", "test1")).when(responseBodyServiceSpy).removeKeyedFields(any(), any());
        final ResponseBody actual = responseBodyServiceSpy.createResponseBody(responseGroup, body, headers, 200);
        verify(this.responseBodyRepository, times(1)).save(any());
        verify(responseBodyServiceSpy, times(1)).removeKeyedFields(body, Set.of("bruh"));
        verify(responseBodyServiceSpy, times(1)).filterHeaders(headers);
        assertAll(
                () -> assertThat(actual.getResponseGroupSeq(), is(equalTo(responseGroup))),
                () -> assertThat(actual.getBody(), is(equalTo("{\"bruh1\":\"test1\"}"))),
                () -> assertThat(actual.getHeaders(), is(equalTo(headers))),
                () -> assertThat(actual.getResponseCode(), is(equalTo(200)))
        );
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
