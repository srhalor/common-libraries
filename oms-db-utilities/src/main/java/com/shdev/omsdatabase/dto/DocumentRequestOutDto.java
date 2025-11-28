package com.shdev.omsdatabase.dto;

import java.time.Instant;

/**
 * Document Request Output DTO record to transfer document request data from service layer to client.
 *
 * @param id              the unique identifier of the document request
 * @param sourceSystem    the source system reference data lite DTO
 * @param documentType    the document type reference data lite DTO
 * @param documentName    the document name reference data lite DTO
 * @param documentStatus  the document status reference data lite DTO
 * @param createdDat      the timestamp when the document request was created
 * @param lastUpdateDat   the timestamp when the document request was last updated
 * @param createUidHeader the user identifier from header who created the document request
 * @param createUidToken  the user identifier from token who created the document request
 *
 * @author Shailesh Halor
 */
public record DocumentRequestOutDto(
        Long id,
        ReferenceDataLiteDto sourceSystem,
        ReferenceDataLiteDto documentType,
        ReferenceDataLiteDto documentName,
        ReferenceDataLiteDto documentStatus,
        Instant createdDat,
        Instant lastUpdateDat,
        String createUidHeader,
        String createUidToken
) {
}
