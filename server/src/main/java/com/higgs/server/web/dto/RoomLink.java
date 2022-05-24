package com.higgs.server.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
