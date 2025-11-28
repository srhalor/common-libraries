--
-- File: create_tbom_requests_metadata_values.sql
-- Purpose: Create tbom_requests_metadata_values to store actual metadata values per document request.
-- Notes: Each row maps one request (omdrt_id) to one metadata key (omrda_id) with a concrete value.
--        omrda_id references tbom_reference_data entries of refDataType METADATA_KEY. No ON DELETE CASCADE.
-- Dependencies: Requires tbom_document_requests and tbom_reference_data to exist.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_requests_metadata_values CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- Drop sequence if exists
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE sqomrme_metadata_value_id';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- sequence does not exist
            RAISE;
        END IF;
END;
/

-- Create sequence for PK
CREATE SEQUENCE sqomrme_metadata_value_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE tbom_requests_metadata_values
(
    id             NUMBER PRIMARY KEY,
    omdrt_id       NUMBER        NOT NULL,
    omrda_id       NUMBER        NOT NULL,
    metadata_value VARCHAR2(255) NOT NULL
);

-- Add comments to table and columns
COMMENT ON TABLE tbom_requests_metadata_values IS 'Stores actual metadata values for each tbom_document_requests row.';
COMMENT ON COLUMN tbom_requests_metadata_values.id IS 'Primary key for metadata value.';
COMMENT ON COLUMN tbom_requests_metadata_values.omdrt_id IS 'Foreign key to tbom_document_requests.id (the owning document request).';
COMMENT ON COLUMN tbom_requests_metadata_values.omrda_id IS 'Foreign key to tbom_reference_data.id; must refer to a METADATA_KEY entry.';
COMMENT ON COLUMN tbom_requests_metadata_values.metadata_value IS 'Concrete value for the referenced metadata key (string up to 255 chars).';

-- Add foreign key constraints
ALTER TABLE tbom_requests_metadata_values
    ADD CONSTRAINT omdrt_omrme_fk1 FOREIGN KEY (omdrt_id)
        REFERENCES tbom_document_requests (id);
ALTER TABLE tbom_requests_metadata_values
    ADD CONSTRAINT omrda_omrme_fk2 FOREIGN KEY (omrda_id)
        REFERENCES tbom_reference_data (id);

-- Create indexes
CREATE INDEX omrme_01 ON tbom_requests_metadata_values (omdrt_id);
CREATE INDEX omrme_02 ON tbom_requests_metadata_values (omrda_id);
CREATE INDEX omrme_03 ON tbom_requests_metadata_values (metadata_value);

-- Trigger to set PK before insert
CREATE OR REPLACE TRIGGER omrme_01t_bir
    BEFORE INSERT
    ON tbom_requests_metadata_values
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT sqomrme_metadata_value_id.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
END omrme_01t_bir;
/
