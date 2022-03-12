package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ACTION_PARAMETER")
public class ActionParameter {
    @Id
    @NotNull
    @Column(name = "ACTION_PARAMETER_SEQ")
    @GeneratedValue(generator = "SQ_ACTION_PARAMETER", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SQ_ACTION_PARAMETER", sequenceName = "SQ_ACTION_PARAMETER", allocationSize = 1)
    private Long actionParameterSeq;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ACTION_SEQ")
    private Long actionSeq;

    @ManyToOne
    @JoinColumn(name = "ACTION_PARAMETER_TYPE_SEQ")
    private ActionParameterType actionParameterType;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    @Transient
    @JsonInclude
    private String currentValue;
}
