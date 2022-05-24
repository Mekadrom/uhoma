package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActionHandlerDto {
    @NotNull
    private Long actionHandlerSeq;

    @NotNull
    private String name;

    private String definition;

    @JsonIgnore
    private HomeDto home;
}
