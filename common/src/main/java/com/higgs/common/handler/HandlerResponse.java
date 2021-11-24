package com.higgs.common.handler;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class HandlerResponse {
    public HandlerResponse(final HandlerRequest requestFor) {
        this.setExpected(requestFor.getReturnResponse());
        this.setToNodeSeq(requestFor.getToNodeSeq());
        this.setFromNodeSeq(requestFor.getFromNodeSeq());
        this.setToUserSeq(requestFor.getToUserSeq());
        this.setFromUserSeq(requestFor.getFromUserSeq());
    }

    private boolean isExpected;
    private Long toNodeSeq;
    private Long fromNodeSeq;
    private Long toUserSeq;
    private Long fromUserSeq;
}
