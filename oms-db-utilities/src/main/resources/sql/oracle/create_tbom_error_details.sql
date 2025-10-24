--
-- File: create_tbom_error_details.sql
-- Purpose: Create and manage the tbom_error_details table and supporting objects (Oracle canonical).
-- Summary: Captures error details encountered during processing of a Document request or Thunderhead batch. Stores the
--          owning batch reference, a short error category, and a detailed description. Provides an
--          index for lookups by batch, a sequence for key assignment, and a trigger to populate the
--          primary key when not explicitly provided.
-- Usage: Run once as part of schema provisioning or during clean rebuilds. Insert a row per error
--        encountered for a batch/request. Primary key is assigned from sequence if NULL on insert.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_error_details CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- Drop sequence if exists
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE sqomedl_error_details_id';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- sequence does not exist
            RAISE;
        END IF;
END;
/

-- Create sequence for PK
CREATE SEQUENCE sqomedl_error_details_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE tbom_error_details
(
    id                NUMBER PRIMARY KEY,
    omtbe_id          NUMBER        NOT NULL,
    error_category    VARCHAR2(100) NOT NULL,
    error_description CLOB          NOT NULL
);

-- Add comments to table and columns
COMMENT ON TABLE tbom_error_details IS 'Captures error details encountered during processing of a Document request or Thunderhead batch.';
COMMENT ON COLUMN tbom_error_details.id IS 'Primary key for error details.';
COMMENT ON COLUMN tbom_error_details.omtbe_id IS 'Foreign key to tbom_th_batches(id).';
COMMENT ON COLUMN tbom_error_details.error_category IS 'Error category or type (e.g., VALIDATION_ERROR, TIMEOUT, INTERNAL_ERROR).';
COMMENT ON COLUMN tbom_error_details.error_description IS 'Detailed error description or message.';

-- Add foreign key constraint
ALTER TABLE tbom_error_details
    ADD CONSTRAINT omtbe_omedl_fk FOREIGN KEY (omtbe_id)
        REFERENCES tbom_th_batches (id);

-- Create index
CREATE INDEX omedl_01 ON tbom_error_details (omtbe_id);

-- Trigger to set PK before insert
CREATE OR REPLACE TRIGGER omedl_01t_bir
    BEFORE INSERT
    ON tbom_error_details
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT sqomedl_error_details_id.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
END omedl_01t_bir;
/

