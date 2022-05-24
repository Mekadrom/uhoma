package com.higgs.server.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;

@Data
public class ActionDto {
    @NotNull
    private Long actionSeq;

    @NotNull
    private Long ownerNodeSeq;

    @NotNull
    private String name;

    private ActionHandlerDto actionHandler;

    private Collection<ActionParameterDto> parameters;
}
