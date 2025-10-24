--
-- File: create_tbom_document_requests_blob.sql
-- Purpose: Create table tbom_document_requests_blob to store JSON/XML request payloads
--          in a 1:1 relationship with tbom_document_requests.
-- Notes: omdrt_id is both the PK and an FK to tbom_document_requests(id); JSON is required, XML optional.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_document_requests_blob;

CREATE TABLE tbom_document_requests_blob
(
    omdrt_id     BIGINT NOT NULL COMMENT 'Identifier matching tbom_document_requests.id (1:1); serves as both primary key and foreign key.',
    json_request JSON   NOT NULL COMMENT 'Request payload in JSON format; required.',
    xml_request  TEXT   NULL COMMENT 'Request payload in XML format; optional.',
    CONSTRAINT pk_tbom_document_requests_blob PRIMARY KEY (omdrt_id),
    CONSTRAINT fk_tbom_document_requests_blob_req FOREIGN KEY (omdrt_id)
        REFERENCES tbom_document_requests (id)
        ON DELETE CASCADE
) COMMENT ='Stores JSON and optional XML request payloads for a single tbom_document_requests row (1:1 mapping via PK=FK).';
