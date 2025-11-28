package com.shdev.omsdatabase.dto;

import com.shdev.omsdatabase.constants.LengthConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Metadata Value DTO record to transfer metadata refDataValue data into the service layer.
 *
 * @param requestId     the identifier of the associated request
 * @param metadataKeyId the identifier of the metadata key reference data
 * @param metadataValue the refDataValue of the metadata
 *
 * @author Shailesh Halor
 */
public record MetadataValueInDto(
        @NotNull Long requestId,
        @NotNull Long metadataKeyId,
        @NotNull @Size(max = LengthConstants.METADATA_VALUE) String metadataValue
) {
}
