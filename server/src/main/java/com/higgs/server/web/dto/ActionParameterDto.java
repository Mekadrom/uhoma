package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.persistence.Transient;

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
