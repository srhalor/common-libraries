package com.shdev.omsdatabase.dto;

import java.time.OffsetDateTime;

/**
 * Document Configuration DTO record to transfer document configuration data from service layer to client.
 *
 * @param id            the unique identifier of the document configuration
 * @param footer        the footer reference data lite DTO
 * @param appDocSpec    the application document specification reference data lite DTO
 * @param code          the code reference data lite DTO
 * @param value         the configuration metadataValue
 * @param desc          the description of the document configuration
 * @param effectFromDat the date from which the configuration is effective
 * @param effectToDat   the date until which the configuration is effective
 * @param createdDat    the timestamp when the configuration was created
 * @param lastUpdateDat the timestamp when the configuration was last updated
 * @param createUid     the user identifier who created the configuration
 * @param lastUpdateUid the user identifier who last updated the configuration
 * @author Shailesh Halor
 */
public record DocumentConfigOutDto(
        Long id,
        ReferenceDataLiteDto footer,
        ReferenceDataLiteDto appDocSpec,
        ReferenceDataLiteDto code,
        String value,
        String desc,
        OffsetDateTime effectFromDat,
        OffsetDateTime effectToDat,
        OffsetDateTime createdDat,
        OffsetDateTime lastUpdateDat,
        String createUid,
        String lastUpdateUid
) {
}

