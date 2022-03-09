package com.higgs.server.scv;

/**
 * Marker class for RuntimeExceptions that are thrown from SCV code.
 */
public class CheckFailureException extends RuntimeException {
    public CheckFailureException(final String message) {
        super(message);
    }
}
