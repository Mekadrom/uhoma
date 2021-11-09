package com.higgs.server.web.service.dto;

import com.higgs.server.db.entity.Action;
import lombok.Data;

@Data
public class ActionRequest {
    // {"fromNodeSeq":0,"toNodeSeq":1,"actionWithParams":{"actionSeq":1,"ownerNodeSeq":1,"name":"Test TV action 1","actionHandler":null,"parameters":[]},"sentEpoch":1636443089533}
    private Long fromNodeSeq;

    private Long toNodeSeq;

    private Action actionWithParams;
}
