package com.factoryops.common.exception;

/**
 * Thrown when a requested resource cannot be located.
 * Intended to be reused (not re-declared) by every microservice.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message, 404);
    }
}
