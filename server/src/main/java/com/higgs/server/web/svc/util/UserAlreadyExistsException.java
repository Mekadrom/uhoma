package com.higgs.server.web.svc.util;

import java.io.Serial;

public class UserAlreadyExistsException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserAlreadyExistsException(final String username) {
        super("User '" + username + "' already exists");
    }
}
