package com.higgs.common.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class HandlerRequest extends HashMap<String, Object> {
    @Serial
    private static final long serialVersionUID = 329L;

    public HandlerRequest(final Map<String, Object> input) {
        super(input);
    }

    private Boolean returnResponse;
    private Long toNodeSeq;
    private Long fromNodeSeq;
    private Long toUserSeq;
    private Long fromUserSeq;
}
