package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.ThBatchEntity;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ThBatchEntity}
 *
 * @author Shailesh Halor
 */
public record ThBatchResDto(
        Long id,
        Long omdrtId,
        Long thBatchId,
        ReferenceDataResDto omrdaThStatus,
        String batchName,
        Long dmsDocumentId,
        Boolean syncStatus,
        Boolean eventStatus,
        Long retryCount,
        Instant createdDat,
        Instant lastUpdateDat,
        String createUidHeader,
        String createUidToken) implements Serializable {
}
