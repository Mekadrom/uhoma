package com.higgs.server.scv;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckType {
    PRE_INITIALIZE(10),
    POST_INITIALIZE(20),
    HEALTH_CHECK(30),
    PRE_DESTROY(40),
    POST_DESTROY(50);

    private final int exitCode;
}