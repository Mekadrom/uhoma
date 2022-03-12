package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.higgs.server.db.entity.Home;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActionParameterTypeDto {
    @NotNull
    private Long actionParameterTypeSeq;

    private String name;

    private String typeDef;

    @JsonIgnore
    private Home home;
}
