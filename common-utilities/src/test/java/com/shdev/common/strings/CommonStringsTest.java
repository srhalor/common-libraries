package com.shdev.common.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonStringsTest {

    @Test
    void isNullOrBlank_handlesNull() {
        assertTrue(CommonStrings.isNullOrBlank(null));
    }

    @Test
    void isNullOrBlank_handlesWhitespace() {
        assertTrue(CommonStrings.isNullOrBlank("   \t\n "));
    }

    @Test
    void isNullOrBlank_handlesNonBlank() {
        assertFalse(CommonStrings.isNullOrBlank("hello"));
    }
}

