package com.higgs.server.web.rest.util;

import com.higgs.server.web.dto.AuthResult;

@FunctionalInterface
public interface AuthSupplier {
    AuthResult supply(String username, String password);
}
