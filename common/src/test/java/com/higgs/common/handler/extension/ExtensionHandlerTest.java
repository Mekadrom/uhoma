package com.higgs.common.handler.extension;

import com.higgs.common.handler.Handler;
import com.higgs.common.handler.HandlerDefinition;
import com.higgs.common.handler.HandlerHandler;
import com.higgs.common.handler.HandlerRequest;
import com.higgs.common.handler.HandlerResponse;
import com.hubspot.jinjava.Jinjava;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExtensionHandlerTest {
    @Test
    @SuppressWarnings("unchecked")
    void testHandle() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = new HashMap<>();
        final ExtensionHandlerRequest extensionHandlerRequest = mock(ExtensionHandlerRequest.class);
        final HandlerHandler handlerHandler = mock(HandlerHandler.class);

        final Handler<HandlerRequest, HandlerResponse> handler = mock(Handler.class);
        final HandlerResponse handlerResponse = mock(HandlerResponse.class);

        when(handlerHandler.findHandlerByName(any())).thenReturn(Optional.of(handler));
        doReturn(List.of(handlerResponse)).when(extensionHandlerSpy).handleExtensionCall(any(), any(), any(), any(), any());

        assertThat(extensionHandlerSpy.handle(handlerDefinition, headers, extensionHandlerRequest, handlerHandler), is(List.of(handlerResponse)));

        verify(extensionHandlerSpy, times(1)).handleExtensionCall(handler, handlerDefinition, headers, extensionHandlerRequest, handlerHandler);
    }

    @Test
    void testHandleSuperNotFound() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = new HashMap<>();
        final ExtensionHandlerRequest extensionHandlerRequest = mock(ExtensionHandlerRequest.class);
        final HandlerHandler handlerHandler = mock(HandlerHandler.class);

        final HandlerResponse handlerResponse = mock(HandlerResponse.class);

        when(handlerHandler.findHandlerByName(any())).thenReturn(Optional.empty());
        doReturn(List.of(handlerResponse)).when(extensionHandlerSpy).handleExtensionCall(any(), any(), any(), any(), any());

        extensionHandlerSpy.handle(handlerDefinition, headers, extensionHandlerRequest, handlerHandler);

        verify(extensionHandlerSpy, times(0)).handleExtensionCall(any(), any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("getTestHandleNullArgsParams")
    void testHandleNullArgs(final HandlerDefinition handlerDefinition, final Map<String, Object> headers, final ExtensionHandlerRequest extensionHandlerRequest, final HandlerHandler handlerHandler) {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.handle(handlerDefinition, headers, extensionHandlerRequest, handlerHandler));
    }

    public static Stream<Arguments> getTestHandleNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null, null, null),
                Arguments.of(mock(HandlerDefinition.class), null, null, null),
                Arguments.of(mock(HandlerDefinition.class), new HashMap<>(), null, null),
                Arguments.of(mock(HandlerDefinition.class), new HashMap<>(), mock(ExtensionHandlerRequest.class), null)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHandleExtensionCall() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final Handler<HandlerRequest, HandlerResponse> handler = mock(Handler.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);
        final Map<String, Object> headers = new HashMap<>();
        final ExtensionHandlerRequest extensionHandlerRequest = mock(ExtensionHandlerRequest.class);
        final HandlerHandler handlerHandler = mock(HandlerHandler.class);

        final HandlerResponse handlerResponse = mock(HandlerResponse.class);

        final HandlerDefinition prototype = mock(HandlerDefinition.class);
        when(prototype.getDef()).thenReturn(Map.of());

        doReturn(prototype).when(handler).getPrototypeHandlerDef();
        doReturn(Map.of()).when(extensionHandlerSpy).copyFieldTemplateValues(any(HandlerDefinition.class), any(HandlerDefinition.class));
        doReturn(Map.of()).when(extensionHandlerSpy).interpolateFieldValues(any(), any());
        doReturn(List.of(handlerResponse)).when(handlerHandler).process(any(), any(), any());

        assertThat(extensionHandlerSpy.handleExtensionCall(handler, handlerDefinition, headers, extensionHandlerRequest, handlerHandler), is(List.of(handlerResponse)));

        verify(handler, times(1)).getPrototypeHandlerDef();
        verify(extensionHandlerSpy, times(1)).copyFieldTemplateValues(prototype, handlerDefinition);
        verify(extensionHandlerSpy, times(1)).interpolateFieldValues(any(), eq(extensionHandlerRequest));
        verify(handlerHandler, times(1)).process(eq(prototype), eq(headers), any());
    }

    @ParameterizedTest
    @MethodSource("getTestHandleExtensionCallNullArgsParams")
    void testHandleExtensionCallNullArgs(final Handler<HandlerRequest, HandlerResponse> handler, final HandlerDefinition extensionHandlerDef, final Map<String, Object> headers, final ExtensionHandlerRequest request, final HandlerHandler handlerHandler) {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.handleExtensionCall(handler, extensionHandlerDef, headers, request, handlerHandler));
    }

    public static Stream<Arguments> getTestHandleExtensionCallNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null, null, null, null),
                Arguments.of(mock(Handler.class), null, null, null, null),
                Arguments.of(mock(Handler.class), mock(HandlerDefinition.class), null, null, null),
                Arguments.of(mock(Handler.class), mock(HandlerDefinition.class), new HashMap<>(), null, null),
                Arguments.of(mock(Handler.class), mock(HandlerDefinition.class), new HashMap<>(), mock(ExtensionHandlerRequest.class), null)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCopyFieldTemplateValues() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final HandlerDefinition prototype = mock(HandlerDefinition.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);

        doReturn(Map.of()).when(extensionHandlerSpy).copyFieldTemplateValues(any(Map.class), any(Map.class));

        assertThat(extensionHandlerSpy.copyFieldTemplateValues(prototype, handlerDefinition), is(Map.of()));

        verify(extensionHandlerSpy, times(1)).copyFieldTemplateValues(Map.of(), Map.of());
    }

    @ParameterizedTest
    @MethodSource("getTestCopyFieldTemplateValuesNullArgsParams")
    void testCopyFieldTemplateValuesNullArgs(final HandlerDefinition prototype, final HandlerDefinition extension) {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.copyFieldTemplateValues(prototype, extension));
    }

    public static Stream<Arguments> getTestCopyFieldTemplateValuesNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(mock(HandlerDefinition.class), null),
                Arguments.of(null, mock(HandlerDefinition.class))
        );
    }

    @ParameterizedTest
    @MethodSource("getTestCopyFieldTemplateValuesMapsParams")
    void testCopyFieldTemplateValuesMaps(final Map<String, Object> prototypeDef, final Map<String, Object> handlerDef, final Map<String, Object> expectedToInterpolate) {
        assertThat(spy(ExtensionHandler.class).copyFieldTemplateValues(prototypeDef, handlerDef), is(expectedToInterpolate));
    }

    public static Stream<Arguments> getTestCopyFieldTemplateValuesMapsParams() {
        return Stream.of(
                Arguments.of(Map.of("key", "value"), Map.of("key", "value"), Map.of("key", "value")),
                Arguments.of(Map.of("key", "value"), Map.of("key", "value2"), Map.of("key", "value2")),
                Arguments.of(Map.of("key", "value"), Map.of("key2", "value"), Map.of()),
                Arguments.of(Map.of("key", "value"), Map.of("key2", "value2"), Map.of()),
                Arguments.of(Map.of("key", "value"), Map.of("key", "value2", "key2", "value2"), Map.of("key", "value2")),
                Arguments.of(Map.of(), Map.of("key", "value2"), Map.of())
        );
    }

    @ParameterizedTest
    @MethodSource("getTestCopyFieldTemplateValuesMapsNullArgsParams")
    void testCopyFieldTemplateValuesMapsNullArgs(final Map<String, Object> prototypeDef, final Map<String, Object> handlerDef) {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.copyFieldTemplateValues(prototypeDef, handlerDef));
    }

    public static Stream<Arguments> getTestCopyFieldTemplateValuesMapsNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Map.of(), null),
                Arguments.of(null, Map.of())
        );
    }

    @Test
    void testInterpolateFieldValues() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final Jinjava jinjava = mock(Jinjava.class);
        final Map<String, Object> toInterpolate = new HashMap<>();
        toInterpolate.put("key", "value");
        toInterpolate.put("key2", "value2");
        final ExtensionHandlerRequest request = new ExtensionHandlerRequest(new HashMap<>());

        request.put("key", "value");
        request.put("key2", "value2");

        doReturn(jinjava).when(extensionHandlerSpy).getJinjaEngine();
        when(jinjava.render(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(extensionHandlerSpy.interpolateFieldValues(toInterpolate, request), is(toInterpolate));

        verify(jinjava, times(1)).render("value", request);
        verify(jinjava, times(1)).render("value2", request);
    }

    @ParameterizedTest
    @MethodSource("getTestInterpolateFieldValuesNullArgsParams")
    void testInterpolateFieldValuesNullArgs(final Map<String, Object> toInterpolate, final ExtensionHandlerRequest request) {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.interpolateFieldValues(toInterpolate, request));
    }

    public static Stream<Arguments> getTestInterpolateFieldValuesNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(new HashMap<>(), null),
                Arguments.of(null, new ExtensionHandlerRequest(new HashMap<>()))
        );
    }

    @Test
    void testInterpolateFieldValuesNonString() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final Jinjava jinjava = mock(Jinjava.class);
        final Map<String, Object> toInterpolate = new HashMap<>();
        toInterpolate.put("key", new Object());
        toInterpolate.put("key2", "value2");
        final ExtensionHandlerRequest request = new ExtensionHandlerRequest(new HashMap<>());

        request.put("key", "value");
        request.put("key2", "value2");

        doReturn(jinjava).when(extensionHandlerSpy).getJinjaEngine();
        when(jinjava.render(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(extensionHandlerSpy.interpolateFieldValues(toInterpolate, request), is(Map.of("key2", "value2")));

        verify(jinjava, times(1)).render(any(), eq(request));
    }

    @Test
    void testGetJinjaEngine() {
        assertNotNull(spy(ExtensionHandler.class).getJinjaEngine());
    }

    @Test
    void testRequestBodyToRequestObj() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("key", "value");

        final ExtensionHandlerRequest request = extensionHandlerSpy.requestBodyToRequestObj(requestBody);

        assertThat(request.get("key"), is("value"));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testRequestBodyToRequestObjNullArgs() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.requestBodyToRequestObj(null));
    }

    @Test
    void testQualifies() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);

        doReturn(true, false).when(extensionHandlerSpy).isExtension(any(HandlerDefinition.class));

        assertTrue(extensionHandlerSpy.qualifies(mock(HandlerDefinition.class)));
        assertFalse(extensionHandlerSpy.qualifies(mock(HandlerDefinition.class)));

        verify(extensionHandlerSpy, times(2)).isExtension(any(HandlerDefinition.class));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testQualifiesNullArgs() {
        final ExtensionHandler extensionHandlerSpy = spy(ExtensionHandler.class);
        assertThrows(IllegalArgumentException.class, () -> extensionHandlerSpy.qualifies(null));
    }

    @Test
    void testGetName() {
        assertThat(spy(ExtensionHandler.class).getName(), is(ExtensionHandler.NAME));
    }

    @Test
    void testGetProtoTypeHandlerDef() {
        assertThat(spy(ExtensionHandler.class).getPrototypeHandlerDef(), is(ExtensionHandler.PROTOTYPE_HANDLER_DEF));
    }
}
