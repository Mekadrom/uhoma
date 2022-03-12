package com.higgs.server.web.dto;

import com.higgs.server.db.entity.Home;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RoomDto {
    @NotNull
    private Long roomSeq;

    @NotNull
    private String name;

    private Home home;
}
