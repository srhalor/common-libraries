-- Description: Script to insert reference data into tbom_reference_data table

-- Clear existing reference data so this script can be run multiple times safely
TRUNCATE TABLE tbom_reference_data;

DECLARE
    PROCEDURE insert_reference(
        p_type IN VARCHAR2,
        p_name IN VARCHAR2,
        p_description IN VARCHAR2 := NULL,
        p_effect_from IN DATE := DATE '2020-01-01',
        p_effect_to IN DATE := DATE '4712-12-31'
    ) IS
    BEGIN
        -- Insert only if the (type,name) pair does not already exist to make the script idempotent
        INSERT INTO tbom_reference_data (ref_data_type, ref_data_name, description, effect_from_dat, effect_to_dat)
        SELECT p_type, p_name, p_description, p_effect_from, p_effect_to
        FROM DUAL
        WHERE NOT EXISTS (SELECT 1
                          FROM tbom_reference_data r
                          WHERE r.ref_data_type = p_type
                            AND r.ref_data_name = p_name);
    EXCEPTION
        WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20010,
                                    'Failed to insert reference ' || p_type || ' / ' || p_name || ': ' || SQLERRM);
    END insert_reference;

BEGIN

    -- Reference data types to categorize reference data entries
    insert_reference('REF_DATA_TYPE', 'DOCUMENT_TYPE', 'Type of document (e.g. INVOICE, POLICY)');
    insert_reference('REF_DATA_TYPE', 'DOCUMENT_NAME', 'Specific document names (e.g. IVZRECPA, POSHOOFF)');
    insert_reference('REF_DATA_TYPE', 'METADATA_KEY', 'Keys for metadata associated with documents');
    insert_reference('REF_DATA_TYPE', 'DOCUMENT_STATUS', 'Status of document requests');
    insert_reference('REF_DATA_TYPE', 'BATCH_STATUS', 'Status of document batches');
    insert_reference('REF_DATA_TYPE', 'SOURCE_SYSTEM', 'Source systems generating document requests');
    insert_reference('REF_DATA_TYPE', 'APP_DOC_SPEC', 'Application document specifications for configurations');
    insert_reference('REF_DATA_TYPE', 'FOOTER_ID', 'Footer identifiers for document footers');
    insert_reference('REF_DATA_TYPE', 'DOC_CONFIG_CODE', 'Document configuration codes for various settings');

    -- Actual reference data entries

    -- Source Systems
    insert_reference('SOURCE_SYSTEM', 'CIBT', 'CIBT source system');
    insert_reference('SOURCE_SYSTEM', 'ARCADE', 'ARCADE source system');

    -- Document Types
    insert_reference('DOCUMENT_TYPE', 'POLICY', 'Policy Documents');
    insert_reference('DOCUMENT_TYPE', 'INVOICE', 'Invoice Documents');

    -- Document Names
    insert_reference('DOCUMENT_NAME', 'POSHOOFF', 'Short offer policy document');
    insert_reference('DOCUMENT_NAME', 'IVZRECPA', 'Zero reconciliation or premium adjustment invoice document');
    insert_reference('DOCUMENT_NAME', 'IVBRKCOM', 'Broker Commission Statement invoice document');
    insert_reference('DOCUMENT_NAME', 'IVCLFCAP', 'Notice of Credit Limit Fee Cap Breach invoice document');

    -- Metadata Keys
    insert_reference('METADATA_KEY', 'REQUEST_CORRELATION_ID', 'Metadata key for request correlation id');
    insert_reference('METADATA_KEY', 'SOURCE_ENVIRONMENT', 'Metadata key for source environment');
    insert_reference('METADATA_KEY', 'LANGUAGE_CODE', 'Metadata key for language code');
    insert_reference('METADATA_KEY', 'FOOTER_ID', 'Metadata key for policy number');
    insert_reference('METADATA_KEY', 'ATRADIUS_ORG_ID', 'Metadata key for Atradius organization identifier');
    insert_reference('METADATA_KEY', 'CUSTOMER_ID', 'Metadata key for customer identifier');
    insert_reference('METADATA_KEY', 'POLICY_ID', 'Metadata key for policy number');
    insert_reference('METADATA_KEY', 'INVOICE_ID', 'Metadata key for policy number');

    -- Document Statuses
    insert_reference('DOCUMENT_STATUS', 'NEW', 'New document request received');
    insert_reference('DOCUMENT_STATUS', 'PROCESSING', 'Document request is being processed');
    insert_reference('DOCUMENT_STATUS', 'IN_THUNDERHEAD', 'Document request is being processed in thunderhead');
    insert_reference('DOCUMENT_STATUS', 'STOPPED', 'Document request processing stopped');
    insert_reference('DOCUMENT_STATUS', 'FAILED', 'Document request processing failed');
    insert_reference('DOCUMENT_STATUS', 'COMPLETED', 'Document request processed successfully');

    -- Batch Statuses
    insert_reference('BATCH_STATUS', 'SUBMITTED', 'Batch submitted to thunderhead successfully');
    insert_reference('BATCH_STATUS', 'PROCESSING', 'Batch is being processed by thunderhead');
    insert_reference('BATCH_STATUS', 'STOPPED', 'Batch processing stopped in thunderhead');
    insert_reference('BATCH_STATUS', 'FAILED', 'Batch processing failed in thunderhead');
    insert_reference('BATCH_STATUS', 'COMPLETED', 'Batch processed successfully in thunderhead');

    -- Application Document Specifications
    insert_reference('APP_DOC_SPEC', '*', 'Default configuration for all document names');
    insert_reference('APP_DOC_SPEC', 'PO', 'Policy documents name starting with PO');
    insert_reference('APP_DOC_SPEC', 'POSHOOFF', 'Policy documents name starting with POSHOOFF');
    insert_reference('APP_DOC_SPEC', 'IV', 'Invoice documents name starting with IV');

    -- Footer Identifiers
    insert_reference('FOOTER_ID', '0', 'Default footer for documents');
    insert_reference('FOOTER_ID', '1', 'Footer id 1 for documents');
    insert_reference('FOOTER_ID', '2', 'Footer id 2 for documents');
    insert_reference('FOOTER_ID', '3', 'Footer id 3 for documents');
    insert_reference('FOOTER_ID', '4', 'Footer id 4 for documents');

    -- Document Configuration Codes
    insert_reference('DOC_CONFIG_CODE', 'SIGNEE_1', 'Footer reference code for signee 1');
    insert_reference('DOC_CONFIG_CODE', 'SIGNEE_2', 'Footer reference code for signee 2');
    insert_reference('DOC_CONFIG_CODE', 'TOPIC_NAME', 'Footer reference code for topic name');
    insert_reference('DOC_CONFIG_CODE', 'BATCH_CONFIG_ID', 'Thunderhead batch config id');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
