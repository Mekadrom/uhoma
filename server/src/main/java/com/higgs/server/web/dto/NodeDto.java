package com.higgs.server.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;

@Data
public class NodeDto {
    @NotNull
    private Long nodeSeq;

    private String name;

    @NotNull
    private HomeDto home;

    @NotNull
    private RoomDto room;

    private Collection<ActionDto> actions;
}
