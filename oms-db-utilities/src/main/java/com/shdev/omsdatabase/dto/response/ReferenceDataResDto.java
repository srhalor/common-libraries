package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.ReferenceDataEntity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for {@link ReferenceDataEntity}
 *
 * @author Shailesh Halor
 */
public record ReferenceDataResDto(
        Long id,
        String refDataType,
        String refDataName,
        String description,
        LocalDate effectFromDat,
        LocalDate effectToDat,
        Instant createdDat,
        Instant lastUpdateDat,
        String createUid,
        String lastUpdateUid) implements Serializable {
}
