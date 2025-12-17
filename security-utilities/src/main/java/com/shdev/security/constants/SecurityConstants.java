package com.shdev.security.constants;

/**
 * Security-related constants including roles, prefixes, and error messages.
 *
 * @author Shailesh Halor
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    // Role-related constants
    /**
     * Default role assigned when no roles are found in JWT token.
     */
    public static final String DEFAULT_ROLE = "USER";

    /**
     * Prefix for Spring Security role authorities.
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * Prefix for OAuth2 scope authorities.
     */
    public static final String SCOPE_PREFIX = "SCOPE_";

    /**
     * Delimiter used in userRole field from security-service.
     * Example: "ADMIN:USER:MANAGER"
     */
    public static final String ROLE_DELIMITER = ":";

    // Error message constants
    /**
     * Error message for missing JWT token.
     */
    public static final String ERROR_MISSING_TOKEN = "Authentication required. Please provide a valid JWT token.";

    /**
     * Error message for invalid JWT token format.
     */
    public static final String ERROR_INVALID_TOKEN_FORMAT = "Invalid token format. Token must be a valid JWT.";

    /**
     * Error message for missing required origin headers.
     */
    public static final String ERROR_MISSING_ORIGIN_HEADERS =
            "Missing required origin headers: Atradius-Origin-Service, Atradius-Origin-Application, Atradius-Origin-User";

    /**
     * Error message for access denied.
     */
    public static final String ERROR_ACCESS_DENIED =
            "Access denied. You don't have the required permissions to access this resource.";

    /**
     * Error message for authentication required.
     */
    public static final String ERROR_AUTHENTICATION_REQUIRED =
            "Authentication required. Please provide valid credentials.";

    /**
     * Error message for empty security-service response.
     */
    public static final String ERROR_EMPTY_VALIDATION_RESPONSE = "Empty response from security-service";

    /**
     * Error message for token validation failure.
     */
    public static final String ERROR_TOKEN_VALIDATION_FAILED = "Token validation failed";
}

