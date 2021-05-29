package com.higgs.server.db.entity;

import com.higgs.server.db.util.ClassToClasspathJpaConverter;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ACTION_PARAMETER")
public class ActionParameter {
    @Id
    @NotNull
    @GeneratedValue(generator = "SQ_ACTION_PARAMETER")
    @SequenceGenerator(name = "SQ_ACTION_PARAMETER")
    @Column(name = "ACTION_PARAMETER_SEQ")
    private Long actionParameterSeq;

    @OneToOne
    @JoinColumn(name = "ACTION_SEQ")
    private Action action;

    @Convert(converter = ClassToClasspathJpaConverter.class)
    @Column(name = "TYPE")
    private Class<?> type;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
}
