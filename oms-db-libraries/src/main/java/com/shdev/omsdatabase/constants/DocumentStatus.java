package com.shdev.omsdatabase.constants;

/**
 * Known document request statuses as populated by the reference data seeds.
 * These values map to TBOM_REFERENCE_DATA entries with type DOCUMENT_STATUS.
 *
 * @author Shailesh Halor
 */
public enum DocumentStatus {
    NEW,
    PROCESSING,
    STOPPED,
    FAILED,
    COMPLETED
}
