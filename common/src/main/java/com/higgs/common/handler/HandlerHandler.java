package com.higgs.common.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.util.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class HandlerHandler {
    private final CommonUtils commonUtils;
    private final List<Handler<HandlerRequest, HandlerResponse>> handlers;
    private final ProxyHandlerGenerator proxyHandlerGenerator;

    public List<HandlerResponse> process(@NonNull final Map<String, List<String>> kafkaHeaders,
                                         @NonNull final String body) throws IOException {
        return this.process(kafkaHeaders, this.commonUtils.parseMap(body));
    }

    List<HandlerResponse> process(@NonNull final Map<String, List<String>> headers,
                                  @NonNull final Map<String, Object> body) throws JsonProcessingException {
        return this.process(this.parseHandlerDef(String.valueOf(headers.get(HAKafkaConstants.HEADER_ACTION_HANDLER_DEF))), headers, body);
    }

    HandlerDefinition parseHandlerDef(final String handlerDef) throws JsonProcessingException {
        return this.commonUtils.getDefaultMapper().readValue(handlerDef, HandlerDefinition.class);
    }

    public List<HandlerResponse> process(final HandlerDefinition handlerDef,
                                         @NonNull final Map<String, List<String>> headers,
                                         @NonNull final Map<String, Object> body) {
        final List<HandlerResponse> responses = new ArrayList<>();
        responses.addAll(this.processDefaultHandlers(handlerDef, headers, body));
        responses.addAll(this.processWithProxyHandlers(handlerDef, headers, body));
        return responses;
    }

    List<HandlerResponse> processDefaultHandlers(final HandlerDefinition handlerDef,
                                                 @NonNull final Map<String, List<String>> headers,
                                                 @NonNull final Map<String, Object> body) {
        return this.handlers.stream()
                .filter(handler -> handler.qualifies(handlerDef))
                .map(handler -> handler.handle(handlerDef, headers, body, this))
                .collect(ArrayList::new, List::addAll, List::addAll);
    }

    List<HandlerResponse> processWithProxyHandlers(final HandlerDefinition handlerDef,
                                                   @NonNull final Map<String, List<String>> headers,
                                                   @NonNull final Map<String, Object> body) {
        return this.proxyHandlerGenerator.buildProxyHandlers(handlerDef).stream()
                .map(handler -> handler.handle(handlerDef, headers, body, this))
                .collect(ArrayList::new, List::addAll, List::addAll);
    }

    public Optional<Handler<HandlerRequest, HandlerResponse>> findHandlerByName(final String handlerName) {
        return this.handlers.stream().filter(it -> it.getName().equals(handlerName)).findFirst();
    }
}
