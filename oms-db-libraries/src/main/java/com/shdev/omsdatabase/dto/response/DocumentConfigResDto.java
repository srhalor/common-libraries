package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.DocumentConfigEntity;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link DocumentConfigEntity}
 *
 * @author Shailesh Halor
 */
public record DocumentConfigResDto(
        Long id,
        ReferenceDataResDto omrdaFooter,
        ReferenceDataResDto omrdaAppDocSpec,
        ReferenceDataResDto omrdaCode,
        String configValue,
        Instant createdDat,
        Instant lastUpdateDat,
        String createUid,
        String lastUpdateUid) implements Serializable {
}
