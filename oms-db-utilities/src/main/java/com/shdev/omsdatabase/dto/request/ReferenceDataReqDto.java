package com.shdev.omsdatabase.dto.request;

import com.shdev.omsdatabase.entity.ReferenceDataEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link ReferenceDataEntity}
 *
 * @author Shailesh Halor
 */
public record ReferenceDataReqDto(
        @NotNull
        @Size(max = 50)
        String refDataType,
        @NotNull
        @Size(max = 100)
        String refDataName,
        @Size(max = 255)
        String description,
        @NotNull
        LocalDate effectFromDat,
        @NotNull
        LocalDate effectToDat) implements Serializable {
}
