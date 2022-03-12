package com.higgs.server.web.dto;

import com.higgs.server.db.entity.Room;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoomLink {
    @NotNull
    private Long roomLinkSeq;

    @NotNull
    private Room startRoom;

    @NotNull
    private Room endRoom;

    @NotNull
    private String transitionLocationDef;
}
