--
-- File: create_tbom_document_requests.sql
-- Purpose: Create and manage the tbom_document_requests table and supporting objects.
-- Summary: Defines the document requests table capturing source system, document type/name, and request status,
--          with timestamps and creator identifiers; includes indexes, a sequence for PKs, and triggers to
--          default timestamps and maintain last-update time.
-- Usage: Run once as part of schema provisioning. Application should populate create_uid_header and create_uid_token
--        from API header and token; triggers set created_dat and last_update_dat automatically.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_document_requests CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- Drop sequence if exists
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE sqomrda_doc_request_id';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- sequence does not exist
            RAISE;
        END IF;
END;
/

-- Create sequence for PK
CREATE SEQUENCE sqomrda_doc_request_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE tbom_document_requests
(
    id                     NUMBER PRIMARY KEY,
    omrda_source_system_id NUMBER       NOT NULL,
    omrda_document_type_id NUMBER       NOT NULL,
    omrda_document_name_id NUMBER       NOT NULL,
    omrda_doc_status_id    NUMBER       NOT NULL,
    created_dat            TIMESTAMP    NOT NULL,
    last_update_dat        TIMESTAMP    NOT NULL,
    create_uid_header      VARCHAR2(20) NOT NULL,
    create_uid_token       VARCHAR2(20) NOT NULL
);

-- Add comments to table and columns
COMMENT ON TABLE tbom_document_requests IS 'Table to store document requests and their overall processing status.';
COMMENT ON COLUMN tbom_document_requests.id IS 'Primary key for document requests.';
COMMENT ON COLUMN tbom_document_requests.omrda_source_system_id IS 'Foreign key to tbom_reference_data (SOURCE_SYSTEM) identifying the Source System making the request.';
COMMENT ON COLUMN tbom_document_requests.omrda_document_type_id IS 'Foreign key to tbom_reference_data (DOCUMENT_TYPE) identifying the type of document requested.';
COMMENT ON COLUMN tbom_document_requests.omrda_document_name_id IS 'Foreign key to tbom_reference_data (DOCUMENT_NAME) identifying the name of the document requested.';
COMMENT ON COLUMN tbom_document_requests.omrda_doc_status_id IS 'Foreign key to tbom_reference_data (DOCUMENT_STATUS) indicating request processing status.';
COMMENT ON COLUMN tbom_document_requests.created_dat IS 'Record creation timestamp.';
COMMENT ON COLUMN tbom_document_requests.last_update_dat IS 'Record last update timestamp.';
COMMENT ON COLUMN tbom_document_requests.create_uid_header IS 'User ID from request header when creating the record.';
COMMENT ON COLUMN tbom_document_requests.create_uid_token IS 'User ID from JWT token when creating the record.';

-- Create indexes
CREATE INDEX omdrt_01 ON tbom_document_requests (created_dat);
CREATE INDEX omdrt_02 ON tbom_document_requests (omrda_doc_status_id);
CREATE INDEX omdrt_03 ON tbom_document_requests (omrda_source_system_id);
CREATE INDEX omdrt_04 ON tbom_document_requests (omrda_document_type_id);
CREATE INDEX omdrt_05 ON tbom_document_requests (omrda_document_name_id);

-- Add foreign key constraints
ALTER TABLE tbom_document_requests
    ADD CONSTRAINT omrda_omdrt_fk1 FOREIGN KEY (omrda_source_system_id)
        REFERENCES tbom_reference_data (id);
ALTER TABLE tbom_document_requests
    ADD CONSTRAINT omrda_omdrt_fk2 FOREIGN KEY (omrda_document_type_id)
        REFERENCES tbom_reference_data (id);
ALTER TABLE tbom_document_requests
    ADD CONSTRAINT omrda_omdrt_fk3 FOREIGN KEY (omrda_document_name_id)
        REFERENCES tbom_reference_data (id);
ALTER TABLE tbom_document_requests
    ADD CONSTRAINT omrda_omdrt_fk4 FOREIGN KEY (omrda_doc_status_id)
        REFERENCES tbom_reference_data (id);

-- Trigger to set PK, created_dat, last_update_dat, create_uid_header, and create_uid_token before insert
CREATE OR REPLACE TRIGGER omdrt_01t_bir
    BEFORE INSERT
    ON tbom_document_requests
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT sqomrda_doc_request_id.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
    :NEW.created_dat := SYSTIMESTAMP;
    :NEW.last_update_dat := SYSTIMESTAMP;
    IF :NEW.create_uid_header IS NULL THEN
        :NEW.create_uid_header := USER;
    END IF;
    IF :NEW.create_uid_token IS NULL THEN
        :NEW.create_uid_token := USER;
    END IF;
END;
/

-- Trigger to set last_update_dat before update
CREATE OR REPLACE TRIGGER omdrt_02t_bur
    BEFORE UPDATE
    ON tbom_document_requests
    FOR EACH ROW
BEGIN
    :NEW.last_update_dat := SYSTIMESTAMP;
END omdrt_02t_bur;
/
