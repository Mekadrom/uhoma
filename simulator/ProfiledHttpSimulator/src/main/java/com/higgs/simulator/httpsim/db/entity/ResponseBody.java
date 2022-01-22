package com.higgs.simulator.httpsim.db.entity;

import com.higgs.simulator.httpsim.db.converter.JpaJsonToMapConverter;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Entity
@Table(name = "RESPONSE_BODY")
public class ResponseBody {
    @Id
    @NotNull
    @Column(name = "RESPONSE_BODY_SEQ")
    @SequenceGenerator(name = "SQ_RESPONSE_BODY")
    @GeneratedValue(generator = "SQ_RESPONSE_BODY", strategy = GenerationType.IDENTITY)
    private Long responseBodySeq;

    @Column(name = "HEADERS")
    @Convert(converter = JpaJsonToMapConverter.class)
    private Map<String, Object> headers;

    @Column(name = "RESPONSE_CODE")
    private int responseCode;

    @Nullable
    @Column(name = "BODY")
    private String body;

    @NotNull
    @Column(name = "ENDPOINT")
    private String endpoint;
}
