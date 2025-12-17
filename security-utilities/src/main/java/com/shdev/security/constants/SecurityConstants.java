package com.shdev.security.constants;

/**
 * Security-related constants.
 *
 * @author Shailesh Halor
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

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
}

