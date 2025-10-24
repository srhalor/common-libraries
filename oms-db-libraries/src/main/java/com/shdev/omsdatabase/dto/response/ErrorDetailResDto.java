package com.shdev.omsdatabase.dto.response;

import com.shdev.omsdatabase.entity.ErrorDetailEntity;

import java.io.Serializable;

/**
 * DTO for {@link ErrorDetailEntity}
 *
 * @author Shailesh Halor
 */
public record ErrorDetailResDto(
        Long id,
        ThBatchResDto omtbe,
        String errorCategory,
        String errorDescription) implements Serializable {
}
