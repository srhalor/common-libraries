--
-- File: create_tbom_th_batches.sql
-- Purpose: Create and manage the tbom_th_batches table and supporting objects (Oracle canonical).
-- Summary: Stores Thunderhead batch details linked to document requests, including
--          batch status (reference data), sync/event flags, retry counter, and
--          audit metadata. Provides indexes for common lookups and triggers to
--          assign keys/timestamps and default creator UIDs.
-- Usage: Run once as part of schema provisioning or during clean rebuilds. The
--        PK is populated from sequence when not provided. Timestamps are set
--        automatically; creator UIDs default to USER when missing.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_th_batches CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- Drop sequence if exists
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE sqomthb_th_batch_id';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- sequence does not exist
            RAISE;
        END IF;
END;
/

-- Create sequence for PK (used if ID not provided on INSERT)
CREATE SEQUENCE sqomthb_th_batch_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE tbom_th_batches
(
    id                 NUMBER PRIMARY KEY,
    omdrt_id           NUMBER                  NOT NULL,
    th_batch_id        NUMBER    DEFAULT 10000 NOT NULL,
    omrda_th_status_id NUMBER                  NOT NULL,
    batch_name         VARCHAR2(100)           NOT NULL,
    dms_document_id    NUMBER,
    sync_status        NUMBER(1) DEFAULT 0     NOT NULL,
    event_status       NUMBER(1) DEFAULT 0     NOT NULL,
    retry_count        NUMBER    DEFAULT 0     NOT NULL,
    created_dat        TIMESTAMP               NOT NULL,
    last_update_dat    TIMESTAMP               NOT NULL,
    create_uid_header  VARCHAR2(20)            NOT NULL,
    create_uid_token   VARCHAR2(20)            NOT NULL
);

-- Add comments to table and columns
COMMENT ON TABLE tbom_th_batches IS 'Thunderhead batch details associated with document requests.';
COMMENT ON COLUMN tbom_th_batches.id IS 'Primary key for Thunderhead batch.';
COMMENT ON COLUMN tbom_th_batches.omdrt_id IS 'Foreign key to tbom_document_requests(id). One request can have multiple batches.';
COMMENT ON COLUMN tbom_th_batches.th_batch_id IS 'External Thunderhead batch identifier; defaults to 10000 if not provided.';
COMMENT ON COLUMN tbom_th_batches.omrda_th_status_id IS 'Foreign key to tbom_reference_data(id) with type BATCH_STATUS indicating batch processing status.';
COMMENT ON COLUMN tbom_th_batches.batch_name IS 'Batch name passed to Thunderhead when creating the batch.';
COMMENT ON COLUMN tbom_th_batches.dms_document_id IS 'Identifier of the generated document in DMS (nullable until available).';
COMMENT ON COLUMN tbom_th_batches.sync_status IS 'Flag indicating whether the Thunderhead batch status has been synchronized back to OMS.';
COMMENT ON COLUMN tbom_th_batches.event_status IS 'Flag indicating whether the batch status event has been published.';
COMMENT ON COLUMN tbom_th_batches.retry_count IS 'Number of retry attempts for synchronization and event publishing.';
COMMENT ON COLUMN tbom_th_batches.created_dat IS 'Record creation timestamp set at insert.';
COMMENT ON COLUMN tbom_th_batches.last_update_dat IS 'Record last update timestamp set on insert/update.';
COMMENT ON COLUMN tbom_th_batches.create_uid_header IS 'User ID (from request header) that created the record; defaults to USER when missing.';
COMMENT ON COLUMN tbom_th_batches.create_uid_token IS 'User ID (from JWT token) that created the record; defaults to USER when missing.';

-- Create indexes (omtbe_01..06)
CREATE INDEX omtbe_01 ON tbom_th_batches (omrda_th_status_id);
CREATE INDEX omtbe_02 ON tbom_th_batches (th_batch_id);
CREATE INDEX omtbe_03 ON tbom_th_batches (sync_status);
CREATE INDEX omtbe_04 ON tbom_th_batches (event_status);
CREATE INDEX omtbe_05 ON tbom_th_batches (omdrt_id);
CREATE INDEX omtbe_06 ON tbom_th_batches (retry_count);

-- Add foreign key constraints
ALTER TABLE tbom_th_batches
    ADD CONSTRAINT omdrt_omtbe_fk1 FOREIGN KEY (omdrt_id)
        REFERENCES tbom_document_requests (id);
ALTER TABLE tbom_th_batches
    ADD CONSTRAINT omrda_omtbe_fk2 FOREIGN KEY (omrda_th_status_id)
        REFERENCES tbom_reference_data (id);

-- Trigger to set PK (if missing), created_dat, last_update_dat, create_uid_header, and create_uid_token before insert
CREATE OR REPLACE TRIGGER omtbe_01t_bir
    BEFORE INSERT
    ON tbom_th_batches
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT sqomthb_th_batch_id.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    IF :NEW.created_dat IS NULL THEN
        :NEW.created_dat := SYSTIMESTAMP;
    END IF;
    :NEW.last_update_dat := SYSTIMESTAMP;
    IF :NEW.create_uid_header IS NULL THEN
        :NEW.create_uid_header := USER;
    END IF;
    IF :NEW.create_uid_token IS NULL THEN
        :NEW.create_uid_token := USER;
    END IF;
END omtbe_01t_bir;
/

-- Trigger to set last_update_dat before update
CREATE OR REPLACE TRIGGER omtbe_01t_bur
    BEFORE UPDATE
    ON tbom_th_batches
    FOR EACH ROW
BEGIN
    :NEW.last_update_dat := SYSTIMESTAMP;
END omtbe_01t_bur;
/
