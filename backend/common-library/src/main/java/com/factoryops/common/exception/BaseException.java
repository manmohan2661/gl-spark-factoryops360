package com.factoryops.common.exception;

/**
 * Root of the shared exception hierarchy. Domain-specific exceptions in each
 * microservice may extend this (or one of its subclasses below) to keep
 * error handling consistent across the platform.
 */
public class BaseException extends RuntimeException {

    private final Integer statusCode;

    public BaseException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public BaseException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
