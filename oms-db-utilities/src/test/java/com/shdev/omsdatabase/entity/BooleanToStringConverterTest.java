package com.shdev.omsdatabase.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BooleanToStringConverter} verifying bidirectional
 * conversion between Boolean values and the database representation ("Y"/"N").
 */
@DisplayName("BooleanToStringConverter unit tests")
class BooleanToStringConverterTest {

    private final BooleanToStringConverter converter = new BooleanToStringConverter();

    /**
     * Test: convertToDatabaseColumn converts TRUE to 'Y'
     * Given: Boolean value TRUE
     * When: convertToDatabaseColumn is called
     * Then: Returns string "Y"
     */
    @Test
    @DisplayName("convertToDatabaseColumn: TRUE maps to 'Y'")
    void convertsTrueToY() {
        assertThat(converter.convertToDatabaseColumn(Boolean.TRUE)).isEqualTo("Y");
    }

    /**
     * Test: convertToDatabaseColumn converts FALSE to 'N'
     * Given: Boolean value FALSE
     * When: convertToDatabaseColumn is called
     * Then: Returns string "N"
     */
    @Test
    @DisplayName("convertToDatabaseColumn: FALSE maps to 'N'")
    void convertsFalseToN() {
        assertThat(converter.convertToDatabaseColumn(Boolean.FALSE)).isEqualTo("N");
    }

    /**
     * Test: convertToDatabaseColumn handles null by defaulting to 'N'
     * Given: null Boolean value
     * When: convertToDatabaseColumn is called
     * Then: Returns default string "N"
     */
    @Test
    @DisplayName("convertToDatabaseColumn: null maps to default 'N'")
    void convertsNullToNDefault() {
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("N");
    }

    /**
     * Test: convertToEntityAttribute converts 'Y' to TRUE
     * Given: Database string value "Y"
     * When: convertToEntityAttribute is called
     * Then: Returns Boolean TRUE
     */
    @Test
    @DisplayName("convertToEntityAttribute: 'Y' maps to TRUE")
    void convertsYToTrue() {
        assertThat(converter.convertToEntityAttribute("Y")).isTrue();
    }

    /**
     * Test: convertToEntityAttribute converts 'N' to FALSE
     * Given: Database string value "N"
     * When: convertToEntityAttribute is called
     * Then: Returns Boolean FALSE
     */
    @Test
    @DisplayName("convertToEntityAttribute: 'N' maps to FALSE")
    void convertsNToFalse() {
        assertThat(converter.convertToEntityAttribute("N")).isFalse();
    }

    /**
     * Test: convertToEntityAttribute handles null by defaulting to FALSE
     * Given: null database string value
     * When: convertToEntityAttribute is called
     * Then: Returns default Boolean FALSE
     */
    @Test
    @DisplayName("convertToEntityAttribute: null maps to default FALSE")
    void convertsNullToFalseDefault() {
        assertThat(converter.convertToEntityAttribute(null)).isFalse();
    }
}
