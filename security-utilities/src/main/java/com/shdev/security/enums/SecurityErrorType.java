package com.shdev.security.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Enumeration of security-related error types with their HTTP status codes and messages.
 * Provides a centralized way to handle different security errors consistently.
 *
 * @author Shailesh Halor
 */
@Getter
@RequiredArgsConstructor
public enum SecurityErrorType {

    // Authentication Errors (401)
    MISSING_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "missing_token",
            "Authentication required. Please provide a valid JWT token."
    ),
    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "invalid_token",
            "The provided token is invalid or malformed."
    ),
    EXPIRED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "expired_token",
            "The provided token has expired."
    ),
    TOKEN_VALIDATION_FAILED(
            HttpStatus.UNAUTHORIZED,
            "token_validation_failed",
            "Token validation with security service failed."
    ),

    // Authorization Errors (403)
    ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "access_denied",
            "Access denied. You don't have the required permissions to access this resource."
    ),
    INSUFFICIENT_SCOPE(
            HttpStatus.FORBIDDEN,
            "insufficient_scope",
            "The token does not have sufficient scope to access this resource."
    ),
    INSUFFICIENT_ROLE(
            HttpStatus.FORBIDDEN,
            "insufficient_role",
            "You don't have the required role to access this resource."
    ),

    // Header Validation Errors (400)
    MISSING_ORIGIN_HEADERS(
            HttpStatus.BAD_REQUEST,
            "missing_origin_headers",
            "Missing required origin headers: Atradius-Origin-Service, Atradius-Origin-Application, Atradius-Origin-User"
    ),
    INVALID_HEADER_FORMAT(
            HttpStatus.BAD_REQUEST,
            "invalid_header_format",
            "One or more request headers have an invalid format."
    ),

    // Service Errors (500/503)
    SECURITY_SERVICE_UNAVAILABLE(
            HttpStatus.SERVICE_UNAVAILABLE,
            "security_service_unavailable",
            "Security service is currently unavailable. Please try again later."
    ),
    INTERNAL_SECURITY_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "internal_security_error",
            "An internal security error occurred."
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    /**
     * Get SecurityErrorType by error code.
     *
     * @param errorCode the error code to search for
     * @return the matching SecurityErrorType, or null if not found
     */
    public static SecurityErrorType fromErrorCode(String errorCode) {
        for (SecurityErrorType type : values()) {
            if (type.errorCode.equals(errorCode)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Get SecurityErrorType by HTTP status.
     *
     * @param status the HTTP status
     * @return the first matching SecurityErrorType, or INTERNAL_SECURITY_ERROR if not found
     */
    public static SecurityErrorType fromHttpStatus(HttpStatus status) {
        for (SecurityErrorType type : values()) {
            if (type.httpStatus == status) {
                return type;
            }
        }
        return INTERNAL_SECURITY_ERROR;
    }
}

