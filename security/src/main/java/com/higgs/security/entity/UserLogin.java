package com.higgs.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_LOGIN")
public class UserLogin implements UserDetails {
    @Serial
    private static final long serialVersionUID = 236L;

    @PrePersist
    public void onInsert() {
        this.setCreated(Date.from(OffsetDateTime.now().toInstant()));
    }

    @Id
    @NotNull
    @Column(name = "USER_LOGIN_SEQ")
    @SequenceGenerator(name = "SQ_USER_LOGIN")
    @GeneratedValue(generator = "SQ_USER_LOGIN", strategy = GenerationType.IDENTITY)
    private Long userLoginSeq;

    @NotNull
    private String username;

    @NotNull
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @Column(name = "IS_LOCKED")
    private boolean isLocked;

    @Column(name = "IS_ENABLED")
    private boolean isEnabled;

    @Column(name = "IS_EXPIRED")
    private boolean isExpired;

    @Column(name = "IS_CREDENTIALS_EXPIRED")
    private boolean isCredentialsExpired;

    @Column(name = "ROLES")
    private Set<String> roles;

    @Column(name = "LAST_LOGIN")
    private Date lastLogin;

    @NotNull
    @Column(name = "CREATED")
    private Date created;

    public UserLogin addRole(@NonNull final String role) {
        this.setRoles(Optional.ofNullable(this.getRoles()).orElseGet(() -> new HashSet<>(Collections.singleton(role))));
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .filter(it -> it.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isCredentialsExpired();
    }
}
