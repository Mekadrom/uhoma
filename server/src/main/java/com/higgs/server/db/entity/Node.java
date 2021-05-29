package com.higgs.server.db.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

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
@Table(name = "NODE")
public class Node {
    @Id
    @NotNull
    @GeneratedValue(generator = "SQ_NODE")
    @SequenceGenerator(name = "SQ_NODE")
    @Column(name = "NODE_SEQ")
    private Long nodeSeq;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "URL")
    private String url;

    @Column(name = "PORT")
    private Integer port;

    @NotNull
    @ColumnDefault("'/rest/api'")
    @Column(name = "ENDPOINT")
    private String endpoint;

    @ManyToOne
    @JoinColumn(name = "ROOM_SEQ")
    private Room room;

    @OneToMany
    @JoinColumn(name = "OWNER_NODE_SEQ")
    private Collection<Action> publicActions;
}
