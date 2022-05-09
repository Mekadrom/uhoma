package com.higgs.common.handler;

public interface ProxyHandlerFactory<T extends HandlerRequest, R extends HandlerResponse> {

    boolean qualifies(HandlerDefinition handlerDef);

    Handler<T, R> generate(HandlerDefinition handlerDef);

}
