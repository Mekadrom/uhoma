package com.higgs.server.web.dto;

import com.higgs.server.db.entity.Action;
import lombok.Data;

@Data
public class ActionRequest {
    private Action actionWithParams;
    private Long toNodeSeq;
    private Long fromNodeSeq;
    private String toUsername;
    private String fromUsername;
}
