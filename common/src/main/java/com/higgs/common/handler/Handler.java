package com.higgs.common.handler;

import com.higgs.common.kafka.HAKafkaConstants;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Handler<T extends HandlerRequest, R extends HandlerResponse> {
    String BUILTIN_TYPE = "builtin_type";
    String IS_BUILTIN = "is_builtin";
    String IS_EXTENSION = "is_extension";
    String EXTENDS_FROM = "extends_from";

    List<R> handle(HandlerDefinition handlerDef, @NonNull Map<String, List<String>> headers, @NonNull T request, @NonNull HandlerHandler handlerHandler);

    default List<R> handle(final HandlerDefinition handlerDef, @NonNull final Map<String, List<String>> headers, @NonNull final Map<String, Object> requestBody, @NonNull final HandlerHandler handlerHandler) {
        return this.handle(handlerDef, headers, this.processRequest(headers, requestBody), handlerHandler);
    }

    default T processRequest(@NonNull final Map<String, List<String>> headers, @NonNull final Map<String, Object> requestBody) {
        final T request = this.requestBodyToRequestObj(requestBody);
        request.setToNodeSeq(this.getLongHeader(headers, HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ));
        request.setFromNodeSeq(this.getLongHeader(headers, HAKafkaConstants.HEADER_SENDING_NODE_SEQ));
        request.setToUsername(headers.get(HAKafkaConstants.HEADER_RECEIVING_USERNAME).stream().findAny().orElse(null));
        request.setFromUsername(headers.get(HAKafkaConstants.HEADER_SENDING_USERNAME).stream().findAny().orElse(null));
        return request;
    }

    default Long getLongHeader(@NonNull final Map<String, List<String>> headers, @NonNull final String headerName) {
        return headers.getOrDefault(headerName, new ArrayList<>()).stream()
                .filter(StringUtils::isNumeric)
                .map(Long::valueOf)
                .findAny()
                .orElse(null);
    }

    T requestBodyToRequestObj(@NonNull Map<String, Object> requestBody);

    default boolean qualifies(@NonNull final HandlerDefinition handlerDef) {
        return (this.isBuiltin(handlerDef) && this.builtinTypeIs(handlerDef, this.getName())) || this.extendsFrom(handlerDef, this.getName());
    }

    default boolean isBuiltin(@NonNull final HandlerDefinition handlerDef) {
        return this.isBuiltin(handlerDef.getMetadata());
    }

    default boolean isBuiltin(@NonNull final Map<String, Object> metadataMap) {
        return Boolean.TRUE.equals(Boolean.valueOf(String.valueOf(metadataMap.get(Handler.IS_BUILTIN))));
    }

    default boolean builtinTypeIs(@NonNull final HandlerDefinition handlerDef, @NonNull final String fieldValue) {
        return this.builtinTypeIs(handlerDef.getMetadata(), fieldValue);
    }

    default boolean builtinTypeIs(@NonNull final Map<String, Object> metadataMap, @NonNull final String fieldValue) {
        return fieldValue.equals(String.valueOf(metadataMap.get(Handler.BUILTIN_TYPE)));
    }

    default boolean extendsFrom(@NonNull final HandlerDefinition handlerDef, @NonNull final String fieldValue) {
        return this.isExtension(handlerDef) && this.extendsFrom(handlerDef.getMetadata(), fieldValue);
    }

    default boolean isExtension(@NonNull final HandlerDefinition handlerDef) {
        return this.isExtension(handlerDef.getMetadata());
    }

    default boolean isExtension(@NonNull final Map<String, Object> metadataMap) {
        return Boolean.TRUE.equals(Boolean.valueOf(String.valueOf(metadataMap.get(Handler.IS_EXTENSION))));
    }

    default boolean extendsFrom(@NonNull final Map<String, Object> handlerDef, @NonNull final String fieldValue) {
        return fieldValue.equals(String.valueOf(handlerDef.get(Handler.EXTENDS_FROM)));
    }

    String getName();

    HandlerDefinition getPrototypeHandlerDef();

}
