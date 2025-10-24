package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.DocumentRequestEntity;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link DocumentRequestEntity}
 *
 * @author Shailesh Halor
 */
public record DocumentRequestResDto(
        Long id,
        ReferenceDataResDto omrdaSourceSystem,
        ReferenceDataResDto omrdaDocumentType,
        ReferenceDataResDto omrdaDocumentName,
        ReferenceDataResDto omrdaDocStatus,
        Instant createdDat,
        Instant lastUpdateDat,
        String createUidHeader,
        String createUidToken) implements Serializable {
}
