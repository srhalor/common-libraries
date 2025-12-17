package com.shdev.security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Utility class for JWT token operations including extraction and validation.
 *
 * @author Shailesh Halor
 */
@Slf4j
public class JwtTokenUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$");

    private JwtTokenUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Extract Bearer token from Authorization header.
     * Example: "Bearer eyJhbGc..." -> "eyJhbGc..."
     *
     * @param authorizationHeader the Authorization header value
     * @return the extracted token, or null if header is invalid
     */
    public static String extractBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            log.debug("Authorization header is empty or null");
            return null;
        }

        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            log.debug("Authorization header does not start with 'Bearer ' prefix");
            return null;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();

        if (!StringUtils.hasText(token)) {
            log.debug("Token is empty after removing Bearer prefix");
            return null;
        }

        return token;
    }

    /**
     * Check if a string looks like a valid JWT token format.
     * This is a basic format check, not cryptographic validation.
     * A JWT should have three parts separated by dots: header.payload.signature
     *
     * @param token the token string to check
     * @return true if token matches JWT format, false otherwise
     */
    public static boolean hasValidJwtFormat(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        return JWT_PATTERN.matcher(token).matches();
    }

    /**
     * Check if authorization header is present and starts with Bearer prefix.
     *
     * @param authorizationHeader the Authorization header value
     * @return true if header is valid Bearer format, false otherwise
     */
    public static boolean isBearerAuthHeader(String authorizationHeader) {
        return StringUtils.hasText(authorizationHeader) &&
               authorizationHeader.startsWith(BEARER_PREFIX);
    }
}

