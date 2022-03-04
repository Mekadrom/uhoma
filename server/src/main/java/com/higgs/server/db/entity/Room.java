package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Optional;

@Data
@Entity
@Table(name = "ROOM")
public class Room implements DtoFilter {
    @Id
    @NotNull
    @Column(name = "ROOM_SEQ")
    @GeneratedValue(generator = "SQ_ROOM", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SQ_ROOM", sequenceName = "SQ_ROOM", allocationSize = 1)
    private Long roomSeq;

    @NotNull
    @Column(name = "NAME", unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "HOME_SEQ")
    private Home home;

    @Override
    public Long getHomeSeq() {
        return Optional.ofNullable(this.getHome()).map(Home::getHomeSeq).orElse(null);
    }
}
