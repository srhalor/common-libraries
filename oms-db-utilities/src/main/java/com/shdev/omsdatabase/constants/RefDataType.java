package com.shdev.omsdatabase.constants;

/**
 * Enumeration of reference data types stored in TBOM_REFERENCE_DATA. These mirror the values
 * inserted by the insert_tbom_reference_data.sql script and should be used wherever a
 * particular reference data type needs to be referenced programmatically.
 *
 * @author Shailesh Halor
 */
public enum RefDataType {
    DOCUMENT_TYPE,
    DOCUMENT_NAME,
    METADATA_KEY,
    DOCUMENT_STATUS,
    BATCH_STATUS,
    SOURCE_SYSTEM,
    APP_DOC_SPEC,
    FOOTER_ID,
    DOC_CONFIG_CODE
}
