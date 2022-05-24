package com.higgs.server.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomDto {
    @NotNull
    private Long roomSeq;

    @NotNull
    private String name;

    private HomeDto home;
}
