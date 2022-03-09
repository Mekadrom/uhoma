package com.higgs.server.web.socket;

import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.kafka.KafkaTopicEnum;
import com.higgs.common.kafka.ServerProducer;
import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.ActionHandler;
import com.higgs.server.web.dto.ActionRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NodeSocket}.
 */
@ExtendWith(MockitoExtension.class)
class NodeSocketTest {
    @Mock
    private ServerProducer producer;

    private NodeSocket nodeSocket;

    @BeforeEach
    void setUp() {
        this.nodeSocket = new NodeSocket(this.producer);
    }

    /**
     * Test the method {@link NodeSocket#receiveMessage(ActionRequest, Principal)} with valid input.
     */
    @Test
    void testReceiveMessage() {
        final ActionRequest actionRequest = mock(ActionRequest.class);
        final Principal principal = mock(Principal.class);
        when(actionRequest.getActionWithParams()).thenReturn(mock(Action.class));
        this.nodeSocket.receiveMessage(actionRequest, principal);
        assertDoesNotThrow(() -> verify(this.producer, times(1)).send(eq(KafkaTopicEnum.NODE_ACTION), eq(actionRequest), any()));
    }

    /**
     * Test the method {@link NodeSocket#receiveMessage(ActionRequest, Principal)} with invalid (null) inputs.
     *
     * @param actionRequest a mocked {@link ActionRequest}
     * @param principal     a mocked {@link Principal}
     */
    @ParameterizedTest
    @MethodSource("getTestReceiveMessageNullParams")
    void testReceiveMessageNullArgs(final ActionRequest actionRequest, final Principal principal) {
        assertThrows(IllegalArgumentException.class, () -> this.nodeSocket.receiveMessage(actionRequest, principal));
    }

    public static Stream<Arguments> getTestReceiveMessageNullParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, mock(Principal.class)),
                Arguments.of(mock(ActionRequest.class), null)
        );
    }

    /**
     * Test the method {@link NodeSocket#buildHeaderMap(ActionRequest)} with invalid input, verifies that an
     * {@link IllegalArgumentException} is thrown.
     */
    @Test
    void testReceiveMessageNullActionWithParams() {
        final ActionRequest actionRequest = mock(ActionRequest.class);
        final Principal principal = mock(Principal.class);
        when(actionRequest.getActionWithParams()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> this.nodeSocket.receiveMessage(actionRequest, principal));
    }

    /**
     * Test the method {@link NodeSocket#buildHeaderMap(ActionRequest)} with valid input.
     */
    @Test
    void testBuildHeaderMap() {
        final ActionRequest actionRequest = mock(ActionRequest.class);
        final Action action = mock(Action.class);
        final ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionRequest.getActionWithParams()).thenReturn(action);
        when(action.getActionHandler()).thenReturn(actionHandler);
        when(actionHandler.getDefinition()).thenReturn("test");
        when(actionRequest.getToNodeSeq()).thenReturn(1L);
        when(actionRequest.getFromNodeSeq()).thenReturn(2L);
        when(actionRequest.getToUsername()).thenReturn("user1");
        when(actionRequest.getFromUsername()).thenReturn("user2");

        final Map<String, Object> actual = this.nodeSocket.buildHeaderMap(actionRequest);
        verify(actionRequest, times(1)).getActionWithParams();
        verify(action, times(1)).getActionHandler();
        verify(actionHandler, times(1)).getDefinition();
        assertThat(actual.get(HAKafkaConstants.HEADER_ACTION_HANDLER_DEF), is(equalTo("test")));
        assertThat(actual.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ), is(equalTo(1L)));
        assertThat(actual.get(HAKafkaConstants.HEADER_SENDING_NODE_SEQ), is(equalTo(2L)));
        assertThat(actual.get(HAKafkaConstants.HEADER_RECEIVING_USERNAME), is(equalTo("user1")));
        assertThat(actual.get(HAKafkaConstants.HEADER_SENDING_USERNAME), is(equalTo("user2")));
    }

    /**
     * Test the method {@link NodeSocket#buildHeaderMap(ActionRequest)} with valid but empty input.
     */
    @Test
    void testBuildHeaderMapNullActionDef() {
        final ActionRequest actionRequest = mock(ActionRequest.class);
        final Action action = mock(Action.class);
        final ActionHandler actionHandler = mock(ActionHandler.class);
        when(actionRequest.getActionWithParams()).thenReturn(action);
        when(action.getActionHandler()).thenReturn(actionHandler);
        when(actionHandler.getDefinition()).thenReturn(null);

        final Map<String, Object> actual = this.nodeSocket.buildHeaderMap(actionRequest);
        verify(actionRequest, times(1)).getActionWithParams();
        verify(action, times(1)).getActionHandler();
        verify(actionHandler, times(1)).getDefinition();
        assertThat(actual.get(HAKafkaConstants.HEADER_ACTION_HANDLER_DEF), is(equalTo(StringUtils.EMPTY)));
    }

    /**
     * Test the method {@link NodeSocket#buildHeaderMap(ActionRequest)} with invalid (null) input. Expects an
     * {@link IllegalArgumentException} to be thrown.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testBuildHeaderMapNull() {
        assertThrows(IllegalArgumentException.class, () -> this.nodeSocket.buildHeaderMap(null));
    }
}
