package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.DocumentRequestEntity;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link DocumentRequestEntity}
 *
 * @author Shailesh Halor
 */
public record DocumentRequestReqDto(
        @NotNull
        Long omrdaSourceSystemId,
        @NotNull
        Long omrdaDocumentTypeId,
        @NotNull
        Long omrdaDocumentNameId,
        @NotNull
        Long omrdaDocStatusId) implements Serializable {
}
