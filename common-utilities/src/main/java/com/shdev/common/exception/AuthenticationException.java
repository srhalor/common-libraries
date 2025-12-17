package com.shdev.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication or authorization fails.
 *
 * @author Shailesh Halor
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, HttpStatus.UNAUTHORIZED, cause);
    }
}

