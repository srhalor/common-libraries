package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.ErrorDetailEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link ErrorDetailEntity}
 *
 * @author Shailesh Halor
 */
public record ErrorDetailReqDto(
        @NotNull
        Long omtbeId,
        @NotNull
        @Size(max = 100)
        String errorCategory,
        @NotNull
        String errorDescription) implements Serializable {
}
