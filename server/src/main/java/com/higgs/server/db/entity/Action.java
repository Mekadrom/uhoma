package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACTION")
public class Action {
    @Id
    @NotNull
    @Column(name = "ACTION_SEQ")
    @SequenceGenerator(name = "SQ_ACTION")
    @GeneratedValue(generator = "SQ_ACTION", strategy = GenerationType.IDENTITY)
    private Long actionSeq;

    @NotNull
    @Column(name = "OWNER_NODE_SEQ")
    private Long ownerNodeSeq;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "ACTION_HANDLER_SEQ")
    private ActionHandler actionHandler;

    @OneToMany
    @JsonManagedReference
    @JoinColumn(name = "ACTION_SEQ")
    private Collection<ActionParameter> parameters;
}
