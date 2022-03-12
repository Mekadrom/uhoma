package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.higgs.server.db.entity.Home;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActionHandlerDto {
    @NotNull
    private Long actionHandlerSeq;

    @NotNull
    private String name;

    private String definition;

    @JsonIgnore
    private Home home;
}
