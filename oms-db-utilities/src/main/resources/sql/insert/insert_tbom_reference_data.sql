-- Description: Script to insert reference data into tbom_reference_data table
-- Assumes helper procedures/functions (add_reference_data_proc) are already created in the database.
-- Idempotent inserts using existing helper.

TRUNCATE TABLE tbom_reference_data;

BEGIN
    -- Reference data types (non-editable meta types)
    add_reference_data_proc('REF_DATA_TYPE','DOCUMENT_TYPE','N','Type of document (e.g. INVOICE, POLICY)');
    add_reference_data_proc('REF_DATA_TYPE','DOCUMENT_NAME','N','Name of document (e.g. IVZRECPA, POSHOOFF)');
    add_reference_data_proc('REF_DATA_TYPE','METADATA_KEY','N','Keys for metadata associated with documents');
    add_reference_data_proc('REF_DATA_TYPE','DOCUMENT_STATUS','N','Status of document requests');
    add_reference_data_proc('REF_DATA_TYPE','BATCH_STATUS','N','Status of document request batch jobs');
    add_reference_data_proc('REF_DATA_TYPE','SOURCE_SYSTEM','N','Source systems requesting documents');
    add_reference_data_proc('REF_DATA_TYPE','APP_DOC_SPEC','N','Application document specifications for configurations');
    add_reference_data_proc('REF_DATA_TYPE','FOOTER_ID','N','Footer identifiers for document footers');
    add_reference_data_proc('REF_DATA_TYPE','DOC_CONFIG_CODE','N','Document configuration codes for various settings');

    -- Document Statuses (non-editable)
    add_reference_data_proc('DOCUMENT_STATUS','QUEUED','N','Queued for processing');
    add_reference_data_proc('DOCUMENT_STATUS','PROCESSING','N','Processing');
    add_reference_data_proc('DOCUMENT_STATUS','STOPPED','N','Stopped processing');
    add_reference_data_proc('DOCUMENT_STATUS','FAILED','N','Failed to process');
    add_reference_data_proc('DOCUMENT_STATUS','COMPLETED','N','Completed successfully');

    -- Batch Statuses (non-editable)
    add_reference_data_proc('BATCH_STATUS','PROCESSING_OMS','N','Batch processing in OMS');
    add_reference_data_proc('BATCH_STATUS','PROCESSING_THUNDERHEAD','N','Batch processing in thunderhead');
    add_reference_data_proc('BATCH_STATUS','STOPPED_THUNDERHEAD','N','Batch processing stopped in thunderhead');
    add_reference_data_proc('BATCH_STATUS','FAILED_OMS','N','Batch processing failed in OMS');
    add_reference_data_proc('BATCH_STATUS','FAILED_THUNDERHEAD','N','Batch processing failed in thunderhead');
    add_reference_data_proc('BATCH_STATUS','COMPLETED','N','Batch processed successfully in thunderhead');

    -- Source Systems (non-editable)
    add_reference_data_proc('SOURCE_SYSTEM','CIBT','Y','CIBT source system');
    add_reference_data_proc('SOURCE_SYSTEM','ARCADE','Y','ARCADE source system');

    -- Document Types (non-editable)
    add_reference_data_proc('DOCUMENT_TYPE','POLICY','Y','Policy Documents');
    add_reference_data_proc('DOCUMENT_TYPE','INVOICE','Y','Invoice Documents');

    -- Document Names (non-editable)
    add_reference_data_proc('DOCUMENT_NAME','POSHOOFF','Y','Short Offer');
    add_reference_data_proc('DOCUMENT_NAME','IVZRECPA','Y','Zero Reconciliation or Premium Adjustment');
    add_reference_data_proc('DOCUMENT_NAME','IVBRKCOM','Y','Broker Commission Statement');
    add_reference_data_proc('DOCUMENT_NAME','IVCLFCAP','Y','Notice of Credit Limit Fee Cap Breach');

    -- Metadata Keys (editable)
    add_reference_data_proc('METADATA_KEY','REQUEST_CORRELATION_ID','N','Request correlation Id');
    add_reference_data_proc('METADATA_KEY','SOURCE_ENVIRONMENT','N','Source environment');
    add_reference_data_proc('METADATA_KEY','LANGUAGE_CODE','N','Language code');
    add_reference_data_proc('METADATA_KEY','FOOTER_ID','N','Footer identifier');
    add_reference_data_proc('METADATA_KEY','ATRADIUS_ORG_ID','Y','Atradius organization Id');
    add_reference_data_proc('METADATA_KEY','CUSTOMER_ID','Y','Customer Id');
    add_reference_data_proc('METADATA_KEY','POLICY_ID','Y','Policy Id');
    add_reference_data_proc('METADATA_KEY','INVOICE_ID','Y','Invoice Id');

    -- Application Document Specifications (non-editable)
    add_reference_data_proc('APP_DOC_SPEC','*','N','Default configuration for all document names');
    add_reference_data_proc('APP_DOC_SPEC','PO','Y','Policy documents name starting with PO');
    add_reference_data_proc('APP_DOC_SPEC','POSHOOFF','Y','Policy documents name starting with POSHOOFF');
    add_reference_data_proc('APP_DOC_SPEC','IV','Y','Invoice documents name starting with IV');

    -- Footer Identifiers (editable)
    add_reference_data_proc('FOOTER_ID','0','N','Default footer id');
    add_reference_data_proc('FOOTER_ID','1','Y','Footer id 1');
    add_reference_data_proc('FOOTER_ID','2','Y','Footer id 2');
    add_reference_data_proc('FOOTER_ID','3','Y','Footer id 3');
    add_reference_data_proc('FOOTER_ID','4','Y','Footer id 4');

    -- Document Configuration Codes (editable)
    add_reference_data_proc('DOC_CONFIG_CODE','SIGNEE_1','N','Document reference code for signee 1');
    add_reference_data_proc('DOC_CONFIG_CODE','SIGNEE_2','N','Document reference code for signee 2');
    add_reference_data_proc('DOC_CONFIG_CODE','TOPIC_NAME','N','Document reference code for topic name');
    add_reference_data_proc('DOC_CONFIG_CODE','BATCH_CONFIG_ID','N','Document reference code for thunderhead batch configuration identifier');

    COMMIT;
END;
/
