package com.higgs.server.web.dto;

import lombok.Data;

@Data
public class ActionRequest {
    private ActionDto actionWithParams;
    private Long toNodeSeq;
    private Long fromNodeSeq;
    private String toUsername;
    private String fromUsername;
}
