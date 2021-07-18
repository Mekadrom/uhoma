package com.higgs.server.db.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ACTION_PARAMETER_TYPE")
public class ActionParameterType {
    @Id
    @NotNull
    @Column(name = "ACTION_PARAMETER_TYPE_SEQ")
    @SequenceGenerator(name = "SQ_ACTION_PARAMETER_TYPE")
    @GeneratedValue(generator = "SQ_ACTION_PARAMETER_TYPE", strategy = GenerationType.IDENTITY)
    private Long actionParameterTypeSeq;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "TYPE_DEF")
    private String typeDef;
}
