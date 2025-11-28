package com.shdev.omsdatabase.dto;

/**
 * Reference Data Lite DTO record to transfer lightweight reference data between service and client layers.
 *
 * @param id           the identifier of the reference data
 * @param refDataValue the refDataValue of the reference data
 * @param description  the description of the reference data
 *
 * @author Shailesh Halor
 */
public record ReferenceDataLiteDto(
        Long id,
        String refDataValue,
        String description
) {
}
