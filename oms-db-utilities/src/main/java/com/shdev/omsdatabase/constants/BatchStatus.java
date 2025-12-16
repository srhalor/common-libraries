package com.shdev.omsdatabase.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum representing valid batch processing statuses as seeded by
 * insert_tbom_reference_data.sql (REF_DATA_TYPE = BATCH_STATUS).
 *
 * <p>The underlying database rows use REF_DATA_TYPE = "BATCH_STATUS" and
 * REF_DATA_VALUE matching the {@link #code} of each constant below.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Convert from database string to enum
 *     BatchStatus status = BatchStatus.fromCode(entity.getBatchStatus());
 *
 *     // Persist enum value
 *     entity.setBatchStatus(BatchStatus.FAILED_THUNDERHEAD.getCode());
 *
 *     // Branch logic
 *     if (status == BatchStatus.COMPLETED) { // success flow }
 * </pre>
 *
 * <p>Lookup is null-safe; unknown codes return null.</p>
 */
@Getter
@AllArgsConstructor
public enum BatchStatus {
    PROCESSING_OMS("PROCESSING_OMS", "Batch processing in OMS"),
    PROCESSING_THUNDERHEAD("PROCESSING_THUNDERHEAD", "Batch processing in thunderhead"),
    STOPPED_THUNDERHEAD("STOPPED_THUNDERHEAD", "Batch processing stopped in thunderhead"),
    FAILED_OMS("FAILED_OMS", "Batch processing failed in OMS"),
    FAILED_THUNDERHEAD("FAILED_THUNDERHEAD", "Batch processing failed in thunderhead"),
    COMPLETED("COMPLETED", "Batch processed successfully in thunderhead");

    /** Constant for the shared REF_DATA_TYPE value. */
    public static final String REF_DATA_TYPE = "BATCH_STATUS";

    private final String code;
    private final String description;

    private static final Map<String, BatchStatus> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(BatchStatus::getCode, e -> e));

    /**
     * Null-safe lookup of enum by its code.
     *
     * @param code value stored in REF_DATA_VALUE column
     * @return matching enum constant or null if not found
     */
    public static BatchStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        return BY_CODE.get(code);
    }
}

