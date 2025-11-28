package com.shdev.omsdatabase.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Document Request Input DTO record to transfer document request data from client to service layer.
 *
 * @param sourceSystemId the identifier of the source system reference data
 * @param documentTypeId the identifier of the document type reference data
 * @param documentNameId the identifier of the document name reference data
 * @param docStatusId    the identifier of the document status reference data
 *
 * @author Shailesh Halor
 */
public record DocumentRequestInDto(
        @NotNull Long sourceSystemId,
        @NotNull Long documentTypeId,
        @NotNull Long documentNameId,
        @NotNull Long docStatusId
) {
}
