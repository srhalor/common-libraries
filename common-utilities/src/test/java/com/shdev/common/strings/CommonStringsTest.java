package com.shdev.common.strings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CommonStrings} utility class verifying string validation
 * and sanitization operations.
 */
@DisplayName("CommonStrings utility unit tests")
class CommonStringsTest {

    /**
     * Test: isNullOrBlank returns true for empty string
     * Given: Empty string input
     * When: isNullOrBlank is called
     * Then: Returns true
     */
    @Test
    @DisplayName("isNullOrBlank: returns true for empty string")
    void isNullOrBlank_handlesNull() {
        assertThat(CommonStrings.isNullOrBlank("")).isTrue();
    }

    /**
     * Test: isNullOrBlank returns true for whitespace-only string
     * Given: String containing only whitespace characters
     * When: isNullOrBlank is called
     * Then: Returns true
     */
    @Test
    @DisplayName("isNullOrBlank: returns true for whitespace-only string")
    void isNullOrBlank_handlesWhitespace() {
        assertThat(CommonStrings.isNullOrBlank("   ")).isTrue();
    }

    /**
     * Test: isNullOrBlank returns false for non-blank string
     * Given: String containing non-whitespace characters
     * When: isNullOrBlank is called
     * Then: Returns false
     */
    @Test
    @DisplayName("isNullOrBlank: returns false for non-blank string")
    void isNullOrBlank_handlesNonBlank() {
        assertThat(CommonStrings.isNullOrBlank("  world  ")).isFalse();
    }

    /**
     * Test: sanitizeString returns null for whitespace-only input
     * Given: String containing only whitespace
     * When: sanitizeString is called
     * Then: Returns null
     */
    @Test
    @DisplayName("sanitizeString: returns null for whitespace-only input")
    void sanitizeString_handlesNull() {
        assertThat(CommonStrings.sanitizeString("   ")).isNull();
    }

    /**
     * Test: sanitizeString trims whitespace from non-blank string
     * Given: String with leading and trailing whitespace
     * When: sanitizeString is called
     * Then: Returns trimmed string without whitespace
     */
    @Test
    @DisplayName("sanitizeString: trims whitespace from non-blank string")
    void sanitizeString_handlesNonBlank() {
        assertThat(CommonStrings.sanitizeString("  hello  ")).isEqualTo("hello");
    }

}
