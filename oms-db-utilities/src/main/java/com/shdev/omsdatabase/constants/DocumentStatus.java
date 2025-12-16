package com.shdev.omsdatabase.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum representing valid document processing statuses as seeded by
 * insert_tbom_reference_data.sql (REF_DATA_TYPE = DOCUMENT_STATUS).
 *
 * <p>The underlying database rows use REF_DATA_TYPE = "DOCUMENT_STATUS" and
 * REF_DATA_VALUE matching the {@link #code} of each constant below.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Convert from database string to enum
 *     DocumentStatus status = DocumentStatus.fromCode(entity.getStatus());
 *
 *     // Persist enum value
 *     entity.setStatus(DocumentStatus.FAILED.getCode());
 *
 *     // Branch logic
 *     if (status == DocumentStatus.COMPLETED) { // success flow }
 * </pre>
 *
 * <p>Lookup is null-safe; unknown codes return null.</p>
 */
@Getter
@AllArgsConstructor
public enum DocumentStatus {
    QUEUED("QUEUED", "Queued for processing"),
    PROCESSING("PROCESSING", "Processing"),
    STOPPED("STOPPED", "Stopped processing"),
    FAILED("FAILED", "Failed to process"),
    COMPLETED("COMPLETED", "Completed successfully");

    /** Constant for the shared REF_DATA_TYPE value. */
    public static final String REF_DATA_TYPE = "DOCUMENT_STATUS";

    private final String code;
    private final String description;

    private static final Map<String, DocumentStatus> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(DocumentStatus::getCode, e -> e));

    /**
     * Null-safe lookup of enum by its code.
     *
     * @param code value stored in REF_DATA_VALUE column
     * @return matching enum constant or null if not found
     */
    public static DocumentStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        return BY_CODE.get(code);
    }
}
