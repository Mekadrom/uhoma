package com.higgs.server.web.service.dto;

import com.higgs.server.db.entity.Action;
import lombok.Data;

@Data
public class ActionRequest {
    private Long fromNodeSeq;
    private Long toNodeSeq;
    private Action actionWithParams;
    private String username;
}
