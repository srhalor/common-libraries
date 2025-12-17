package com.shdev.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when validation fails.
 *
 * @author Shailesh Halor
 */
public class ValidationException extends BusinessException {

    public ValidationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.BAD_REQUEST, cause);
    }
}

