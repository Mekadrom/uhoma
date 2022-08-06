package com.higgs.simulator.httpsim.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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

    @NotNull
    @Column(name = "PROFILE_SEQ", nullable = false)
    private Long profileSeq;

    @NotNull
    @Column(name = "ENDPOINT", nullable = false)
    private String endpoint;
}
