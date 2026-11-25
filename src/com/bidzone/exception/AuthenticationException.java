package com.bidzone.exception;

/**
 * Exception thrown when authentication fails, credentials are invalid,
 * or an unauthorized user attempts an operation.
 */
public class AuthenticationException extends BidZoneException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
