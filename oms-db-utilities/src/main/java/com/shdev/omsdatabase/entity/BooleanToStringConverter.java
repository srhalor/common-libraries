package com.shdev.omsdatabase.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter to persist Java Boolean values as CHAR(1) ('Y' = true, 'N' = false).
 * Auto-applied to all Boolean attributes. Null booleans are stored as 'N' (treat absent as false).
 */
@Converter(autoApply = true)
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) return "N"; // decide default false for null
        return attribute ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) return Boolean.FALSE; // default
        return "Y".equalsIgnoreCase(dbData);
    }
}

