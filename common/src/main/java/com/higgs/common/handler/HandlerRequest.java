package com.higgs.common.handler;

import lombok.Data;

@Data
public abstract class HandlerRequest {
    private Boolean returnResponse;
    private Long fromNodeSeq;
    private String fromUserName;
    private Long toNodeSeq;
}
