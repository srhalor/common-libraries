--
-- File: create_tbom_document_requests_blob.sql
-- Purpose: Create table tbom_document_requests_blob to store JSON/XML request payloads
--          in a 1:1 relationship with tbom_document_requests.
-- Notes: omdrt_id is both the PK and an FK to tbom_document_requests(id); JSON is required, XML optional.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_document_requests_blob CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

CREATE TABLE tbom_document_requests_blob
(
    omdrt_id     NUMBER NOT NULL,
    json_request CLOB   NOT NULL,
    xml_request  CLOB   NULL,
    CONSTRAINT pk_tbom_document_requests_blob
        PRIMARY KEY (omdrt_id),
    CONSTRAINT fk_tbom_document_requests_blob_req
        FOREIGN KEY (omdrt_id)
            REFERENCES tbom_document_requests (id)
                ON DELETE CASCADE
);

-- Comments (canonical definitions)
COMMENT ON TABLE tbom_document_requests_blob IS
    'Stores JSON and optional XML request payloads for a single tbom_document_requests row (1:1 mapping via PK=FK).';

COMMENT ON COLUMN tbom_document_requests_blob.omdrt_id IS
    'Identifier matching tbom_document_requests.id (1:1); serves as both primary key and foreign key.';

COMMENT ON COLUMN tbom_document_requests_blob.json_request IS
    'Request payload in JSON format; required.';

COMMENT ON COLUMN tbom_document_requests_blob.xml_request IS
    'Request payload in XML format; optional.';
