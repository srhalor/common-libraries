--
-- File: create_tbom_requests_metadata_values.sql
-- Purpose: Create tbom_requests_metadata_values to store actual metadata values per document request.
-- Notes: Each row maps one request (omdrt_id) to one metadata key (omrda_id) with a concrete value.
--        omrda_id references tbom_reference_data entries of type METADATA_KEY. No ON DELETE CASCADE.
-- Dependencies: Requires tbom_document_requests and tbom_reference_data to exist.
-- Changelog:
--   2025-10-23 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_requests_metadata_values;

CREATE TABLE tbom_requests_metadata_values
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key for metadata value.',
    omdrt_id       BIGINT       NOT NULL COMMENT 'Foreign key to tbom_document_requests.id (the owning document request).',
    omrda_id       BIGINT       NOT NULL COMMENT 'Foreign key to tbom_reference_data.id; must refer to a METADATA_KEY entry.',
    metadata_value VARCHAR(255) NOT NULL COMMENT 'Concrete value for the referenced metadata key (string up to 255 chars).',
    CONSTRAINT omdrt_omrme_fk1 FOREIGN KEY (omdrt_id)
        REFERENCES tbom_document_requests (id)
        ON DELETE RESTRICT,
    CONSTRAINT omrda_omrme_fk2 FOREIGN KEY (omrda_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT
) COMMENT ='Stores actual metadata values for each tbom_document_requests row.';

-- Create indexes
CREATE INDEX omrme_01 ON tbom_requests_metadata_values (omdrt_id);
CREATE INDEX omrme_02 ON tbom_requests_metadata_values (omrda_id);
CREATE INDEX omrme_03 ON tbom_requests_metadata_values (metadata_value);
