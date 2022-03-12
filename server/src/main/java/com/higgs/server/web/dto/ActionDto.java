package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
