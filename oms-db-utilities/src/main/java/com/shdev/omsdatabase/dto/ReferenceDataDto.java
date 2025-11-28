package com.shdev.omsdatabase.dto;

import com.shdev.omsdatabase.constants.LengthConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Reference Data DTO record to transfer reference data between service and client layers.
 *
 * @param id            the unique identifier of the reference data
 * @param refDataType   the type of the reference data
 * @param refDataValue  the value of the reference data
 * @param description   the description of the reference data
 * @param editable      indicates if the reference data is editable
 * @param effectFromDat the date from which the reference data is effective
 * @param effectToDat   the date until which the reference data is effective
 * @param createdDat    the timestamp when the reference data was created
 * @param lastUpdateDat the timestamp when the reference data was last updated
 * @param createUid     the user identifier who created the reference data
 * @param lastUpdateUid the user identifier who last updated the reference data
 *
 * @author Shailesh Halor
 */
public record ReferenceDataDto(
        Long id,
        @NotNull @Size(max = LengthConstants.REF_DATA_TYPE) String refDataType,
        @NotNull @Size(max = LengthConstants.REF_DATA_VALUE) String refDataValue,
        @Size(max = LengthConstants.DESCRIPTION) String description,
        @NotNull Boolean editable,
        LocalDate effectFromDat,
        LocalDate effectToDat,
        Instant createdDat,
        Instant lastUpdateDat,
        String createUid,
        String lastUpdateUid
) {
}
