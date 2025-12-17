package com.shdev.security.exception;

/**
 * Exception thrown when JWT token validation fails.
 * This can occur when:
 * - Token is invalid or malformed
 * - Token has expired
 * - Security-service validation endpoint is unreachable
 * - Token validation response is empty or invalid
 *
 * @author Shailesh Halor
 */
public class TokenValidationException extends RuntimeException {

    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

