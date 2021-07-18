package com.higgs.server.db.entity;

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
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ROOM_LINK")
public class RoomLink {
    @Id
    @NotNull
    @Column(name = "ROOM_LINK_SEQ")
    @SequenceGenerator(name = "SQ_ROOM_LINK")
    @GeneratedValue(generator = "SQ_ROOM_LINK", strategy = GenerationType.IDENTITY)
    private Long roomLinkSeq;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "START_ROOM_SEQ")
    private Room startRoom;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "END_ROOM_SEQ")
    private Room endRoom;

    @NotNull
    @Column(name = "TRANSITION_LOCATION_DEF")
    private String transitionLocationDef;
}
