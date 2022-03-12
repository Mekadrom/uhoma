package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.validation.constraints.NotNull;
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

    @JsonManagedReference
    private Collection<ActionParameterDto> parameters;
}
