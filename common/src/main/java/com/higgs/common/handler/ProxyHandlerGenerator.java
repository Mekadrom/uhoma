package com.higgs.common.handler;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProxyHandlerGenerator {
    private final Set<HandlerFactory<? extends HandlerRequest, ? extends HandlerResponse>> handlerFactories = new HashSet<>();

    public List<? extends Handler<? extends HandlerRequest, ? extends HandlerResponse>> buildProxyHandlers(final Map<String, Object> handlerDef) {
        return this.handlerFactories.stream()
                .filter(factory -> factory.qualifies(handlerDef))
                .map(factory -> factory.generate(handlerDef))
                .collect(Collectors.toList());
    }

    public void addHandlerFactory(final HandlerFactory<? extends HandlerRequest, ? extends HandlerResponse> handlerFactory) {
        this.handlerFactories.add(handlerFactory);
    }

    public void addAllHandlerFactories(final Iterable<HandlerFactory<? extends HandlerRequest, ? extends HandlerResponse>> handlerFactories) {
        handlerFactories.forEach(this::addHandlerFactory);
    }

    public void removeHandlerFactory(final HandlerFactory<? extends HandlerRequest, ? extends HandlerResponse> handlerFactory) {
        this.handlerFactories.remove(handlerFactory);
    }

    public void removeAllHandlerFactories(final Iterable<HandlerFactory<? extends HandlerRequest, ? extends HandlerResponse>> handlerFactories) {
        handlerFactories.forEach(this::removeHandlerFactory);
    }
}
