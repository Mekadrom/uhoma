package com.higgs.common.handler;

import java.util.Map;

public interface ProxyHandlerFactory<T extends HandlerRequest, R extends HandlerResponse> {

    Handler<T, R> generate(Map<String, Object> handlerDef);

    boolean qualifies(Map<String, Object> handlerDef);

}
