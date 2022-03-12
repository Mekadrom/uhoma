package com.higgs.server.scv;

/**
 * Marker class for RuntimeExceptions that are thrown from SCV code.
 */
public class CheckFailureException extends RuntimeException {
    public static final long serialVersionUID = 1L;

    public CheckFailureException(final String message) {
        super(message);
    }
}
