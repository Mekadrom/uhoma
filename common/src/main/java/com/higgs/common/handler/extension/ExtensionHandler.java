package com.higgs.common.handler.extension;

import com.higgs.common.handler.Handler;
import com.higgs.common.handler.HandlerDefinition;
import com.higgs.common.handler.HandlerHandler;
import com.higgs.common.handler.HandlerRequest;
import com.higgs.common.handler.HandlerResponse;
import com.hubspot.jinjava.Jinjava;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class ExtensionHandler implements Handler<ExtensionHandlerRequest, HandlerResponse> {
    public static final String EXTENSION_HANDLER = "extension_handler";

    private static final Jinjava JINJA_ENGINE = new Jinjava();

    static final HandlerDefinition PROTOTYPE_HANDLER_DEF = new HandlerDefinition(
            Map.of(Handler.IS_EXTENSION, true),
            new HashMap<>()
    );

    @Override
    public List<HandlerResponse> handle(@NonNull final HandlerDefinition handlerDef,
                                        @NonNull final Map<String, List<String>> headers,
                                        @NonNull final ExtensionHandlerRequest request,
                                        @NonNull final HandlerHandler handlerHandler) {
        final Optional<? extends Handler<? extends HandlerRequest, ? extends HandlerResponse>> extendsFrom =
                handlerHandler.findHandlerByName(String.valueOf(handlerDef.getMetadata().get(Handler.EXTENDS_FROM)));
        if (extendsFrom.isPresent()) {
            return this.handleExtensionCall(extendsFrom.get(), handlerDef, headers, request, handlerHandler);
        }
        return Collections.emptyList();
    }

    private List<HandlerResponse> handleExtensionCall(@NonNull final Handler<? extends HandlerRequest, ? extends HandlerResponse> handler,
                                                      @NonNull final HandlerDefinition handlerDef,
                                                      @NonNull final Map<String, List<String>> headers,
                                                      @NonNull final ExtensionHandlerRequest request,
                                                      @NonNull final HandlerHandler handlerHandler) {
        final Map<String, Object> mockedRequestBody = new HashMap<>();
        final HandlerDefinition prototypeHandlerDef = handler.getPrototypeHandlerDef();

        // for every expected key in the extended handler's prototype definition, get that value from the extension's
        // handler def and use it as input
        prototypeHandlerDef.getDef().keySet().forEach(key -> mockedRequestBody.put(key, handlerDef.getDef().get(key)));
        this.interpolateStringValues(mockedRequestBody, request);
        return handlerHandler.process(prototypeHandlerDef, headers, mockedRequestBody);
    }

    private void interpolateStringValues(final Map<String, Object> mockedRequestBody, final ExtensionHandlerRequest request) {
        mockedRequestBody.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof String)
                .forEach(strEntry -> strEntry.setValue(ExtensionHandler.JINJA_ENGINE.render(((String) strEntry.getValue()), request)));
    }

    @Override
    public ExtensionHandlerRequest requestBodyToRequestObj(@NonNull final Map<String, Object> requestBody) {
        return new ExtensionHandlerRequest(requestBody);
    }

    @Override
    public boolean qualifies(@NonNull final HandlerDefinition handlerDef) {
        return this.isExtension(handlerDef);
    }

    @Override
    public String getName() {
        return ExtensionHandler.EXTENSION_HANDLER;
    }

    @Override
    public HandlerDefinition getPrototypeHandlerDef() {
        return ExtensionHandler.PROTOTYPE_HANDLER_DEF;
    }
}
