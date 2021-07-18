package com.higgs.server.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Data
@Entity
@Table(name = "ACCOUNT")
public class Account {
    @Id
    @NotNull
    @Column(name = "ACCOUNT_SEQ")
    @SequenceGenerator(name = "SQ_ACCOUNT")
    @GeneratedValue(generator = "SQ_ACCOUNT", strategy = GenerationType.IDENTITY)
    private Long accountSeq;

    @NotNull
    @Column(name = "CREATED")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date created;

    @NotNull
    @OneToMany
    @JsonManagedReference
    @JoinColumn(name = "USER_LOGIN_SEQ")
    private Collection<UserLogin> userLogin;
}
