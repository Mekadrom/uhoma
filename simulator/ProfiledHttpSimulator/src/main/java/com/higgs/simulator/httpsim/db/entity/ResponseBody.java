package com.higgs.simulator.httpsim.db.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.higgs.simulator.httpsim.db.converter.JsonMapConverter;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Map;

@Data
@Entity
@Table(name = "RESPONSE_BODY")
public class ResponseBody {
    @Id
    @NotNull
    @SequenceGenerator(name = "SQ_RESPONSE_BODY")
    @Column(name = "RESPONSE_BODY_SEQ", nullable = false)
    @GeneratedValue(generator = "SQ_RESPONSE_BODY", strategy = GenerationType.SEQUENCE)
    private Long responseBodySeq;

    @Column(name = "HEADERS", length = 4000)
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> headers;

    @Column(name = "KEYED_FIELD_VALUES", length = 4000)
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> keyedFieldValues;

    @NotNull
    @Column(name = "RESPONSE_CODE", nullable = false)
    private int responseCode;

    @Nullable
    @Column(name = "BODY")
    private String body;

    @NotNull
    @Column(name = "RESPONSE_GROUP_SEQ", nullable = false)
    private Long responseGroupSeq;
}
