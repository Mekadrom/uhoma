package com.higgs.common.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProxyHandlerGeneratorTest {
    private ProxyHandlerGenerator proxyHandlerGenerator;

    @BeforeEach
    void setUp() {
        this.proxyHandlerGenerator = new ProxyHandlerGenerator();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testBuildProxyHandlers() {
        final HandlerDefinition handlerDefinition1 = new HandlerDefinition(Map.of("1", "1"), Map.of("2", "2"));
        final HandlerDefinition handlerDefinition2 = new HandlerDefinition(Map.of("3", "3"), Map.of("4", "4"));
        final ProxyHandlerFactory<HandlerRequest, HandlerResponse> proxyHandlerFactory = mock(ProxyHandlerFactory.class);
        final Handler<HandlerRequest, HandlerResponse> handler1 = mock(Handler.class);
        final Handler<HandlerRequest, HandlerResponse> handler2 = mock(Handler.class);
        when(proxyHandlerFactory.qualifies(handlerDefinition1)).thenReturn(true);
        when(proxyHandlerFactory.qualifies(handlerDefinition2)).thenReturn(true);
        when(proxyHandlerFactory.generate(handlerDefinition1)).thenReturn(handler1);
        when(proxyHandlerFactory.generate(handlerDefinition2)).thenReturn(handler2);
        this.proxyHandlerGenerator.addHandlerFactory(proxyHandlerFactory);
        final List<Handler<HandlerRequest, HandlerResponse>> actual1 = this.proxyHandlerGenerator.buildProxyHandlers(handlerDefinition1);
        final List<Handler<HandlerRequest, HandlerResponse>> actual2 = this.proxyHandlerGenerator.buildProxyHandlers(handlerDefinition2);
        verify(proxyHandlerFactory, times(1)).qualifies(handlerDefinition1);
        verify(proxyHandlerFactory, times(1)).qualifies(handlerDefinition2);
        verify(proxyHandlerFactory, times(1)).generate(handlerDefinition1);
        verify(proxyHandlerFactory, times(1)).generate(handlerDefinition2);
        assertThat(actual1, is(List.of(handler1)));
        assertThat(actual2, is(List.of(handler2)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveHandlerFactory() {
        final HandlerDefinition handlerDefinition = new HandlerDefinition(Map.of("1", "1"), Map.of("2", "2"));
        final ProxyHandlerFactory<HandlerRequest, HandlerResponse> proxyHandlerFactory = mock(ProxyHandlerFactory.class);
        final Handler<HandlerRequest, HandlerResponse> handler = mock(Handler.class);
        when(proxyHandlerFactory.qualifies(handlerDefinition)).thenReturn(true);
        when(proxyHandlerFactory.generate(handlerDefinition)).thenReturn(handler);
        this.proxyHandlerGenerator.addHandlerFactory(proxyHandlerFactory);
        this.proxyHandlerGenerator.removeHandlerFactory(proxyHandlerFactory);
        final List<Handler<HandlerRequest, HandlerResponse>> actualAfter = this.proxyHandlerGenerator.buildProxyHandlers(handlerDefinition);
        verify(proxyHandlerFactory, times(0)).qualifies(handlerDefinition);
        verify(proxyHandlerFactory, times(0)).generate(handlerDefinition);
        assertThat(actualAfter, is(List.of()));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAddAllHandlerFactories() {
        final HandlerDefinition handlerDefinition1 = new HandlerDefinition(Map.of("1", "1"), Map.of("2", "2"));
        final HandlerDefinition handlerDefinition2 = new HandlerDefinition(Map.of("3", "3"), Map.of("4", "4"));
        final ProxyHandlerFactory<HandlerRequest, HandlerResponse> proxyHandlerFactory1 = mock(ProxyHandlerFactory.class);
        final ProxyHandlerFactory<HandlerRequest, HandlerResponse> proxyHandlerFactory2 = mock(ProxyHandlerFactory.class);
        final Handler<HandlerRequest, HandlerResponse> handler1 = mock(Handler.class);
        final Handler<HandlerRequest, HandlerResponse> handler2 = mock(Handler.class);
        when(proxyHandlerFactory1.qualifies(handlerDefinition1)).thenReturn(true);
        when(proxyHandlerFactory1.qualifies(handlerDefinition2)).thenReturn(false);
        when(proxyHandlerFactory2.qualifies(handlerDefinition1)).thenReturn(false);
        when(proxyHandlerFactory2.qualifies(handlerDefinition2)).thenReturn(true);
        when(proxyHandlerFactory1.generate(handlerDefinition1)).thenReturn(handler1);
        when(proxyHandlerFactory2.generate(handlerDefinition2)).thenReturn(handler2);
        this.proxyHandlerGenerator.addAllHandlerFactories(List.of(proxyHandlerFactory1, proxyHandlerFactory2));
        final List<Handler<HandlerRequest, HandlerResponse>> actual1 = this.proxyHandlerGenerator.buildProxyHandlers(handlerDefinition1);
        final List<Handler<HandlerRequest, HandlerResponse>> actual2 = this.proxyHandlerGenerator.buildProxyHandlers(handlerDefinition2);
        verify(proxyHandlerFactory1, times(1)).qualifies(handlerDefinition1);
        verify(proxyHandlerFactory2, times(1)).qualifies(handlerDefinition2);
        verify(proxyHandlerFactory1, times(1)).generate(handlerDefinition1);
        verify(proxyHandlerFactory2, times(1)).generate(handlerDefinition2);
        assertThat(actual1, is(List.of(handler1)));
        assertThat(actual2, is(List.of(handler2)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveAllHandlerFactories() {
        final HandlerDefinition handlerDefinition = new HandlerDefinition(Map.of("1", "1"), Map.of("2", "2"));
        final ProxyHandlerFactory<HandlerRequest, HandlerResponse> proxyHandlerFactory1 = mock(ProxyHandlerFactory.class);
        final ProxyHandlerFactory<HandlerRequest, HandlerResponse> proxyHandlerFactory2 = mock(ProxyHandlerFactory.class);
        final Handler<HandlerRequest, HandlerResponse> handler = mock(Handler.class);
        when(proxyHandlerFactory1.qualifies(handlerDefinition)).thenReturn(true);
        when(proxyHandlerFactory1.generate(handlerDefinition)).thenReturn(handler);
        when(proxyHandlerFactory2.qualifies(handlerDefinition)).thenReturn(true);
        when(proxyHandlerFactory2.generate(handlerDefinition)).thenReturn(handler);
        this.proxyHandlerGenerator.addAllHandlerFactories(List.of(proxyHandlerFactory1, proxyHandlerFactory2));
        this.proxyHandlerGenerator.removeAllHandlerFactories(List.of(proxyHandlerFactory1, proxyHandlerFactory2));
        final List<Handler<HandlerRequest, HandlerResponse>> actualAfter = this.proxyHandlerGenerator.buildProxyHandlers(handlerDefinition);
        verify(proxyHandlerFactory1, times(0)).qualifies(handlerDefinition);
        verify(proxyHandlerFactory1, times(0)).generate(handlerDefinition);
        verify(proxyHandlerFactory2, times(0)).qualifies(handlerDefinition);
        verify(proxyHandlerFactory2, times(0)).generate(handlerDefinition);
        assertThat(actualAfter, is(List.of()));
    }
}
