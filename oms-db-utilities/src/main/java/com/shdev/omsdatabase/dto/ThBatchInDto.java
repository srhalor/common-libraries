package com.shdev.omsdatabase.dto;

import com.shdev.omsdatabase.constants.LengthConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * TH Batch Input DTO record to transfer TH batch data from client to service layer.
 *
 * @param requestId     the identifier of the associated request
 * @param batchId       the identifier of the batch
 * @param batchStatusId the identifier of the batch status reference data
 * @param batchName     the name of the batch
 * @param dmsDocumentId the identifier of the DMS document
 * @param syncStatus    the synchronization status of the batch
 * @param eventStatus   the event status of the batch
 * @param retryCount    the retry count for processing the batch
 *
 * @author Shailesh Halor
 */
public record ThBatchInDto(
        @NotNull Long requestId,
        @NotNull Long batchId,
        @NotNull Long batchStatusId,
        @NotNull @Size(max = LengthConstants.BATCH_NAME) String batchName,
        Long dmsDocumentId,
        Boolean syncStatus,
        Boolean eventStatus,
        Long retryCount
) {
}
