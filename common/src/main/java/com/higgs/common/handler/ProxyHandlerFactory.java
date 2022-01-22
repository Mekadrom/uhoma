package com.higgs.common.handler;

public interface ProxyHandlerFactory<T extends HandlerRequest, R extends HandlerResponse> {

    Handler<T, R> generate(HandlerDefinition handlerDef);

    boolean qualifies(HandlerDefinition handlerDef);

}
