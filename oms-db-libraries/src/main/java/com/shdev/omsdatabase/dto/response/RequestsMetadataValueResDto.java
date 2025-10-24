package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;

import java.io.Serializable;

/**
 * DTO for {@link RequestsMetadataValueEntity}
 *
 * @author Shailesh Halor
 */
public record RequestsMetadataValueResDto(
        Long id,
        DocumentRequestResDto omdrt,
        ReferenceDataResDto omrda,
        String metadataValue) implements Serializable {
}
