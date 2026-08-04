package com.factoryops.common.exception;

/**
 * Thrown when a caller is not permitted to perform the requested operation.
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, 401);
    }
}
