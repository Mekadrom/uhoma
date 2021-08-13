package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NODE")
public class Node {
    @Id
    @NotNull
    @Column(name = "NODE_SEQ")
    @SequenceGenerator(name = "SQ_NODE")
    @GeneratedValue(generator = "SQ_NODE", strategy = GenerationType.IDENTITY)
    private Long nodeSeq;

    @Column(name = "NAME", unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ROOM_SEQ")
    private Room room;

    @JsonManagedReference
    @JoinColumn(name = "OWNER_NODE_SEQ")
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<Action> actions;

    /**
     * Don't let lombok generate this in order to avoid stack overflow errors when the entity is saved.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
