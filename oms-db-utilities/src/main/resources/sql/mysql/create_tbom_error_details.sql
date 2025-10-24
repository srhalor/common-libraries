--
-- File: create_tbom_error_details.sql
-- Purpose: Create and manage the tbom_error_details table and supporting objects (MySQL variant).
-- Summary: Captures error details encountered during processing of a Document request or Thunderhead batch. Stores the
--          owning batch reference, a short error category, and a detailed description. Provides an
--          index for lookups by batch. Uses AUTO_INCREMENT for PK.
-- Usage: Run as part of schema provisioning or clean rebuilds. Insert a row per error for a batch/request.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_error_details;

CREATE TABLE tbom_error_details
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key for error details.',
    omtbe_id          BIGINT       NOT NULL COMMENT 'Foreign key to tbom_th_batches(id).',
    error_category    VARCHAR(100) NOT NULL COMMENT 'Error category or type (e.g., VALIDATION_ERROR, TIMEOUT, INTERNAL_ERROR).',
    error_description TEXT         NOT NULL COMMENT 'Detailed error description or message.',
    CONSTRAINT omtbe_omedl_fk FOREIGN KEY (omtbe_id)
        REFERENCES tbom_th_batches (id)
        ON DELETE RESTRICT
) COMMENT ='Captures error details encountered during processing of a Document request or Thunderhead batch.';

-- Create index
CREATE INDEX omedl_01 ON tbom_error_details (omtbe_id);
