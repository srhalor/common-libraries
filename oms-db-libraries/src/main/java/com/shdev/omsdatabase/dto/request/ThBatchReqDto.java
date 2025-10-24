package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.ThBatchEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link ThBatchEntity}
 *
 * @author Shailesh Halor
 */
public record ThBatchReqDto(
        // id removed; should be taken from path for PUT/PATCH
        @NotNull
        Long omdrtId,
        @NotNull
        Long thBatchId,
        @NotNull
        Long omrdaThStatusId,
        @NotNull
        @Size(max = 100)
        String batchName,
        Long dmsDocumentId,
        @NotNull
        Boolean syncStatus,
        @NotNull
        Boolean eventStatus,
        @NotNull
        Long retryCount) implements Serializable {
}
