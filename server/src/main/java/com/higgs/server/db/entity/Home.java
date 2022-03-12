package com.higgs.server.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HOME")
public class Home implements DtoFilter {
    public static final String HOME_TYPE_USER = "user";

    @Id
    @NotNull
    @Column(name = "HOME_SEQ")
    @GeneratedValue(generator = "SQ_HOME", strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "SQ_HOME", sequenceName = "SQ_HOME", allocationSize = 1)
    private Long homeSeq;

    @NotNull
    @Column(name = "CREATED")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date created;

    @NotNull
    @Column(name = "TYPE")
    private String type;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "OWNER_USER_LOGIN_SEQ")
    private Long ownerUserLoginSeq;

    @PrePersist
    public void populateCreated() {
        this.setCreated(Date.from(OffsetDateTime.now().toInstant()));
    }
}
