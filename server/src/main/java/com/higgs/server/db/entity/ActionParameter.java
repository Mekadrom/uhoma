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
    @Column(name = "ACTION_PARAMETER_SEQ")
    @SequenceGenerator(name = "SQ_ACTION_PARAMETER")
    @GeneratedValue(generator = "SQ_ACTION_PARAMETER", strategy = GenerationType.IDENTITY)
    private Long actionParameterSeq;

    @Column(name = "NAME")
    private String name;

    @OneToOne
    @JoinColumn(name = "ACTION_SEQ")
    @JsonBackReference
    private Action action;

    @ManyToOne
    @JoinColumn(name = "ACTION_PARAMETER_TYPE_SEQ")
    private ActionParameterType actionParameterType;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
}
