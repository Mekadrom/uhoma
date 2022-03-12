package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.higgs.server.db.entity.ActionParameterType;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Data
public class ActionParameterDto {
    @NotNull
    private Long actionParameterSeq;

    private String name;

    private Long actionSeq;

    private ActionParameterType actionParameterType;

    private String defaultValue;

    @Transient
    @JsonInclude
    private String currentValue;
}
