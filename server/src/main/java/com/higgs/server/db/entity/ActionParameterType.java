package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACTION_PARAMETER_TYPE")
public class ActionParameterType implements DtoFilter {
    @Id
    @NotNull
    @Column(name = "ACTION_PARAMETER_TYPE_SEQ")
    @GeneratedValue(generator = "SQ_ACTION_PARAMETER_TYPE", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SQ_ACTION_PARAMETER_TYPE", sequenceName = "SQ_ACTION_PARAMETER_TYPE", allocationSize = 1)
    private Long actionParameterTypeSeq;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "TYPE_DEF")
    private String typeDef;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "HOME_SEQ")
    private Home home;

    @Override
    public Long getHomeSeq() {
        return Optional.ofNullable(this.getHome()).map(Home::getHomeSeq).orElse(null);
    }
}
