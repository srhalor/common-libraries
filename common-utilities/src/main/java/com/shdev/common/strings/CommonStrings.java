package com.shdev.common.strings;

import lombok.experimental.UtilityClass;

/**
 * A utility class for common string operations.
 */
@UtilityClass
public final class CommonStrings {

    /**
     * Checks if a string is null or blank (empty or only whitespace).
     *
     * @param s the string to check
     * @return true if the string is null or blank, false otherwise
     */
    public static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Sanitizes a string; returns null if input is null/blank, otherwise a trimmed value.
     *
     * @param input the string to sanitize
     * @return sanitized string or null
     */
    public static String sanitizeString(String input) {
        if (CommonStrings.isNullOrBlank(input)) {
            return null;
        }
        return input.trim();
    }

}
