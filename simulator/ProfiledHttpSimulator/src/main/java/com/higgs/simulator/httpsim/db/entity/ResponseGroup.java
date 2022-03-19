package com.higgs.simulator.httpsim.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Table(name = "response_group", uniqueConstraints = @UniqueConstraint(columnNames = { "PROFILE_SEQ", "ENDPOINT" }))
public class ResponseGroup {
    @Id
    @NotNull
    @Column(name = "RESPONSE_GROUP_SEQ")
    @SequenceGenerator(name = "SQ_RESPONSE_GROUP", sequenceName = "SQ_RESPONSE_GROUP")
    @GeneratedValue(generator = "SQ_RESPONSE_GROUP", strategy = GenerationType.SEQUENCE)
    private Long responseGroupSeq;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "PROFILE_SEQ", nullable = false)
    private Profile profile;

    @NotNull
    @Column(name = "ENDPOINT", nullable = false)
    private String endpoint;

    @NotNull
    @OneToMany
    @JsonManagedReference
    @JoinColumn(name = "RESPONSE_GROUP_SEQ")
    private List<ResponseBody> responseBodies;
}
