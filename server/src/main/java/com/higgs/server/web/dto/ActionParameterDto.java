package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Data
public class ActionParameterDto {
    @NotNull
    private Long actionParameterSeq;

    private String name;

    private Long actionSeq;

    private ActionParameterTypeDto actionParameterType;

    private String defaultValue;

    @Transient
    @JsonInclude
    private String currentValue;
}
