package com.shdev.omsdatabase.dto;

import java.time.OffsetDateTime;

/**
 * TH Batch Output DTO record to transfer TH batch data from service layer to client.
 *
 * @param id              the unique identifier of the TH batch
 * @param requestId       the identifier of the associated request
 * @param batchId         the batch identifier
 * @param batchStatus     the batch status reference data lite DTO
 * @param batchName       the name of the batch
 * @param dmsDocumentId   the DMS document identifier
 * @param syncStatus      the synchronization status
 * @param eventStatus     the event status
 * @param retryCount      the number of retries attempted
 * @param createdDat      the timestamp when the batch was created
 * @param lastUpdateDat   the timestamp when the batch was last updated
 * @param createUidHeader the user identifier from header who created the batch
 * @param createUidToken  the user identifier from token who created the batch
 * @author Shailesh Halor
 */
public record ThBatchOutDto(
        Long id,
        Long requestId,
        Long batchId,
        ReferenceDataLiteDto batchStatus,
        String batchName,
        Long dmsDocumentId,
        Boolean syncStatus,
        Boolean eventStatus,
        Long retryCount,
        OffsetDateTime createdDat,
        OffsetDateTime lastUpdateDat,
        String createUidHeader,
        String createUidToken
) {
}
