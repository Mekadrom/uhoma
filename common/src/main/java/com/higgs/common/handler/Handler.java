package com.higgs.common.handler;

import com.higgs.common.kafka.HAKafkaConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public interface Handler<T extends HandlerRequest, R extends HandlerResponse> {
    String BUILTIN_TYPE = "builtin_type";
    String IS_BUILTIN = "is_builtin";

    R handle(T request);

    default R handle(final Map<String, List<String>> headers, final Map<String, Object> requestBody) {
        return this.handle(this.processRequest(headers, requestBody));
    }

    private T processRequest(final Map<String, List<String>> headers, final Map<String, Object> requestBody) {
        final String toNodeSeq = headers.get(HAKafkaConstants.HEADER_RECEIVING_NODE_SEQ).stream().findAny().orElse(null);
        final String fromNodeSeq = headers.get(HAKafkaConstants.HEADER_SENDING_NODE_SEQ).stream().findAny().orElse(null);
        final String fromUsername = headers.get(HAKafkaConstants.HEADER_SENDING_USER_NAME).stream().findAny().orElse(null);
        return this.processRequest(toNodeSeq, fromNodeSeq, fromUsername, requestBody);
    }

    private T processRequest(final String toNodeSeq, final String fromNodeSeq, final String fromUsername, final Map<String, Object> requestBody) {
        if (!StringUtils.isNumeric(toNodeSeq)) {
            throw new IllegalArgumentException("toNodeSeq must be a number");
        }
        if (!StringUtils.isNumeric(fromNodeSeq)) {
            throw new IllegalArgumentException("toNodeSeq must be a number");
        }

        final T request = this.requestBodyToRequestObj(requestBody);
        request.setToNodeSeq(Long.valueOf(toNodeSeq));
        request.setFromNodeSeq(Long.valueOf(fromNodeSeq));
        request.setFromUserName(fromUsername);
        return request;
    }

    T requestBodyToRequestObj(Map<String, Object> requestBody);

    boolean qualifies(Map<String, Object> handlerDef);

}
