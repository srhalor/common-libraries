package com.shdev.omsdatabase.constants;

/**
 * Known Thunderhead batch statuses as populated by the reference data seeds.
 * These values map to TBOM_REFERENCE_DATA entries with type BATCH_STATUS.
 *
 * @author Shailesh Halor
 */
public enum BatchStatus {
    PROCESSING,
    SUBMITTED_TO_TH,
    STOPPED,
    FAILED,
    COMPLETED
}
