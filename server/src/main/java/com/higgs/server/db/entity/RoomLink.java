package com.higgs.server.db.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROOM_LINK")
public class RoomLink {
    @Id
    @NotNull
    @Column(name = "ROOM_LINK_SEQ")
    @GeneratedValue(generator = "SQ_ROOM_LINK", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SQ_ROOM_LINK", sequenceName = "SQ_ROOM_LINK", allocationSize = 1)
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
