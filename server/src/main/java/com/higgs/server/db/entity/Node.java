package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
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
import java.util.Collection;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NODE")
public class Node implements DtoFilter {
    @Id
    @NotNull
    @Column(name = "NODE_SEQ")
    @GeneratedValue(generator = "SQ_NODE", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SQ_NODE", sequenceName = "SQ_NODE", allocationSize = 1)
    private Long nodeSeq;

    @Column(name = "NAME", unique = true)
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "HOME_SEQ")
    private Home home;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ROOM_SEQ")
    private Room room;

    @JoinColumn(name = "OWNER_NODE_SEQ")
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Action> actions;

    @Override
    @JsonIgnore
    public Long getHomeSeq() {
        return Optional.ofNullable(this.getHome()).map(Home::getHomeSeq).orElse(null);
    }
}
