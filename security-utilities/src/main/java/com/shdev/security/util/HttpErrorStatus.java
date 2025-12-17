package com.shdev.security.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing common HTTP error status codes with user-friendly messages.
 * Can be reused across the application for consistent error messaging.
 *
 * @author Shailesh Halor
 */
@Getter
@RequiredArgsConstructor
public enum HttpErrorStatus {

    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Invalid or expired token"),
    FORBIDDEN(403, "Access forbidden"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    SERVICE_UNAVAILABLE(503, "Service unavailable"),
    UNKNOWN(0, "Request failed");

    private final int statusCode;
    private final String message;

    /**
     * Get HttpErrorStatus by status code.
     *
     * @param statusCode the HTTP status code
     * @return corresponding HttpErrorStatus or UNKNOWN if not found
     */
    public static HttpErrorStatus fromStatusCode(int statusCode) {
        for (HttpErrorStatus status : values()) {
            if (status.statusCode == statusCode) {
                return status;
            }
        }
        return UNKNOWN;
    }

    /**
     * Get user-friendly message for a status code.
     *
     * @param statusCode the HTTP status code
     * @return user-friendly error message
     */
    public static String getMessageForStatusCode(int statusCode) {
        return fromStatusCode(statusCode).getMessage();
    }
}

