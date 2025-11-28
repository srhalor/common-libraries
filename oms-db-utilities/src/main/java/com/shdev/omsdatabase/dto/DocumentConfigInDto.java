package com.shdev.omsdatabase.dto;

import com.shdev.omsdatabase.constants.LengthConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Document Configuration DTO record to transfer document configuration data from client to service layer.
 *
 * @param footerId      the identifier of the footer reference data
 * @param appDocSpecId  the identifier of the application document specification reference data
 * @param codeId        the identifier of the code reference data
 * @param value         the configuration metadataValue
 * @param description   the description of the document configuration
 * @param effectFromDat the date from which the configuration is effective
 * @param effectToDat   the date until which the configuration is effective
 *
 * @author Shailesh Halor
 */
public record DocumentConfigInDto(
        @NotNull Long footerId,
        @NotNull Long appDocSpecId,
        @NotNull Long codeId,
        @NotNull @Size(max = LengthConstants.CONFIG_VALUE) String value,
        @Size(max = LengthConstants.DESCRIPTION) String description,
        LocalDate effectFromDat,
        LocalDate effectToDat
) {
}
