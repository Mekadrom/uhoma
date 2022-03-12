package com.higgs.server.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoomLink {
    @NotNull
    private Long roomLinkSeq;

    @NotNull
    private RoomDto startRoom;

    @NotNull
    private RoomDto endRoom;

    @NotNull
    private String transitionLocationDef;
}
