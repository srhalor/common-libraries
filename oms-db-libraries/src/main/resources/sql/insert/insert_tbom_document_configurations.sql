-- Description: Script to insert default document configurations into tbom_document_configurations table.

-- Clear existing data so this script can be run multiple times safely
TRUNCATE TABLE tbom_document_configurations;

DECLARE
    -- Procedure to insert a document configuration by resolving reference ids once per call using the function.
    PROCEDURE insert_document_configurations(
        p_footer_name      IN VARCHAR2,
        p_app_doc_spec     IN VARCHAR2,
        p_code_name        IN VARCHAR2,
        p_value            IN VARCHAR2,
        p_description      IN VARCHAR2 := NULL,
        p_effect_from_dat  IN DATE     := DATE '2020-01-01',
        p_effect_to_dat    IN DATE     := DATE '4712-12-31'
    ) IS
        v_footer_id NUMBER;
        v_app_id    NUMBER;
        v_code_id   NUMBER;
    BEGIN
        -- Resolve reference IDs using shared function (as-of today)
        v_footer_id := get_reference_id('FOOTER_ID',       p_footer_name);
        v_app_id    := get_reference_id('APP_DOC_SPEC',    p_app_doc_spec);
        v_code_id   := get_reference_id('DOC_CONFIG_CODE', p_code_name);

        INSERT INTO tbom_document_configurations (
            omrda_footer_id,
            omrda_app_doc_spec_id,
            omrda_code_id,
            value,
            description,
            effect_from_dat,
            effect_to_dat
        )
        VALUES (
            v_footer_id,
            v_app_id,
            v_code_id,
            p_value,
            p_description,
            p_effect_from_dat,
            p_effect_to_dat
        );
    END insert_document_configurations;

BEGIN
    -- Signature configurations for default footer id '0' and all documents
    insert_document_configurations('0', '*', 'SIGNEE_1', 'GBMHEN1', 'Default signee 1 for all documents');
    insert_document_configurations('0', '*', 'SIGNEE_2', 'DEATES1', 'Default signee 2 for all documents');

    -- Signature configurations for default footer id '0' and documents starting with POSHOOFF
    insert_document_configurations('0', 'POSHOOFF', 'SIGNEE_1', 'EMPTY', 'Default signee 1 for documents starting with IV');
    insert_document_configurations('0', 'POSHOOFF', 'SIGNEE_2', 'ESMNOD1', 'Default signee 2 for documents starting with IV');

    -- Signature configurations for default footer id '0' and documents starting with IV
    insert_document_configurations('0', 'IV', 'SIGNEE_1', 'GBMHEN1', 'Default signee 1 for documents starting with IV');
    insert_document_configurations('0', 'IV', 'SIGNEE_2', 'DEATES1', 'Default signee 2 for documents starting with IV');

    -- Signature configurations for footer id '2' and documents starting with POSHOOFF
    insert_document_configurations('2', 'POSHOOFF', 'SIGNEE_1', 'NLTKAA1', 'Default signee 1 for documents starting with IV');
    insert_document_configurations('2', 'POSHOOFF', 'SIGNEE_2', 'ESMNOD1', 'Default signee 2 for documents starting with IV');

    -- Signature configurations for footer id '2' and documents starting with IV
    insert_document_configurations('2', 'IV', 'SIGNEE_1', 'ESMNOD1', 'Signee 1 for documents starting with IV and footer id 1');
    insert_document_configurations('2', 'IV', 'SIGNEE_2', 'DEATES1', 'Signee 2 for documents starting with IV and footer id 1');

    -- Topic name configurations ( footer id '0' it doesn't matter )
    insert_document_configurations('0', 'POSHOOFF', 'TOPIC_NAME', 'df.oms.offer_document', 'Default topic name for ShortOffer documents');
    insert_document_configurations('0', 'IV', 'TOPIC_NAME', 'df.oms.invoice_documents','Default topic name for documents starting with IV');

    -- Batch configuration id configurations ( footer id '0' it doesn't matter )
    insert_document_configurations('0', 'POSHOOFF', 'BATCH_CONFIG_ID', '1709506625', 'Default thunderhead batch config id for all documents');
    insert_document_configurations('0', 'IV', 'BATCH_CONFIG_ID', '1709524441', 'Thunderhead batch config id for documents starting with IV');

    COMMIT;
END;
/
