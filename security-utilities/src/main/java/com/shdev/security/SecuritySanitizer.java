package com.shdev.security;

import com.shdev.common.strings.CommonStrings;

public final class SecuritySanitizer {
    private SecuritySanitizer() {}

    /**
     * Sanitizes a username; returns null if input is null/blank, otherwise a trimmed value.
     */
    public static String sanitizeUsername(String input) {
        if (CommonStrings.isNullOrBlank(input)) {
            return null;
        }
        return input.trim();
    }
}

