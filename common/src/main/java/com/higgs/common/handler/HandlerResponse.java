package com.higgs.common.handler;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HandlerResponse {
    private boolean isExpected;
    private Long toNodeSeq;
    private Long fromNodeSeq;
    private String toUsername;
    private String fromUsername;

    public HandlerResponse(final HandlerRequest requestFor) {
        this.setExpected(requestFor.getReturnResponse());
        this.setToNodeSeq(requestFor.getToNodeSeq());
        this.setFromNodeSeq(requestFor.getFromNodeSeq());
        this.setToUsername(requestFor.getToUsername());
        this.setFromUsername(requestFor.getFromUsername());
    }
}
