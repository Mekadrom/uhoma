package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ACTION_PARAMETER")
public class ActionParameter {
    @Id
    @NotNull
    @GeneratedValue(generator = "SQ_ACTION_PARAMETER")
    @SequenceGenerator(name = "SQ_ACTION_PARAMETER")
    @Column(name = "ACTION_PARAMETER_SEQ")
    private Long actionParameterSeq;

    @Column(name = "NAME")
    private String name;

    @OneToOne
    @JoinColumn(name = "ACTION_SEQ")
    @JsonBackReference
    private Action action;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
}
