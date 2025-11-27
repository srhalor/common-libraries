package com.shdev.omsdatabase.entity;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BooleanToStringConverterTest {

    private final BooleanToStringConverter converter = new BooleanToStringConverter();

    @Test
    void convertsTrueToY() {
        assertThat(converter.convertToDatabaseColumn(Boolean.TRUE)).isEqualTo("Y");
    }

    @Test
    void convertsFalseToN() {
        assertThat(converter.convertToDatabaseColumn(Boolean.FALSE)).isEqualTo("N");
    }

    @Test
    void convertsNullToNDefault() {
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("N");
    }

    @Test
    void convertsYToTrue() {
        assertThat(converter.convertToEntityAttribute("Y")).isTrue();
    }

    @Test
    void convertsNToFalse() {
        assertThat(converter.convertToEntityAttribute("N")).isFalse();
    }

    @Test
    void convertsNullToFalseDefault() {
        assertThat(converter.convertToEntityAttribute(null)).isFalse();
    }
}

