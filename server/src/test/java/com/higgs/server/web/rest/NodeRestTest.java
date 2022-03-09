package com.higgs.server.web.rest;

import com.higgs.server.db.entity.Node;
import com.higgs.server.db.entity.Room;
import com.higgs.server.web.rest.util.RestUtils;
import com.higgs.server.web.svc.NodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NodeRest}.
 */
@ExtendWith(MockitoExtension.class)
class NodeRestTest {
    @Mock
    private NodeService nodeService;

    @Mock
    private RestUtils restUtils;

    private NodeRest nodeRest;

    @BeforeEach
    void setUp() {
        this.nodeRest = new NodeRest(this.nodeService, this.restUtils);
    }

    /**
     * Tests the method {@link NodeRest#upsertNode(Node, Principal)}. This method should return a {@link ResponseEntity}
     * with the status code {@code 200} if the upsert was successful, and a body containing the updated {@link Node} DTO
     */
    @Test
    void testUpsertNode() {
        final Node node = mock(Node.class);
        final Principal principal = mock(Principal.class);
        when(this.nodeService.upsert(any())).thenReturn(node);
        final ResponseEntity<Node> actual = this.nodeRest.upsertNode(node, principal);
        verify(this.restUtils, times(1)).filterInvalidRequest(any(), any());
        verify(this.nodeService, times(1)).upsert(eq(node));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(node))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link NodeRest#upsertNode(Node, Principal)} with invalid input (null args). This method should
     * throw an {@link IllegalArgumentException} if either input is null.
     * @param node a mock {@link Node}
     * @param principal a mock {@link Principal}
     */
    @ParameterizedTest
    @MethodSource("getTestUpsertNodeNullsParams")
    void testUpsertNodeNulls(final Node node, final Principal principal) {
        assertThrows(IllegalArgumentException.class, () -> this.nodeRest.upsertNode(node, principal));
    }

    public static Stream<Arguments> getTestUpsertNodeNullsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, mock(Principal.class)),
                Arguments.of(mock(Node.class), null)
        );
    }

    /**
     * Tests the method {@link NodeRest#upsertNodes(List, Principal)}. This method should return a
     * {@link ResponseEntity} with the status code {@code 200} if the upsert was successful, and a body containing the
     * updated {@link Node} DTOs.
     */
    @Test
    void testUpsertNodes() {
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Principal principal = mock(Principal.class);
        final List<Node> nodes = List.of(node1, node2);
        when(this.nodeService.upsert(any())).thenReturn(node1).thenReturn(node2);
        final ResponseEntity<List<Node>> actual = this.nodeRest.upsertNodes(nodes, principal);
        verify(this.restUtils, times(2)).filterInvalidRequest(any(), any());
        verify(this.nodeService, times(2)).upsert(any());
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(nodes))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link NodeRest#upsertNodes(List, Principal)} with invalid input (null args). This method should
     * throw an {@link IllegalArgumentException} if either input is null.
     * @param nodes a {@link List} of mock {@link Node}
     * @param principal a mock {@link Principal}
     */
    @ParameterizedTest
    @MethodSource("getTestUpsertNodesNullsParams")
    void testUpsertNodesNulls(final List<Node> nodes, final Principal principal) {
        assertThrows(IllegalArgumentException.class, () -> this.nodeRest.upsertNodes(nodes, principal));
    }

    public static Stream<Arguments> getTestUpsertNodesNullsParams() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, mock(Principal.class)),
                Arguments.of(mock(List.class), null)
        );
    }

    /**
     * Tests the method {@link NodeRest#search(Node, Principal)}. This method should return a {@link ResponseEntity}
     * with the status code {@code 200} if the search was successful, and a body containing the filtered {@link Node}
     * DTOs.
     */
    @Test
    void testSearch() {
        final Node node = mock(Node.class);
        final Principal principal = mock(Principal.class);
        when(this.nodeService.performNodeSearch(any(), any())).thenReturn(List.of(node));
        final ResponseEntity<List<Node>> actual = this.nodeRest.search(node, principal);
        verify(this.restUtils, times(1)).getHomeSeqs(eq(principal));
        assertAll(
                () -> assertThat(actual.getBody(), is(equalTo(List.of(node)))),
                () -> assertThat(actual.getStatusCodeValue(), is(equalTo(200)))
        );
    }

    /**
     * Tests the method {@link NodeRest#search(Node, Principal)} with invalid input (null args). This method should
     * throw an {@link IllegalArgumentException} if either input is null.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void testSearchNulls() {
        assertThrows(IllegalArgumentException.class, () -> this.nodeRest.search(mock(Node.class), null));
    }
}
