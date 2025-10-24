--
-- File: create_tbom_document_requests.sql
-- Purpose: Create and manage the tbom_document_requests table and supporting objects.
-- Summary: Defines the document requests table capturing source system, document type/name, and request status,
--          with timestamps and creator identifiers; includes indexes and triggers to default timestamps and
--          maintain last-update time.
-- Usage: Run once as part of schema provisioning. Application should populate create_uid_header and create_uid_token
--        from API header and token; triggers set created_dat and last_update_dat automatically.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_document_requests;

CREATE TABLE IF NOT EXISTS tbom_document_requests
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key for document requests.',
    omrda_source_system_id BIGINT      NOT NULL COMMENT 'Foreign key to tbom_reference_data (SOURCE_SYSTEM) identifying the Source System making the request.',
    omrda_document_type_id BIGINT      NOT NULL COMMENT 'Foreign key to tbom_reference_data (DOCUMENT_TYPE) identifying the type of document requested.',
    omrda_document_name_id BIGINT      NOT NULL COMMENT 'Foreign key to tbom_reference_data (DOCUMENT_NAME) identifying the name of the document requested.',
    omrda_doc_status_id    BIGINT      NOT NULL COMMENT 'Foreign key to tbom_reference_data (DOCUMENT_STATUS) indicating request processing status.',
    created_dat            DATETIME    NOT NULL COMMENT 'Record creation timestamp.',
    last_update_dat        DATETIME    NOT NULL COMMENT 'Record last update timestamp.',
    create_uid_header      VARCHAR(20) NOT NULL COMMENT 'User ID from request header when creating the record.',
    create_uid_token       VARCHAR(20) NOT NULL COMMENT 'User ID from JWT token when creating the record.',
    CONSTRAINT omrda_omdrt_fk1 FOREIGN KEY (omrda_source_system_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT omrda_omdrt_fk2 FOREIGN KEY (omrda_document_type_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT omrda_omdrt_fk3 FOREIGN KEY (omrda_document_name_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT omrda_omdrt_fk4 FOREIGN KEY (omrda_doc_status_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT ='Table to store document requests and their overall processing status.';

-- Create indexes
CREATE INDEX omdrt_01 ON tbom_document_requests (created_dat);
CREATE INDEX omdrt_02 ON tbom_document_requests (omrda_doc_status_id);
CREATE INDEX omdrt_03 ON tbom_document_requests (omrda_source_system_id);
CREATE INDEX omdrt_04 ON tbom_document_requests (omrda_document_type_id);
CREATE INDEX omdrt_05 ON tbom_document_requests (omrda_document_name_id);

-- Trigger to set created_dat, last_update_dat and default UIDs before insert
DELIMITER $$
CREATE TRIGGER omdrt_01t_bir
    BEFORE INSERT
    ON tbom_document_requests
    FOR EACH ROW
BEGIN
    IF NEW.created_dat IS NULL THEN
        SET NEW.created_dat = NOW();
    END IF;
    SET NEW.last_update_dat = NOW();
    IF NEW.create_uid_header IS NULL OR NEW.create_uid_header = '' THEN
        SET NEW.create_uid_header = CURRENT_USER();
    END IF;
    IF NEW.create_uid_token IS NULL OR NEW.create_uid_token = '' THEN
        SET NEW.create_uid_token = CURRENT_USER();
    END IF;
END$$

-- Trigger to set last_update_dat before update
CREATE TRIGGER omdrt_02t_bur
    BEFORE UPDATE
    ON tbom_document_requests
    FOR EACH ROW
BEGIN
    SET NEW.last_update_dat = NOW();
END$$
DELIMITER ;
