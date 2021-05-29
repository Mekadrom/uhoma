package com.higgs.server.db.entity;

import com.higgs.server.db.util.ClassToClasspathJpaConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
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
    private Node ownerNode;

    @NotNull
    @Convert(converter = ClassToClasspathJpaConverter.class)
    @Column(name = "HANDLER")
    private Class<?/* extends ActionHandler*/> handler; // TODO: import Node jar and change to Class<? extends ActionHandler>

    @OneToMany
    @JoinColumn(name = "ACTION_SEQ")
    private Collection<ActionParameter> parameters;
}
