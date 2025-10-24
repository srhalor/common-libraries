package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.DocumentConfigEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link DocumentConfigEntity}
 *
 * @author Shailesh Halor
 */
public record DocumentConfigReqDto(
        @NotNull
        Long omrdaFooterId,
        @NotNull
        Long omrdaAppDocSpecId,
        @NotNull
        Long omrdaCodeId,
        @NotNull
        @Size(max = 255)
        String value,
        @Size(max = 255)
        String description,
        @NotNull
        LocalDate effectFromDat,
        @NotNull
        LocalDate effectToDat) implements Serializable {
}
