package com.shdev.omsdatabase.dto;

/**
 * Metadata Value DTO record to transfer metadata refDataValue data out of the service layer.
 *
 * @param id            the identifier of the metadata value
 * @param requestId     the identifier of the associated request
 * @param metadataKey   the metadata key reference data
 * @param metadataValue the refDataValue of the metadata
 *
 * @author Shailesh Halor
 */
public record MetadataValueOutDto(
        Long id,
        Long requestId,
        ReferenceDataLiteDto metadataKey,
        String metadataValue
) {
}
