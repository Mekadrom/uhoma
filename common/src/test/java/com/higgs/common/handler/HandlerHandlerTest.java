package com.higgs.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.higgs.common.util.CommonUtils;
import lombok.SneakyThrows;
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
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerHandlerTest {
    @Mock
    private CommonUtils commonUtils;

    @Mock
    private ProxyHandlerGenerator proxyHandlerGenerator;

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    void testProcessDelegates() {
        final HandlerResponse handlerResponse = mock(HandlerResponse.class);
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        final HandlerHandler handlerHandlerSpy = spy(handlerHandler);
        final Map<String, Object> headers = Map.of("1", "1");
        final String body = "{\"body\":\"body\"}";
        doReturn(List.of(handlerResponse)).when(handlerHandlerSpy).process(eq(headers), any(Map.class));
        when(this.commonUtils.parseMap(body)).thenReturn(Map.of("body", "body"));
        final List<HandlerResponse> actual = handlerHandlerSpy.process(headers, body);
        verify(this.commonUtils, times(1)).parseMap(body);
        verify(handlerHandlerSpy, times(1)).process(headers, Map.of("body", "body"));
        assertThat(actual, is(List.of(handlerResponse)));
    }

    @ParameterizedTest
    @MethodSource("getTestProcessDelegatesInvalidArgsParams")
    void testProcessDelegatesInvalidArgs(final Map<String, Object> headers, final String body) {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        assertThrows(IllegalArgumentException.class, () -> handlerHandler.process(headers, body));
    }

    public static Stream<Arguments> getTestProcessDelegatesInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, "{}"),
                Arguments.of(Map.of(), null),
                Arguments.of(null, null)
        );
    }

    @Test
    @SneakyThrows
    void testProcessDelegatesAgain() {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        final HandlerHandler handlerHandlerSpy = spy(handlerHandler);
        final HandlerResponse handlerResponse = mock(HandlerResponse.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = Map.of("1", "1");
        final Map<String, Object> body = Map.of("body", "body");
        doReturn(handlerDefinition).when(handlerHandlerSpy).parseHandlerDef(any());
        doReturn(List.of(handlerResponse)).when(handlerHandlerSpy).process(handlerDefinition, headers, body);
        final List<HandlerResponse> actual = handlerHandlerSpy.process(headers, body);
        verify(handlerHandlerSpy, times(1)).parseHandlerDef(any());
        verify(handlerHandlerSpy, times(1)).process(handlerDefinition, headers, body);
        assertThat(actual, is(List.of(handlerResponse)));
    }

    @ParameterizedTest
    @MethodSource("getTestProcessDelegateInvalidArgsParams")
    void testProcessDelegateInvalidArgs(final Map<String, Object> headers, final Map<String, Object> body) {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        assertThrows(IllegalArgumentException.class, () -> handlerHandler.process(headers, body));
    }

    public static Stream<Arguments> getTestProcessDelegateInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, Map.of()),
                Arguments.of(Map.of(), null),
                Arguments.of(null, null)
        );
    }

    @Test
    @SneakyThrows
    void testParseHandlerDef() {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(this.commonUtils.getDefaultMapper()).thenReturn(objectMapper);
        when(objectMapper.readValue(any(String.class), eq(HandlerDefinition.class))).thenReturn(handlerDefinition);
        final HandlerDefinition actual = handlerHandler.parseHandlerDef("{\"handler\":\"handler\"}");
        verify(this.commonUtils, times(1)).getDefaultMapper();
        verify(objectMapper, times(1)).readValue("{\"handler\":\"handler\"}", HandlerDefinition.class);
        assertThat(actual, is(handlerDefinition));
    }

    @Test
    void testProcess() {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        final HandlerHandler handlerHandlerSpy = spy(handlerHandler);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final HandlerResponse handlerResponse1 = mock(HandlerResponse.class);
        final HandlerResponse handlerResponse2 = mock(HandlerResponse.class);
        final Map<String, Object> headers = Map.of("1", List.of("1"));
        final Map<String, Object> body = Map.of("body", "body");
        doReturn(List.of(handlerResponse1)).when(handlerHandlerSpy).processDefaultHandlers(handlerDefinition, headers, body);
        doReturn(List.of(handlerResponse2)).when(handlerHandlerSpy).processWithProxyHandlers(handlerDefinition, headers, body);
        final List<HandlerResponse> actual = handlerHandlerSpy.process(handlerDefinition, headers, body);
        verify(handlerHandlerSpy, times(1)).processDefaultHandlers(handlerDefinition, headers, body);
        verify(handlerHandlerSpy, times(1)).processWithProxyHandlers(handlerDefinition, headers, body);
        assertThat(actual, is(List.of(handlerResponse1, handlerResponse2)));
    }

    @ParameterizedTest
    @MethodSource("getTestProcessInvalidArgsParams")
    void testProcessInvalidArgs(final HandlerDefinition handlerDefinition, final Map<String, Object> headers, final Map<String, Object> body) {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        assertThrows(IllegalArgumentException.class, () -> handlerHandler.process(handlerDefinition, headers, body));
    }

    public static Stream<Arguments> getTestProcessInvalidArgsParams() {
        return Stream.of(
                Arguments.of(mock(HandlerDefinition.class), null, Map.of()),
                Arguments.of(mock(HandlerDefinition.class), Map.of(), null),
                Arguments.of(null, null, null)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcessDefaultHandlers() {
        final Handler<HandlerRequest, HandlerResponse> handler1 = mock(Handler.class);
        final Handler<HandlerRequest, HandlerResponse> handler2 = mock(Handler.class);
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, List.of(handler1, handler2), this.proxyHandlerGenerator);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final HandlerResponse handlerResponse = mock(HandlerResponse.class);
        final Map<String, Object> headers = Map.of("1", List.of("1"));
        final Map<String, Object> body = Map.of("body", "body");
        when(handler1.qualifies(any())).thenReturn(true);
        when(handler2.qualifies(any())).thenReturn(false);
        when(handler1.handle(any(), any(), any(Map.class), any())).thenReturn(List.of(handlerResponse));
        final List<HandlerResponse> actual = handlerHandler.processDefaultHandlers(handlerDefinition, headers, body);
        verify(handler1, times(1)).qualifies(handlerDefinition);
        verify(handler1, times(1)).handle(handlerDefinition, headers, body, handlerHandler);
        verify(handler2, times(1)).qualifies(handlerDefinition);
        verify(handler2, times(0)).handle(any(), any(), any(Map.class), any());
        assertThat(actual, is(List.of(handlerResponse)));
    }

    @ParameterizedTest
    @MethodSource("getTestProcessDefaultHandlersInvalidArgsParams")
    void testProcessDefaultHandlersInvalidArgs(final HandlerDefinition handlerDefinition, Map<String, Object> headers, final Map<String, Object> body) {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        assertThrows(IllegalArgumentException.class, () -> handlerHandler.processDefaultHandlers(handlerDefinition, headers, body));
    }

    public static Stream<Arguments> getTestProcessDefaultHandlersInvalidArgsParams() {
        return Stream.of(
                Arguments.of(mock(HandlerDefinition.class), null, Map.of()),
                Arguments.of(mock(HandlerDefinition.class), Map.of(), null),
                Arguments.of(null, null, null)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcessWithProxyHandlers() {
        final Handler<HandlerRequest, HandlerResponse> handler = mock(Handler.class);
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, List.of(handler), this.proxyHandlerGenerator);
        final HandlerResponse handlerResponse = mock(HandlerResponse.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = Map.of("1", List.of("1"));
        final Map<String, Object> body = Map.of("body", "body");
        when(this.proxyHandlerGenerator.buildProxyHandlers(any())).thenReturn(List.of(handler));
        when(handler.handle(any(), any(), any(Map.class), any())).thenReturn(List.of(handlerResponse));
        final List<HandlerResponse> actual = handlerHandler.processWithProxyHandlers(handlerDefinition, headers, body);
        verify(this.proxyHandlerGenerator, times(1)).buildProxyHandlers(handlerDefinition);
        verify(handler, times(1)).handle(handlerDefinition, headers, body, handlerHandler);
        assertThat(actual, is(List.of(handlerResponse)));
    }

    @ParameterizedTest
    @MethodSource("getTestProcessWithProxyHandlersInvalidArgsParams")
    void testProcessWithProxyHandlersInvalidArgs(final HandlerDefinition handlerDefinition, final Map<String, Object> headers, final Map<String, Object> body) {
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, Collections.emptyList(), this.proxyHandlerGenerator);
        assertThrows(IllegalArgumentException.class, () -> handlerHandler.processWithProxyHandlers(handlerDefinition, headers, body));
    }

    public static Stream<Arguments> getTestProcessWithProxyHandlersInvalidArgsParams() {
        return Stream.of(
                Arguments.of(mock(HandlerDefinition.class), null, Map.of()),
                Arguments.of(mock(HandlerDefinition.class), Map.of(), null),
                Arguments.of(null, null, null)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindHandlerByName() {
        final Handler<HandlerRequest, HandlerResponse> handler1 = mock(Handler.class);
        final Handler<HandlerRequest, HandlerResponse> handler2 = mock(Handler.class);
        final HandlerHandler handlerHandler = new HandlerHandler(this.commonUtils, List.of(handler1, handler2), this.proxyHandlerGenerator);
        when(handler1.getName()).thenReturn("not_name");
        when(handler2.getName()).thenReturn("name");
        final Optional<Handler<HandlerRequest, HandlerResponse>> actual = handlerHandler.findHandlerByName("name");
        verify(handler1, times(1)).getName();
        verify(handler2, times(1)).getName();
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(handler2));
    }
}
