package com.higgs.common.handler;

import lombok.Data;

@Data
public abstract class HandlerRequest {
    private Boolean returnResponse;
    private Long toNodeSeq;
    private Long fromNodeSeq;
    private Long toUserSeq;
    private Long fromUserSeq;
}
