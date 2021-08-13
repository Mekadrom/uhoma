package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACTION_HANDLER")
public class ActionHandler {
    @Id
    @NotNull
    @Column(name = "ACTION_HANDLER_SEQ")
    @SequenceGenerator(name = "SQ_ACTION_HANDLER")
    @GeneratedValue(generator = "SQ_ACTION_HANDLER", strategy = GenerationType.IDENTITY)
    private Long actionHandlerSeq;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @Column(name = "HANDLER_DEF")
    private String definition;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "ACCOUNT_SEQ")
    private Account account;
}
