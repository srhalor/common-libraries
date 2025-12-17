package com.shdev.security.util;

import java.util.List;

/**
 * Utility class for matching URL paths against patterns.
 * Supports wildcard patterns like /api/**, /health, etc.
 *
 * @author Shailesh Halor
 */
public class PathMatcher {

    private PathMatcher() {
        // Utility class - prevent instantiation
    }

    /**
     * Check if a path matches any of the given patterns.
     *
     * @param path     the request path to check
     * @param patterns list of patterns to match against
     * @return true if path matches any pattern, false otherwise
     */
    public static boolean matches(String path, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        return patterns.stream().anyMatch(pattern -> matchesPattern(path, pattern));
    }

    /**
     * Check if a path matches a specific pattern.
     * Supports:
     * - Exact match: /api/health
     * - Prefix match: /api/health/
     * - Wildcard match: /api/**
     *
     * @param path    the request path
     * @param pattern the pattern to match against
     * @return true if path matches pattern, false otherwise
     */
    public static boolean matchesPattern(String path, String pattern) {
        if (pattern == null || path == null) {
            return false;
        }

        // Handle wildcard pattern: /api/**
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }

        // Exact match or prefix with trailing slash
        return path.equals(pattern) || path.startsWith(pattern + "/");
    }

    /**
     * Check if a path should be excluded based on excluded patterns.
     *
     * @param path           the request path
     * @param excludedPaths  list of patterns to exclude
     * @return true if path should be excluded, false otherwise
     */
    public static boolean isExcluded(String path, List<String> excludedPaths) {
        return matches(path, excludedPaths);
    }
}

