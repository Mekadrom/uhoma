package com.higgs.common.handler;

import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProxyHandlerGenerator implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Set<ProxyHandlerFactory<HandlerRequest, HandlerResponse>> handlerFactories = new HashSet<>();

    public List<Handler<HandlerRequest, HandlerResponse>> buildProxyHandlers(final HandlerDefinition handlerDef) {
        return this.handlerFactories.stream()
                .filter(factory -> factory.qualifies(handlerDef))
                .map(factory -> factory.generate(handlerDef))
                .collect(Collectors.toList());
    }

    public void addHandlerFactory(final ProxyHandlerFactory<HandlerRequest, HandlerResponse> handlerFactory) {
        this.handlerFactories.add(handlerFactory);
    }

    public void addAllHandlerFactories(final Iterable<ProxyHandlerFactory<HandlerRequest, HandlerResponse>> handlerFactories) {
        handlerFactories.forEach(this::addHandlerFactory);
    }

    public void removeHandlerFactory(final ProxyHandlerFactory<HandlerRequest, HandlerResponse> handlerFactory) {
        this.handlerFactories.remove(handlerFactory);
    }

    public void removeAllHandlerFactories(final Iterable<ProxyHandlerFactory<HandlerRequest, HandlerResponse>> handlerFactories) {
        handlerFactories.forEach(this::removeHandlerFactory);
    }
}
