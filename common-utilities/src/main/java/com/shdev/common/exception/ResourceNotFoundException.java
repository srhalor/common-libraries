package com.shdev.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 *
 * @author Shailesh Halor
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.NOT_FOUND, cause);
    }
}

