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
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class ExtensionHandler implements Handler<ExtensionHandlerRequest, HandlerResponse> {
    public static final String NAME = "extension_handler";

    private static final Jinjava JINJA_ENGINE = new Jinjava();

    static final HandlerDefinition PROTOTYPE_HANDLER_DEF = new HandlerDefinition(
            Map.of(Handler.IS_EXTENSION, true),
            new HashMap<>()
    );

    @Override
    public List<HandlerResponse> handle(@NonNull final HandlerDefinition handlerDef,
                                        @NonNull final Map<String, Object> headers,
                                        @NonNull final ExtensionHandlerRequest request,
                                        @NonNull final HandlerHandler handlerHandler) {
        final Optional<Handler<? extends HandlerRequest, ? extends HandlerResponse>> extendsFrom =
                handlerHandler.findHandlerByName(String.valueOf(handlerDef.getMetadata().get(Handler.EXTENDS_FROM)));
        if (extendsFrom.isPresent()) {
            final Handler<? extends HandlerRequest, ? extends HandlerResponse> parent = extendsFrom.get();
            ExtensionHandler.log.debug("Handling extended action which extends from: {}", parent.getName());
            return this.handleExtensionCall(parent, handlerDef, headers, request, handlerHandler);
        }
        return Collections.emptyList();
    }

    List<HandlerResponse> handleExtensionCall(@NonNull final Handler<? extends HandlerRequest, ? extends HandlerResponse> handler,
                                              @NonNull final HandlerDefinition extensionHandlerDef,
                                              @NonNull final Map<String, Object> headers,
                                              @NonNull final ExtensionHandlerRequest request,
                                              @NonNull final HandlerHandler handlerHandler) {
        final HandlerDefinition prototypeHandlerDef = handler.getPrototypeHandlerDef();
        ExtensionHandler.log.info("Handling extension call for handler def: {}", prototypeHandlerDef);
        final Map<String, Object> fieldsToInterpolate = this.copyFieldTemplateValues(prototypeHandlerDef, extensionHandlerDef);
        ExtensionHandler.log.info("pre-interpolated fields: {}", fieldsToInterpolate);
        final Map<String, Object> interpolatedFieldValues = this.interpolateFieldValues(fieldsToInterpolate, request);
        ExtensionHandler.log.info("interpolated fields: {}", interpolatedFieldValues);
        return handlerHandler.process(prototypeHandlerDef, headers, interpolatedFieldValues);
    }

    Map<String, Object> copyFieldTemplateValues(@NonNull final HandlerDefinition prototypeHandlerDef, @NonNull final HandlerDefinition extensionHandlerDef) {
        return this.copyFieldTemplateValues(prototypeHandlerDef.getDef(), extensionHandlerDef.getDef());
    }

    /**
     * @param prototypeDef the handler definition map for the parent handler
     * @param extensionDef the handler definition map for the child handler
     * @return a map containing only fields that are expected by the parent handler, to be interpolated
     */
    Map<String, Object> copyFieldTemplateValues(@NonNull final Map<String, Object> prototypeDef, @NonNull final Map<String, Object> extensionDef) {
        // for every expected key in the extended handler's prototype definition, get that value from the extension's
        // handler def and use it as input
        return extensionDef.keySet().stream()
                .filter(prototypeDef.keySet()::contains)
                .collect(Collectors.toMap(key -> key, extensionDef::get));
    }

    Map<String, Object> interpolateFieldValues(@NonNull final Map<String, Object> toInterpolate, @NonNull final ExtensionHandlerRequest request) {
        return toInterpolate.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            if (entry.getValue() instanceof String strEntry) {
                return this.getJinjaEngine().render(strEntry, request.getParameters());
            }
            return entry.getValue();
        }));
    }

    Jinjava getJinjaEngine() {
        return ExtensionHandler.JINJA_ENGINE;
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
        return ExtensionHandler.NAME;
    }

    @Override
    public HandlerDefinition getPrototypeHandlerDef() {
        return ExtensionHandler.PROTOTYPE_HANDLER_DEF;
    }
}
