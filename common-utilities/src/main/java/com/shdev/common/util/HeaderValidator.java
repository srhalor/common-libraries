package com.shdev.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for validating HTTP headers.
 *
 * @author Shailesh Halor
 */
@Slf4j
public final class HeaderValidator {

    private HeaderValidator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validate that all required headers are present and not empty.
     *
     * @param headerName  the header name
     * @param headerValue the header value
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String headerName, String headerValue) {
        boolean valid = StringUtils.hasText(headerValue);
        if (!valid) {
            log.debug("Header validation failed: {} is missing or empty", headerName);
        }
        return valid;
    }

    /**
     * Validate multiple required headers.
     *
     * @param headers map of header names to values
     * @return list of missing header names (empty if all valid)
     */
    public static List<String> validateRequired(java.util.Map<String, String> headers) {
        List<String> missing = new ArrayList<>();
        headers.forEach((name, value) -> {
            if (!StringUtils.hasText(value)) {
                missing.add(name);
            }
        });
        return missing;
    }

    /**
     * Extract Bearer token from Authorization header.
     *
     * @param authorizationHeader the Authorization header value
     * @return the token without "Bearer " prefix, or null if invalid
     */
    public static String extractBearerToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length()).trim();
            return StringUtils.hasText(token) ? token : null;
        }
        return null;
    }

    /**
     * Extract Basic Auth credentials from Authorization header.
     *
     * @param authorizationHeader the Authorization header value
     * @return the Base64 encoded credentials without "Basic " prefix, or null if invalid
     */
    public static String extractBasicAuth(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Basic ")) {
            String credentials = authorizationHeader.substring("Basic ".length()).trim();
            return StringUtils.hasText(credentials) ? credentials : null;
        }
        return null;
    }
}

