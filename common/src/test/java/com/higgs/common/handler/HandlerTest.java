package com.higgs.common.handler;

import com.higgs.common.kafka.HAKafkaConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
class HandlerTest {
    @Test
    void testHandleDelegates() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        final HandlerRequest request = mock(HandlerRequest.class);
        final HandlerResponse response = mock(HandlerResponse.class);
        final Map<String, Object> headers = Map.of("1", "2");
        final Map<String, Object> requestBody = Map.of("body", "body");
        doReturn(request).when(handlerSpy).processRequest(headers, requestBody);
        doReturn(List.of(response)).when(handlerSpy).handle(any(), eq(headers), eq(request), any());
        doCallRealMethod().when(handlerSpy).handle(any(), eq(headers), eq(requestBody), any());
        final List<HandlerResponse> actual = handlerSpy.handle(mock(HandlerDefinition.class), headers, requestBody, mock(HandlerHandler.class));
        verify(handlerSpy).handle(any(), any(Map.class), eq(request), any());
        assertThat(actual, is(List.of(response)));
    }

    @ParameterizedTest
    @MethodSource("getTestHandleDelegatesInvalidArgsParams")
    void testHandleDelegatesInvalidArgs(final Map<String, Object> headers, final Map<String, Object> requestBody, final HandlerHandler handlerHandler) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        doCallRealMethod().when(handlerSpy).handle(any(), eq(headers), eq(requestBody), any());
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.handle(mock(HandlerDefinition.class), headers, requestBody, handlerHandler));
    }

    public static Stream<Arguments> getTestHandleDelegatesInvalidArgsParams() {
        return Stream.of(
                Arguments.of(null, Map.of("body", "body"), mock(HandlerHandler.class)),
                Arguments.of(Map.of("1", "2"), null, mock(HandlerHandler.class)),
                Arguments.of(Map.of("1", "2"), Map.of("body", "body"), null)
        );
    }

    @Test
    void testProcessRequest() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        final Map<String, Object> headers = Map.of(HAKafkaConstants.HEADER_RECEIVING_USERNAME, "recuser".getBytes(), HAKafkaConstants.HEADER_SENDING_USERNAME, "senduser".getBytes());
        final Map<String, Object> requestBody = Map.of("body", "body");
        final HandlerRequest request = new HandlerRequest() {};

        doReturn(request).when(handlerSpy).requestBodyToRequestObj(requestBody);
        doReturn(1L).when(handlerSpy).getLongHeader(any(), eq(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ));
        doReturn(2L).when(handlerSpy).getLongHeader(any(), eq(HAKafkaConstants.HEADER_SENDING_NODE_SEQ));

        final HandlerRequest actual = handlerSpy.processRequest(headers, requestBody);

        verify(handlerSpy, times(1)).requestBodyToRequestObj(requestBody);
        verify(handlerSpy, times(1)).getLongHeader(headers, HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ);
        verify(handlerSpy, times(1)).getLongHeader(headers, HAKafkaConstants.HEADER_SENDING_NODE_SEQ);

        assertNotNull(actual);
        assertThat(actual.getToNodeSeq(), is(1L));
        assertThat(actual.getFromNodeSeq(), is(2L));
        assertThat(actual.getToUsername(), is("recuser"));
        assertThat(actual.getFromUsername(), is("senduser"));
    }

    @ParameterizedTest
    @MethodSource("getTestProcessRequestNullArgsParams")
    void testProcessRequestNullArgs(final Map<String, Object> headers, final Map<String, Object> requestBody) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.processRequest(headers, requestBody));
    }

    public static Stream<Arguments> getTestProcessRequestNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Map.of("1", "2"), null),
                Arguments.of(null, Map.of("body", "body"))
        );
    }

    @Test
    void testGetLongHeader() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        final Map<String, Object> headers = Map.of(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ, "1", HAKafkaConstants.HEADER_SENDING_NODE_SEQ, "2", "nonnumeric", "three");

        assertThat(handlerSpy.getLongHeader(headers, HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ), is(1L));
        assertThat(handlerSpy.getLongHeader(headers, HAKafkaConstants.HEADER_SENDING_NODE_SEQ), is(2L));
        assertNull(handlerSpy.getLongHeader(headers, "invalid"));
        assertNull(handlerSpy.getLongHeader(headers, "nonnumeric"));
    }

    @ParameterizedTest
    @MethodSource("getTestGetLongHeaderNullArgsParams")
    void testGetLongHeaderNullArgs(final Map<String, Object> headers, final String headerName) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.getLongHeader(headers, headerName));
    }

    public static Stream<Arguments> getTestGetLongHeaderNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(Map.of("1", "2"), null),
                Arguments.of(null, "invalid")
        );
    }

    @ParameterizedTest
    @MethodSource("getTestDefaultQualifiesParams")
    void testDefaultQualifies(final boolean isBuiltIn, final boolean isBuiltinType, final boolean extendsFrom, final boolean expected) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);

        doReturn(isBuiltIn).when(handlerSpy).isBuiltin(any(HandlerDefinition.class));
        doReturn(isBuiltinType).when(handlerSpy).builtinTypeIs(any(HandlerDefinition.class), any());
        doReturn(extendsFrom).when(handlerSpy).extendsFrom(any(HandlerDefinition.class), any());

        assertThat(handlerSpy.qualifies(mock(HandlerDefinition.class)), is(expected));
    }

    public static Stream<Arguments> getTestDefaultQualifiesParams() {
        return Stream.of(
                Arguments.of(true, true, true, true),
                Arguments.of(true, true, false, true),
                Arguments.of(true, false, true, false),
                Arguments.of(true, false, false, false),
                Arguments.of(false, true, true, false),
                Arguments.of(false, true, false, false),
                Arguments.of(false, false, true, false),
                Arguments.of(false, false, false, false)
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testDefaultQualifiesNullArgs() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.qualifies(null));
    }

    @Test
    void testIsBuiltin() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);

        doReturn(true, false).when(handlerSpy).isBuiltin(any(Map.class));

        assertTrue(handlerSpy.isBuiltin(mock(HandlerDefinition.class)));
        assertFalse(handlerSpy.isBuiltin(mock(HandlerDefinition.class)));

        verify(handlerSpy, times(2)).isBuiltin(any(Map.class));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testIsBuiltinNullArgs() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.isBuiltin((HandlerDefinition) null));
    }

    @ParameterizedTest
    @MethodSource("getTestIsBuiltInMapParams")
    void testIsBuiltInMap(final Map<String, Object> metadataMap, final boolean expected) {
        assertThat(spy(Handler.class).isBuiltin(metadataMap), is(expected));
    }

    public static Stream<Arguments> getTestIsBuiltInMapParams() {
        final Map<String, Object> nullMap = new HashMap<>();
        nullMap.put(Handler.IS_BUILTIN, null);
        return Stream.of(
                Arguments.of(Map.of(Handler.IS_BUILTIN, "true"), true),
                Arguments.of(Map.of(Handler.IS_BUILTIN, "false"), false),
                Arguments.of(Map.of(), false),
                Arguments.of(Map.of(Handler.IS_BUILTIN, "FAlse"), false),
                Arguments.of(Map.of(Handler.IS_BUILTIN, ""), false),
                Arguments.of(nullMap, false),
                Arguments.of(Map.of("invalid", "true"), false),
                Arguments.of(Map.of("invalid", "false"), false),
                Arguments.of(Map.of(Handler.IS_BUILTIN, "TrUE"), true),
                Arguments.of(Map.of(Handler.IS_BUILTIN, true), true),
                Arguments.of(Map.of(Handler.IS_BUILTIN, false), false)
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testIsBuiltinMapNullArgs() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.isBuiltin((Map<String, Object>) null));
    }

    @Test
    void testBuiltInTypeIs() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);

        doReturn(true, false).when(handlerSpy).builtinTypeIs(any(Map.class), any());

        assertTrue(handlerSpy.builtinTypeIs(handlerDefinition, "test"));
        assertFalse(handlerSpy.builtinTypeIs(handlerDefinition, "test"));

        verify(handlerSpy, times(2)).builtinTypeIs(any(Map.class), any());
    }

    @ParameterizedTest
    @MethodSource("getTestBuiltInTypeIsNullArgsParams")
    void testBuiltInTypeIsNullArgs(final HandlerDefinition handlerDefinition, final String type) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.builtinTypeIs(handlerDefinition, type));
    }

    public static Stream<Arguments> getTestBuiltInTypeIsNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "test"),
                Arguments.of(mock(HandlerDefinition.class), null)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestBuiltInTypeIsMapParams")
    void testBuiltInTypeIsMap(final Map<String, Object> metadataMap, final String fieldValue, final boolean expected) {
        assertThat(spy(Handler.class).builtinTypeIs(metadataMap, fieldValue), is(expected));
    }

    public static Stream<Arguments> getTestBuiltInTypeIsMapParams() {
        final Map<String, Object> nullMap = new HashMap<>();
        nullMap.put(Handler.IS_BUILTIN, null);
        return Stream.of(
                Arguments.of(Map.of(Handler.BUILTIN_TYPE, "bruh"), "bruh", true),
                Arguments.of(Map.of(Handler.BUILTIN_TYPE, "hurb"), "bruh", false),
                Arguments.of(Map.of(), "bruh", false),
                Arguments.of(Map.of(Handler.BUILTIN_TYPE, "BRUH"), "bruh", false),
                Arguments.of(Map.of(Handler.BUILTIN_TYPE, ""), "bruh", false),
                Arguments.of(Map.of(Handler.BUILTIN_TYPE, ""), "", true),
                Arguments.of(nullMap, "bruh", false),
                Arguments.of(Map.of("invalid", "bruh"), "bruh", false),
                Arguments.of(Map.of("invalid", "false"), "bruh", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestBuiltInTypeIsMapNullArgsParams")
    void testBuiltInTypeIsMapNullArgs(final Map<String, Object> metadataMap, final String type) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.builtinTypeIs(metadataMap, type));
    }

    public static Stream<Arguments> getTestBuiltInTypeIsMapNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "test"),
                Arguments.of(Map.of(), null)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestExtendsFromParams")
    void testExtendsFrom(final boolean isExtension, final boolean extendsFrom, final boolean expected) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);

        doReturn(isExtension).when(handlerSpy).isExtension(any(HandlerDefinition.class));
        doReturn(extendsFrom).when(handlerSpy).extendsFrom(any(Map.class), any());

        assertThat(handlerSpy.extendsFrom(handlerDefinition, "test"), is(expected));
    }

    public static Stream<Arguments> getTestExtendsFromParams() {
        return Stream.of(
                Arguments.of(true, true, true),
                Arguments.of(true, false, false),
                Arguments.of(false, true, false),
                Arguments.of(false, false, false)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestExtendsFromNullArgsParams")
    void testExtendsFromNullArgs(final HandlerDefinition handlerDefinition, final String type) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.extendsFrom(handlerDefinition, type));
    }

    public static Stream<Arguments> getTestExtendsFromNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "test"),
                Arguments.of(mock(HandlerDefinition.class), null)
        );
    }

    @Test
    void testIsExtension() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        final HandlerDefinition handlerDefinition = mock(HandlerDefinition.class);

        doReturn(true, false).when(handlerSpy).isExtension(any(Map.class));

        assertTrue(handlerSpy.isExtension(handlerDefinition));
        assertFalse(handlerSpy.isExtension(handlerDefinition));

        verify(handlerSpy, times(2)).isExtension(any(Map.class));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testIsExtensionNullArgs() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.isExtension((HandlerDefinition) null));
    }

    @ParameterizedTest
    @MethodSource("getTestIsExtensionMapParams")
    void testIsExtensionMap(final Map<String, Object> metadataMap, final boolean expected) {
        assertThat(spy(Handler.class).isExtension(metadataMap), is(expected));
    }

    public static Stream<Arguments> getTestIsExtensionMapParams() {
        return Stream.of(
                Arguments.of(Map.of(Handler.IS_EXTENSION, true), true),
                Arguments.of(Map.of(Handler.IS_EXTENSION, false), false),
                Arguments.of(Map.of(Handler.IS_EXTENSION, "true"), true),
                Arguments.of(Map.of(Handler.IS_EXTENSION, "false"), false),
                Arguments.of(Map.of(Handler.IS_EXTENSION, ""), false),
                Arguments.of(Map.of(Handler.IS_EXTENSION, "bruh"), false),
                Arguments.of(Map.of("invalid", "true"), false),
                Arguments.of(Map.of("invalid", true), false),
                Arguments.of(Map.of(), false)
        );
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testIsExtensionMapNullArgs() {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.isExtension((Map<String, Object>) null));
    }

    @ParameterizedTest
    @MethodSource("getTestExtendsFromMapParams")
    void testExtendsFromMap(final Map<String, Object> handlerDef, final String fieldValue, final boolean expected) {
        assertThat(spy(Handler.class).extendsFrom(handlerDef, fieldValue), is(expected));
    }

    public static Stream<Arguments> getTestExtendsFromMapParams() {
        final Map<String, Object> nullMap = new HashMap<>();
        nullMap.put(Handler.EXTENDS_FROM, null);
        return Stream.of(
                Arguments.of(Map.of(Handler.EXTENDS_FROM, "bruh"), "bruh", true),
                Arguments.of(Map.of(Handler.EXTENDS_FROM, "hurb"), "bruh", false),
                Arguments.of(Map.of(), "bruh", false),
                Arguments.of(Map.of(Handler.EXTENDS_FROM, "BRUH"), "bruh", false),
                Arguments.of(Map.of(Handler.EXTENDS_FROM, ""), "bruh", false),
                Arguments.of(Map.of(Handler.EXTENDS_FROM, ""), "", true),
                Arguments.of(nullMap, "bruh", false),
                Arguments.of(Map.of("invalid", "bruh"), "bruh", false),
                Arguments.of(Map.of("invalid", "false"), "bruh", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestExtendsFromMapNullArgsParams")
    void testExtendsFromMapNullArgs(final Map<String, Object> handlerDef, final String fieldValue) {
        final Handler<HandlerRequest, HandlerResponse> handlerSpy = spy(Handler.class);
        assertThrows(IllegalArgumentException.class, () -> handlerSpy.extendsFrom(handlerDef, fieldValue));
    }

    public static Stream<Arguments> getTestExtendsFromMapNullArgsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, "test"),
                Arguments.of(mock(Map.class), null)
        );
    }
}
