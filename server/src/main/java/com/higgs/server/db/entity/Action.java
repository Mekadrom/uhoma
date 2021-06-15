package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Entity
@Table(name = "ACTION")
public class Action {
    @Id
    @NotNull
    @GeneratedValue(generator = "SQ_ACTION")
    @SequenceGenerator(name = "SQ_ACTION")
    @Column(name = "ACTION_SEQ")
    private Long actionSeq;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "OWNER_NODE_SEQ")
    @JsonBackReference
    private Node ownerNode;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "HANDLER")
    private String handler;

    @OneToMany
    @JoinColumn(name = "ACTION_SEQ")
    @JsonManagedReference
    private Collection<ActionParameter> parameters;
}
