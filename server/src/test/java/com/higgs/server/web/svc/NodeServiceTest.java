package com.higgs.server.web.svc;

import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.ActionParameter;
import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.Node;
import com.higgs.server.db.entity.Room;
import com.higgs.server.db.repo.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NodeService}.
 */
@ExtendWith(MockitoExtension.class)
class NodeServiceTest {
    @Mock
    private ActionParameterService actionParameterService;

    @Mock
    private ActionService actionService;

    @Mock
    private HomeService homeService;

    @Mock
    private NodeRepository nodeRepository;

    private NodeService nodeService;

    @BeforeEach
    void setUp() {
        this.nodeService = new NodeService(this.actionParameterService, this.actionService, this.homeService, this.nodeRepository);
    }

    /**
     * Tests the {@link NodeService#upsert(Node)} method when the node already exists.
     */
    @Test
    void testUpsertNodeExists() {
        final Node node = mock(Node.class);
        final Room room = mock(Room.class);
        when(node.getNodeSeq()).thenReturn(1L);
        when(node.getRoom()).thenReturn(room);
        when(room.getHomeSeq()).thenReturn(2L);
        when(this.nodeRepository.findById(any())).thenReturn(Optional.of(node));
        when(this.nodeRepository.saveAndFlush(any())).thenReturn(node);
        assertThat(this.nodeService.upsert(node), is(equalTo(node)));
        verify(node, times(1)).getRoom();
        verify(this.homeService, times(1)).getHome(eq(2L));
        verify(this.nodeRepository, times(1)).saveAndFlush(eq(node));
        this.verifyUpsertMethodsCalledInOrder(node);
    }

    /**
     * Tests the {@link NodeService#upsert(Node)} method when the node does not exist.
     */
    @Test
    void testUpsertNodeDoesntExist() {
        final Node node = mock(Node.class);
        final Room room = mock(Room.class);
        when(node.getNodeSeq()).thenReturn(1L);
        when(node.getRoom()).thenReturn(room);
        when(room.getHomeSeq()).thenReturn(2L);
        when(this.nodeRepository.saveAndFlush(any())).thenReturn(node);
        when(this.nodeRepository.findById(any())).thenReturn(Optional.empty());
        assertThat(this.nodeService.upsert(node), is(equalTo(node)));
        verify(node, times(1)).getRoom();
        verify(this.homeService, times(1)).getHome(eq(2L));
        this.verifyUpsertMethodsCalledInOrder(node);
    }

    /**
     * Common checks between {@link NodeServiceTest#testUpsertNodeDoesntExist())} and
     * {@link NodeServiceTest#testUpsertNodeExists()}.
     *
     * @param mockNode The node to verify calls with.
     */
    private void verifyUpsertMethodsCalledInOrder(final Node mockNode) {
        final InOrder inOrder = inOrder(this.actionService, this.actionParameterService, this.homeService, this.nodeRepository);
        inOrder.verify(this.homeService, times(1)).getHome(eq(2L));
        inOrder.verify(this.actionParameterService, times(1)).deleteAll(anyCollection(), anyCollection());
        inOrder.verify(this.actionService, times(1)).deleteAll(anyCollection(), anyCollection());
        inOrder.verify(this.actionParameterService, times(1)).saveAll(anyCollection());
        inOrder.verify(this.actionService, times(1)).saveAll(anyCollection());
        inOrder.verify(this.actionParameterService, times(1)).flush();
        inOrder.verify(this.actionService, times(1)).flush();
        inOrder.verify(this.nodeRepository, times(1)).saveAndFlush(eq(mockNode));
    }

    /**
     * Tests the {@link NodeService#upsert(Node)} method with a {@link Node} that contains data, verifying the correct
     * calls are made to save that data in the correct order.
     */
    @Test
    void testUpsertNodeWithData() {
        final Node node = mock(Node.class);
        final Action action = mock(Action.class);
        final List<Action> actionList = List.of(action);
        final List<ActionParameter> actionParameterList = List.of(mock(ActionParameter.class));

        when(node.getNodeSeq()).thenReturn(1L);
        when(this.nodeRepository.findById(any())).thenReturn(Optional.of(node));
        when(node.getActions()).thenReturn(actionList);
        when(action.getParameters()).thenReturn(actionParameterList);

        this.nodeService.upsert(node);
        verify(this.actionParameterService, times(1)).deleteAll(eq(actionParameterList), eq(actionParameterList));
        verify(this.actionService, times(1)).deleteAll(eq(actionList), eq(actionList));
        verify(this.actionParameterService, times(1)).saveAll(eq(actionParameterList));
        verify(this.actionService, times(1)).saveAll(eq(actionList));
        verify(this.actionParameterService, times(1)).flush();
        verify(this.actionService, times(1)).flush();
        verify(this.nodeRepository, times(1)).saveAndFlush(eq(node));
    }

    /**
     * Tests the {@link NodeService#getAllParametersForNode(Collection)} method.
     */
    @Test
    void getAllParametersForNode() {
        final Action action1 = mock(Action.class);
        final Action action2 = mock(Action.class);
        final ActionParameter actionParameter1 = mock(ActionParameter.class);
        final ActionParameter actionParameter2 = mock(ActionParameter.class);

        when(action1.getParameters()).thenReturn(List.of(actionParameter1));
        when(action2.getParameters()).thenReturn(List.of(actionParameter2));
        final Collection<ActionParameter> actual = this.nodeService.getAllParametersForNode(List.of(action1, action2));
        verify(action1, times(1)).getParameters();
        verify(action2, times(1)).getParameters();
        assertThat(actual, contains(actionParameter1, actionParameter2));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input, filtering on
     * {@link Node#getNodeSeq()}.
     */
    @Test
    void testPerformNodeSearchNodeNodeSeqFilter() {
        final Node node = mock(Node.class);
        when(node.getNodeSeq()).thenReturn(1L);
        when(this.nodeRepository.getById(any())).thenReturn(node);
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        verify(this.nodeRepository, times(1)).getById(eq(1L));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input, filtering on
     * {@link Room#getRoomSeq()} from the {@link Room} returned by {@link Node#getRoom()}.
     */
    @Test
    void testPerformNodeSearchNodeRoomSeqFilter() {
        final Node node = mock(Node.class);
        final Room room = mock(Room.class);
        when(node.getNodeSeq()).thenReturn(null);
        when(node.getRoom()).thenReturn(room);
        when(node.getHomeSeq()).thenReturn(2L);
        when(room.getRoomSeq()).thenReturn(1L);
        when(this.nodeRepository.getByRoomRoomSeqAndHomeHomeSeq(any(), any())).thenReturn(List.of(node));
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        verify(this.nodeRepository, times(1)).getByRoomRoomSeqAndHomeHomeSeq(eq(1L), eq(2L));

        when(this.nodeRepository.getByNameContainingIgnoreCaseAndRoomRoomSeqAndHomeHomeSeq(any(), any(), any())).thenReturn(List.of(node));
        when(node.getName()).thenReturn("test");
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        verify(this.nodeRepository, times(1)).getByNameContainingIgnoreCaseAndRoomRoomSeqAndHomeHomeSeq(eq("test"), eq(1L), eq(2L));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input, filtering on
     * {@link Node#getName()}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformNodeSearchNodeNameFilter() {
        final Node node = mock(Node.class);
        when(node.getNodeSeq()).thenReturn(null);
        when(node.getRoom()).thenReturn(null);
        when(node.getName()).thenReturn("test");
        when(this.nodeRepository.getByNameAndHomeHomeSeqIn(any(), any())).thenReturn(List.of(node));
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.nodeRepository, times(1)).getByNameAndHomeHomeSeqIn(eq("test"), captor.capture());
        assertThat(captor.getValue(), contains(2L));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input, filtering on
     * {@link Node#getName()} and {@link Home#getHomeSeq()} for the {@link Home} returned by {@link Node#getHome()}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformNodeSearchNodeNameFilterRoomNullRoomSeq() {
        final Node node = mock(Node.class);
        final Room room = mock(Room.class);
        when(node.getNodeSeq()).thenReturn(null);
        when(node.getName()).thenReturn("test");
        when(node.getRoom()).thenReturn(room);
        when(room.getRoomSeq()).thenReturn(null);
        when(this.nodeRepository.getByNameAndHomeHomeSeqIn(any(), any())).thenReturn(List.of(node));
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.nodeRepository, times(1)).getByNameAndHomeHomeSeqIn(eq("test"), captor.capture());
        assertThat(captor.getValue(), contains(2L));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input, filtering on the
     * {@link Home#getHomeSeq()} for the {@link Home} returned by {@link Node#getHome()}.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformNodeSearchNodeHomeSeqFilter() {
        final Node node = mock(Node.class);
        when(node.getNodeSeq()).thenReturn(null);
        when(node.getRoom()).thenReturn(null);
        when(node.getHomeSeq()).thenReturn(1L);
        when(this.nodeRepository.getByHomeHomeSeqIn(any())).thenReturn(List.of(node));
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.nodeRepository, times(1)).getByHomeHomeSeqIn(captor.capture());
        assertThat(captor.getValue(), contains(1L));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input, filtering on only the
     * user's allowable homeSeqs.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformNodeSearchNodeNullNodeSeqNullHomeSeq() {
        final Node node = mock(Node.class);
        when(node.getNodeSeq()).thenReturn(null);
        when(node.getHomeSeq()).thenReturn(null);
        when(this.nodeRepository.getByHomeHomeSeqIn(any())).thenReturn(List.of(node));
        assertThat(this.nodeService.performNodeSearch(node, List.of(2L)), is(equalTo(List.of(node))));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.nodeRepository, times(1)).getByHomeHomeSeqIn(captor.capture());
        assertThat(captor.getValue(), contains(2L));
    }

    /**
     * Tests the {@link NodeService#performNodeSearch(Node, Collection)} method with valid input (null filter),
     * defaulting to filtering on only the user's allowable homeSeqs.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testPerformNodeSearchNullFilter() {
        when(this.nodeRepository.getByHomeHomeSeqIn(any())).thenReturn(List.of());
        assertThat(this.nodeService.performNodeSearch(null, List.of(2L)), is(equalTo(Collections.emptyList())));
        final ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(this.nodeRepository, times(1)).getByHomeHomeSeqIn(captor.capture());
        assertThat(captor.getValue(), contains(2L));
    }
}
