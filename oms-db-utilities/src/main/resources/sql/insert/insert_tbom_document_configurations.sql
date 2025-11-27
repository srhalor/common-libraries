-- Description: Script to insert default document configurations
-- Assumes helper functions/procedures (get_reference_id, add_document_configuration_proc) exist in the database schema.

TRUNCATE TABLE tbom_document_configurations;

BEGIN
    -- Signature configurations for default footer id '0' and all documents
    add_document_configuration_proc('0','*','SIGNEE_1','GBMHEN1','Default signee 1 for all documents');
    add_document_configuration_proc('0','*','SIGNEE_2','DEATES1','Default signee 2 for all documents');

    -- Signature configurations for default footer id '0' and documents starting with POSHOOFF
    add_document_configuration_proc('0','POSHOOFF','SIGNEE_1','EMPTY','Default signee 1 for documents starting with POSHOOFF');
    add_document_configuration_proc('0','POSHOOFF','SIGNEE_2','ESMNOD1','Default signee 2 for documents starting with POSHOOFF');

    -- Signature configurations for default footer id '0' and documents starting with IV
    add_document_configuration_proc('0','IV','SIGNEE_1','GBMHEN1','Default signee 1 for documents starting with IV');
    add_document_configuration_proc('0','IV','SIGNEE_2','DEATES1','Default signee 2 for documents starting with IV');

    -- Signature configurations for footer id '2' and documents starting with POSHOOFF
    add_document_configuration_proc('2','POSHOOFF','SIGNEE_1','NLTKAA1','signee 1 for POSHOOFF documents footer 2');
    add_document_configuration_proc('2','POSHOOFF','SIGNEE_2','ESMNOD1','signee 2 for POSHOOFF documents footer 2');

    -- Signature configurations for footer id '2' and documents starting with IV
    add_document_configuration_proc('2','IV','SIGNEE_1','ESMNOD1','Signee 1 for IV documents footer 2');
    add_document_configuration_proc('2','IV','SIGNEE_2','DEATES1','Signee 2 for IV documents footer 2');

    -- Topic name configurations ( footer id '0' it doesn't matter )
    add_document_configuration_proc('0','POSHOOFF','TOPIC_NAME','df.oms.offer_document','Default topic name for POSHOOFF documents');
    add_document_configuration_proc('0','IV','TOPIC_NAME','df.oms.invoice_documents','Default topic name for IV documents');

    -- Batch configuration id configurations ( footer id '0' it doesn't matter )
    add_document_configuration_proc('0','POSHOOFF','BATCH_CONFIG_ID','1709506625','Default thunderhead batch config id for POSHOOFF documents');
    add_document_configuration_proc('0','IV','BATCH_CONFIG_ID','1709524441','Default thunderhead batch config id for IV documents');

    COMMIT;
END;
/
