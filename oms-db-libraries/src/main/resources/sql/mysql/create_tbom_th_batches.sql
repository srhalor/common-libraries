--
-- File: create_tbom_th_batches.sql
-- Purpose: Create and manage the tbom_th_batches table and supporting objects (MySQL variant).
-- Summary: Stores Thunderhead batch details linked to document requests, including batch status
--          (reference data), sync/event flags, retry counter, and audit metadata. Includes indexes
--          and triggers to set timestamps and default creator UIDs when missing.
-- Usage: Run as part of schema provisioning or clean rebuilds. Timestamps are set automatically;
--        creator UIDs default to CURRENT_USER() when not provided.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_th_batches;

CREATE TABLE tbom_th_batches
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Internal primary key for Thunderhead batch records.',
    omdrt_id           BIGINT       NOT NULL COMMENT 'Foreign key to tbom_document_requests(id). One request can have multiple batches.',
    th_batch_id        BIGINT       NOT NULL DEFAULT 10000 COMMENT 'External Thunderhead batch identifier; defaults to 10000 if not provided.',
    omrda_th_status_id BIGINT       NOT NULL COMMENT 'Foreign key to tbom_reference_data(id) with type BATCH_STATUS indicating batch processing status.',
    batch_name         VARCHAR(100) NOT NULL COMMENT 'Batch name passed to Thunderhead when creating the batch.',
    dms_document_id    BIGINT COMMENT 'Identifier of the generated document in DMS (nullable until available).',
    sync_status        BOOLEAN      NOT NULL DEFAULT FALSE COMMENT 'Flag indicating whether the Thunderhead batch status has been synchronized back to OMS.',
    event_status       BOOLEAN      NOT NULL DEFAULT FALSE COMMENT 'Flag indicating whether the batch status event has been published.',
    retry_count        INT          NOT NULL DEFAULT 0 COMMENT 'Number of retry attempts for synchronization and event publishing.',
    created_dat        DATETIME     NOT NULL COMMENT 'Record creation timestamp set at insert.',
    last_update_dat    DATETIME     NOT NULL COMMENT 'Record last update timestamp set on insert/update.',
    create_uid_header  VARCHAR(20)  NOT NULL COMMENT 'User ID (from request header) that created the record; defaults to CURRENT_USER() when missing.',
    create_uid_token   VARCHAR(20)  NOT NULL COMMENT 'User ID (from JWT token) that created the record; defaults to CURRENT_USER() when missing.',
    CONSTRAINT omdrt_omtbe_fk1 FOREIGN KEY (omdrt_id)
        REFERENCES tbom_document_requests (id)
        ON DELETE RESTRICT,
    CONSTRAINT omrda_omtbe_fk2 FOREIGN KEY (omrda_th_status_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT
) COMMENT ='Thunderhead batch details associated with document requests.';

-- Create indexes (omtbe_01..06)
CREATE INDEX omtbe_01 ON tbom_th_batches (omrda_th_status_id);
CREATE INDEX omtbe_02 ON tbom_th_batches (th_batch_id);
CREATE INDEX omtbe_03 ON tbom_th_batches (sync_status);
CREATE INDEX omtbe_04 ON tbom_th_batches (event_status);
CREATE INDEX omtbe_05 ON tbom_th_batches (omdrt_id);
CREATE INDEX omtbe_06 ON tbom_th_batches (retry_count);

-- Triggers
DELIMITER $$
-- BEFORE INSERT: set created_dat, last_update_dat and default creator UIDs when missing
CREATE TRIGGER omtbe_01t_bir
    BEFORE INSERT
    ON tbom_th_batches
    FOR EACH ROW
BEGIN
    SET NEW.created_dat = IFNULL(NEW.created_dat, NOW());
    SET NEW.last_update_dat = NOW();
    SET NEW.create_uid_header = IFNULL(NEW.create_uid_header, CURRENT_USER());
    SET NEW.create_uid_token = IFNULL(NEW.create_uid_token, CURRENT_USER());
END$$

-- BEFORE UPDATE: bump last_update_dat
CREATE TRIGGER omtbe_01t_bur
    BEFORE UPDATE
    ON tbom_th_batches
    FOR EACH ROW
BEGIN
    SET NEW.last_update_dat = NOW();
END$$
DELIMITER ;
