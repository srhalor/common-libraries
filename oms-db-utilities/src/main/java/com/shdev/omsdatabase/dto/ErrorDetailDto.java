package com.shdev.omsdatabase.dto;

import com.shdev.omsdatabase.constants.LengthConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Error Detail DTO record to transfer error detail data between service and client layers.
 *
 * @param id          the identifier of the error detail
 * @param batchId     the identifier of the associated batch
 * @param category    the category of the error
 * @param description the description of the error
 *
 * @author Shailesh Halor
 */
public record ErrorDetailDto(
        Long id,
        @NotNull Long batchId,
        @NotNull @Size(max = LengthConstants.ERROR_CATEGORY) String category,
        @NotNull String description
) {
}
