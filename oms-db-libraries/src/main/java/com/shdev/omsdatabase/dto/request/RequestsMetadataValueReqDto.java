package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.RequestsMetadataValueEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link RequestsMetadataValueEntity}
 *
 * @author Shailesh Halor
 */
public record RequestsMetadataValueReqDto(
        // id removed; should be taken from path for PUT/PATCH
        @NotNull
        Long omdrtId,
        @NotNull
        Long omrdaId,
        @NotNull
        @Size(max = 255)
        String metadataValue) implements Serializable {
}
