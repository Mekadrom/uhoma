package com.higgs.server.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class HomeDto {
    @NotNull
    private Long homeSeq;

    @NotNull
    private Date created;

    @NotNull
    private String type;

    @NotNull
    private String name;

    @NotNull
    private Long ownerUserLoginSeq;
}
