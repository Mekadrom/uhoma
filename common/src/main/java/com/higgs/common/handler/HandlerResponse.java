package com.higgs.common.handler;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class HandlerResponse {
    public HandlerResponse(final HandlerRequest requestFor) {
        this.setExpected(requestFor.getReturnResponse());
        this.setFromNodeSeq(requestFor.getFromNodeSeq());
        this.setFromUserName(requestFor.getFromUserName());
        this.setToNodeSeq(requestFor.getToNodeSeq());
    }

    private boolean isExpected;
    private Long fromNodeSeq;
    private String fromUserName;
    private Long toNodeSeq;
}
