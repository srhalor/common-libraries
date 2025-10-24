package com.shdev.common.strings;

public final class CommonStrings {
    private CommonStrings() {
    }

    /**
     * Returns true if the input is null or is all whitespace.
     */
    public static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

