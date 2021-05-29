package com.higgs.server.db.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ROOM")
public class Room {
    @Id
    @NotNull
    @GeneratedValue(generator = "SQ_ROOM")
    @SequenceGenerator(name = "SQ_ROOM")
    @Column(name = "ROOM_SEQ")
    private Long roomSeq;

    @NotNull
    @Column(name = "NAME", unique = true)
    private String name;
}
