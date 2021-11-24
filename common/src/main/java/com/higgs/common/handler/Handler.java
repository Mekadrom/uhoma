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
    String EXTENDS_FROM = "extends_from";

    R handle(@NonNull T request);

    default R handle(@NonNull final Map<String, List<String>> headers, @NonNull final Map<String, Object> requestBody) {
        return this.handle(this.processRequest(headers, requestBody));
    }

    private T processRequest(@NonNull final Map<String, List<String>> headers, @NonNull final Map<String, Object> requestBody) {
        final T request = this.requestBodyToRequestObj(requestBody);
        request.setToNodeSeq(this.getLongHeader(headers, HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ));
        request.setFromNodeSeq(this.getLongHeader(headers, HAKafkaConstants.HEADER_SENDING_NODE_SEQ));
        request.setToUserSeq(this.getLongHeader(headers, HAKafkaConstants.HEADER_RECEIVING_USER_SEQ));
        request.setFromUserSeq(this.getLongHeader(headers, HAKafkaConstants.HEADER_SENDING_USER_SEQ));
        return request;
    }

    private Long getLongHeader(@NonNull final Map<String, List<String>> headers, @NonNull final String headerName) {
        return headers.getOrDefault(headerName, new ArrayList<>()).stream()
                .filter(StringUtils::isNumeric)
                .map(Long::valueOf)
                .findAny()
                .orElse(null);
    }

    T requestBodyToRequestObj(@NonNull Map<String, Object> requestBody);

    boolean qualifies(@NonNull Map<String, Object> handlerDef);

    default boolean isBuiltin(@NonNull final Map<String, Object> handlerDef) {
        return Boolean.TRUE.equals(Boolean.valueOf(String.valueOf(handlerDef.get(Handler.IS_BUILTIN))));
    }

    default boolean builtinTypeIs(@NonNull final Map<String, Object> handlerDef, @NonNull final String fieldValue) {
        return fieldValue.equals(String.valueOf(handlerDef.get(Handler.BUILTIN_TYPE)));
    }

    default boolean extendsFrom(@NonNull final Map<String, Object> handlerDef, @NonNull final String fieldValue) {
        return fieldValue.equals(String.valueOf(handlerDef.get(Handler.EXTENDS_FROM)));
    }
}
