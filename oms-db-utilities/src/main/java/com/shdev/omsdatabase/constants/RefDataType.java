package com.shdev.omsdatabase.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum representing the supported REF_DATA_TYPE values inserted by the SQL script
 * insert_tbom_reference_data.sql. Each enum constant maps to the REF_DATA_TYPE code
 * persisted in TBOM_REFERENCE_DATA. Use this enum anywhere you need a type-safe
 * reference instead of hard-coded strings.
 *
 * <p>Example usage:
 * <pre>
 *     // Convert from database string to enum
 *     RefDataType type = RefDataType.fromCode(entity.getRefDataType());
 *
 *     // Persist enum value
 *     entity.setRefDataType(RefDataType.DOCUMENT_TYPE.getCode());
 * </pre>
 *
 * <p>Lookup is null-safe and returns null when the code isn't recognized.</p>
 */
@Getter
@AllArgsConstructor
public enum RefDataType {
    DOCUMENT_TYPE("DOCUMENT_TYPE", "Type of document (e.g. INVOICE, POLICY)"),
    DOCUMENT_NAME("DOCUMENT_NAME", "Name of document (e.g. IVZRECPA, POSHOOFF)"),
    METADATA_KEY("METADATA_KEY", "Keys for metadata associated with documents"),
    DOCUMENT_STATUS("DOCUMENT_STATUS", "Status of document requests"),
    BATCH_STATUS("BATCH_STATUS", "Status of document request batch jobs"),
    SOURCE_SYSTEM("SOURCE_SYSTEM", "Source systems requesting documents"),
    APP_DOC_SPEC("APP_DOC_SPEC", "Application document specifications for configurations"),
    FOOTER_ID("FOOTER_ID", "Footer identifiers for document footers"),
    DOC_CONFIG_CODE("DOC_CONFIG_CODE", "Document configuration codes for various settings");

    private final String code;
    private final String description;

    private static final Map<String, RefDataType> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(RefDataType::getCode, e -> e));

    /**
     * Null-safe lookup of enum by its code. Returns null if code not found.
     *
     * @param code database value stored in REF_DATA_TYPE
     * @return matching enum or null
     */
    public static RefDataType fromCode(String code) {
        if (code == null) {
            return null;
        }
        return BY_CODE.get(code);
    }
}

