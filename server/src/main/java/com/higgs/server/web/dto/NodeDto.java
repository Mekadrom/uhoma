package com.higgs.server.web.dto;

import com.higgs.server.db.entity.Action;
import com.higgs.server.db.entity.Home;
import com.higgs.server.db.entity.Room;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
public class NodeDto {
    @NotNull
    private Long nodeSeq;

    private String name;

    @NotNull
    private Home home;

    @NotNull
    private Room room;

    private Collection<Action> actions;
}
