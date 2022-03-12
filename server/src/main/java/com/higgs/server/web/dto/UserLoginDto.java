package com.higgs.server.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.higgs.server.security.Role;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Data
public class UserLoginDto {
    private static final long serialVersionUID = 236L;

    @NotNull
    private Long userLoginSeq;

    @NotNull
    private String username;

    @NotNull
    @JsonIgnore
    @ToString.Exclude
    private String password;

    private boolean isLocked;

    private boolean isEnabled;

    private boolean isExpired;

    private boolean isCredentialsExpired;

    private Set<Role> roles;

    private Date lastLogin;

    @NotNull
    private Date created;
}
