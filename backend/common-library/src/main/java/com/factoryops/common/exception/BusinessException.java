package com.factoryops.common.exception;

/**
 * Thrown when a request is well-formed but violates a business rule.
 */
public class BusinessException extends BaseException {

    public BusinessException(String message) {
        super(message, 422);
    }
}
