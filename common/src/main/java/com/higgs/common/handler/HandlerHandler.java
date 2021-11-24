package com.higgs.common.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.higgs.common.kafka.HAKafkaConstants;
import com.higgs.common.util.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HandlerHandler {
    private final CommonUtil commonUtil;
    private final List<? extends Handler<? extends HandlerRequest, ? extends HandlerResponse>> handlers;
    private final ProxyHandlerGenerator proxyHandlerGenerator;

    public List<? extends HandlerResponse> process(@NonNull final Map<String, List<String>> kafkaHeaders,
                                                   @NonNull final String body) throws IOException {
        return this.process(kafkaHeaders, this.commonUtil.parseMap(body));
    }

    private List<? extends HandlerResponse> process(@NonNull final Map<String, List<String>> headers,
                                                    @NonNull final Map<String, Object> body) throws JsonProcessingException {
        return this.process(this.commonUtil.parseMap(String.valueOf(headers.get(HAKafkaConstants.HEADER_ACTION_HANDLER_DEF))), headers, body);
    }

    private List<? extends HandlerResponse> process(@NonNull final Map<String, Object> handlerDef,
                                                    @NonNull final Map<String, List<String>> headers,
                                                    @NonNull final Map<String, Object> body) {
        final List<HandlerResponse> responses = new ArrayList<>();
        responses.addAll(this.processDefaultHandlers(handlerDef, headers, body));
        responses.addAll(this.processWithProxyHandlers(handlerDef, headers, body));
        return responses;
    }

    private List<? extends HandlerResponse> processDefaultHandlers(@NonNull final Map<String, Object> handlerDef,
                                                                   @NonNull final Map<String, List<String>> headers,
                                                                   @NonNull final Map<String, Object> body) {
        return this.handlers.stream()
                .filter(handler -> handler.qualifies(handlerDef))
                .map(handler -> handler.handle(headers, body))
                .collect(Collectors.toList());
    }

    private List<? extends HandlerResponse> processWithProxyHandlers(@NonNull final Map<String, Object> handlerDef,
                                                                     @NonNull final Map<String, List<String>> headers,
                                                                     @NonNull final Map<String, Object> body) {
        return this.proxyHandlerGenerator.buildProxyHandlers(handlerDef).stream()
                .map(handler -> handler.handle(headers, body))
                .collect(Collectors.toList());
    }
}
