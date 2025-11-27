--
-- File: add_document_configuration.sql
-- Purpose: Helper function + convenience procedure to insert (or reuse) an effective-dated
--          tbom_document_configurations row identified by (footer, app_doc_spec, code, value).
-- Summary:
--   * Function add_document_configuration(...) returns ID of active row if one exists for business key.
--   * If no active row or a future-dated version requested, inserts new version (triggers close overlaps).
--   * Effective dating: effect_from_dat defaults to TRUNC(SYSDATE); effect_to_dat defaults to DATE '4712-12-31'.
-- Usage Examples:
--   SELECT add_document_configuration('0','*','SIGNEE_1','GBMHEN1','Default signee 1') FROM dual;
--   SELECT add_document_configuration('0','IV','TOPIC_NAME','df.oms.invoice_documents','Invoice topic', DATE '2030-01-01') FROM dual;
-- Notes:
--   * Active version determined by SYSDATE BETWEEN effect_from_dat AND effect_to_dat.
--   * A future-dated effect_from_dat creates a new version even when an active one exists.
--   * Relies on get_reference_id(type,name) function to resolve foreign key IDs.
--   * Triggers manage ID, timestamps, historization.
--

CREATE OR REPLACE FUNCTION add_document_configuration(
    p_footer_name   IN VARCHAR2,
    p_app_doc_spec  IN VARCHAR2,
    p_code_name     IN VARCHAR2,
    p_value         IN VARCHAR2,
    p_description   IN VARCHAR2 DEFAULT NULL,
    p_effect_from   IN DATE DEFAULT NULL,
    p_effect_to     IN DATE DEFAULT NULL
) RETURN NUMBER
IS
    v_footer_id       NUMBER;
    v_app_doc_spec_id NUMBER;
    v_code_id         NUMBER;
    v_id              NUMBER;
    v_effect_from     DATE := NVL(p_effect_from, TRUNC(SYSDATE));
    v_effect_to       DATE := NVL(p_effect_to, DATE '4712-12-31');
BEGIN
    -- Resolve FK IDs via existing helper function
    v_footer_id       := get_reference_id('FOOTER_ID',       p_footer_name);
    v_app_doc_spec_id := get_reference_id('APP_DOC_SPEC',    p_app_doc_spec);
    v_code_id         := get_reference_id('DOC_CONFIG_CODE', p_code_name);

    -- Attempt to find an active existing configuration for the same business key & value
    SELECT id INTO v_id
    FROM tbom_document_configurations d
    WHERE d.omrda_footer_id       = v_footer_id
      AND d.omrda_app_doc_spec_id = v_app_doc_spec_id
      AND d.omrda_code_id         = v_code_id
      AND d.value                 = p_value
      AND SYSDATE BETWEEN d.effect_from_dat AND d.effect_to_dat
    FETCH FIRST 1 ROWS ONLY;

    RETURN v_id; -- existing active row
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        SELECT sqomdcn_doc_config_id.NEXTVAL INTO v_id FROM dual;
        INSERT INTO tbom_document_configurations (
            id,
            omrda_footer_id,
            omrda_app_doc_spec_id,
            omrda_code_id,
            value,
            description,
            effect_from_dat,
            effect_to_dat,
            created_dat, last_update_dat, create_uid, last_update_uid
        ) VALUES (
            v_id,
            v_footer_id,
            v_app_doc_spec_id,
            v_code_id,
            p_value,
            p_description,
            v_effect_from,
            v_effect_to,
            SYSTIMESTAMP, SYSTIMESTAMP, USER, USER
        );
        RETURN v_id;
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20032, 'add_document_configuration failed for footer=' || p_footer_name || ', spec=' || p_app_doc_spec || ', code=' || p_code_name || ': ' || SQLERRM);
        RETURN NULL; -- satisfy function return path
END add_document_configuration;
/

-- Convenience procedure variant
CREATE OR REPLACE PROCEDURE add_document_configuration_proc(
    p_footer_name   IN VARCHAR2,
    p_app_doc_spec  IN VARCHAR2,
    p_code_name     IN VARCHAR2,
    p_value         IN VARCHAR2,
    p_description   IN VARCHAR2 DEFAULT NULL,
    p_effect_from   IN DATE DEFAULT NULL,
    p_effect_to     IN DATE DEFAULT NULL
) IS
    v_dummy NUMBER;
BEGIN
    v_dummy := add_document_configuration(p_footer_name, p_app_doc_spec, p_code_name, p_value, p_description, p_effect_from, p_effect_to);
END add_document_configuration_proc;
/

-- Optional grants
-- GRANT EXECUTE ON add_document_configuration TO PUBLIC;
-- GRANT EXECUTE ON add_document_configuration_proc TO PUBLIC;
