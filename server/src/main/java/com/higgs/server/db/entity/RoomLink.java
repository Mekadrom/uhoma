package com.higgs.server.db.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ROOM_LINK")
public class RoomLink {
    @Id
    @NotNull
    @GeneratedValue(generator = "SQ_ROOM_LINK")
    @SequenceGenerator(name = "SQ_ROOM_LINK")
    @Column(name = "ROOM_LINK_SEQ")
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
