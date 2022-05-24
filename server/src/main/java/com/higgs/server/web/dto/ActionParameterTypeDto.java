package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActionParameterTypeDto {
    @NotNull
    private Long actionParameterTypeSeq;

    private String name;

    private String typeDef;

    @JsonIgnore
    private HomeDto home;
}
