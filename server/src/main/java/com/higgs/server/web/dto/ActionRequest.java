package com.higgs.server.web.dto;

import com.higgs.server.db.entity.Action;
import lombok.Data;

@Data
public class ActionRequest {
    private Action actionWithParams;
    private Long fromNodeSeq;
    private Long toNodeSeq;
    private String username;
}
