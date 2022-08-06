package com.higgs.simulator.httpsim.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.higgs.simulator.httpsim.db.converter.SetStringConverter;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
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
import java.util.Set;

@Data
@Entity
@Table(name = "PROFILE", uniqueConstraints = @UniqueConstraint(columnNames = { "ENDPOINT" }))
public class Profile {
    @Id
    @NotNull
    @Column(name = "PROFILE_SEQ", nullable = false)
    @SequenceGenerator(name = "SQ_RESPONSE_BODY")
    @GeneratedValue(generator = "SQ_RESPONSE_BODY", strategy = GenerationType.SEQUENCE)
    private Long profileSeq;

    @Column(name = "KEYED_FIELDS", length = 4000)
    @Convert(converter = SetStringConverter.class)
    private Set<String> keyedFields;

    @NotNull
    @Column(name = "ENDPOINT", nullable = false)
    private String endpoint;
}
