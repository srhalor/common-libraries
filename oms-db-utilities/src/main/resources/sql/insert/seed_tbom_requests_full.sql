--
-- File: seed_tbom_requests_full.sql
-- Purpose: Insert randomized sample data for 23 document requests and related tables:
--          tbom_document_requests, tbom_document_requests_blob, tbom_requests_metadata_values,
--          tbom_th_batches, tbom_error_details
-- Assumptions:
--   - Reference data exists (run insert_tbom_reference_data.sql first).
--   - get_reference_id(type,name[,date[,required]]) function exists in the schema.
-- Boolean conventions:
--   - sync_status, event_status stored as CHAR(1) with values 'Y'/'N'.
-- Notes:
--   - XML in blob is inserted only if at least one batch status for the request is not 'PROCESSING_OMS'.
--   - For tbom_th_batches: th_batch_id = 10000 when status in ('PROCESSING_OMS','FAILED_OMS'); otherwise random.
--   - event_status set randomly only for statuses in ('STOPPED_THUNDERHEAD','FAILED_OMS','FAILED_THUNDERHEAD','COMPLETED'); otherwise 'N'.
--   - retry_count random 0..5 only when status NOT IN ('PROCESSING_OMS','FAILED_OMS'); else 0.
--   - Error details created only for FAILED_OMS and FAILED_THUNDERHEAD.
--

DECLARE
    -- Arrays for random selection
    TYPE t_vc_arr IS VARRAY(20) OF VARCHAR2(100);

    v_source_systems t_vc_arr := t_vc_arr('CIBT', 'ARCADE');
    v_doc_types      t_vc_arr := t_vc_arr('POLICY', 'INVOICE');
    v_policy_names   t_vc_arr := t_vc_arr('POSHOOFF');
    v_invoice_names  t_vc_arr := t_vc_arr('IVZRECPA', 'IVBRKCOM', 'IVCLFCAP');
    -- Use DOCUMENT_STATUS values from insert_tbom_reference_data.sql
    v_doc_statuses   t_vc_arr := t_vc_arr('QUEUED', 'PROCESSING', 'STOPPED', 'FAILED', 'COMPLETED');

    -- Batch statuses (ensure existence as needed)
    v_batch_statuses t_vc_arr := t_vc_arr('PROCESSING_OMS', 'PROCESSING_THUNDERHEAD', 'STOPPED_THUNDERHEAD', 'FAILED_OMS', 'FAILED_THUNDERHEAD', 'COMPLETED');

    v_envs           t_vc_arr := t_vc_arr('DEV', 'SIT', 'UAT');
    v_langs          t_vc_arr := t_vc_arr('EN', 'ES', 'FR', 'DE', 'NL');

    v_opt_meta_keys  t_vc_arr := t_vc_arr('ATRADIUS_ORG_ID', 'CUSTOMER_ID', 'POLICY_ID', 'INVOICE_ID');

    v_err_categories t_vc_arr := t_vc_arr('VALIDATION_ERROR', 'TIMEOUT', 'INTERNAL_ERROR', 'MISSING_DATA');

    -- IDs and misc vars per request
    v_req_id           NUMBER;
    v_src              VARCHAR2(50);
    v_type             VARCHAR2(50);
    v_name             VARCHAR2(50);
    v_doc_status_name  VARCHAR2(50);
    v_json             CLOB;
    v_xml              CLOB;
    v_batch_count      NUMBER;
    v_has_non_proc_oms BOOLEAN;

    -- For batches
    v_batch_status_name VARCHAR2(50);
    v_batch_id          NUMBER;
    v_th_batch_id       NUMBER;
    v_event_status      CHAR(1);
    v_sync_status       CHAR(1);
    v_retry_count       NUMBER;
    v_dms_doc_id        NUMBER;
    v_env_val           VARCHAR2(50);
    v_lang_val          VARCHAR2(50);
    v_meta_id           NUMBER;
    v_err_id            NUMBER;

    -- Helpers
    FUNCTION pick(p_arr t_vc_arr) RETURN VARCHAR2 IS
    BEGIN
        RETURN p_arr(TRUNC(DBMS_RANDOM.VALUE(1, p_arr.COUNT + 1)));
    END;

    FUNCTION yn_rand RETURN CHAR IS
    BEGIN
        RETURN CASE TRUNC(DBMS_RANDOM.VALUE(0, 2)) WHEN 1 THEN 'Y' ELSE 'N' END;
    END;

    FUNCTION rnd(n_from NUMBER, n_to NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN TRUNC(DBMS_RANDOM.VALUE(n_from, n_to + 0.9999));
    END;

    -- Ensure a specific BATCH_STATUS reference exists; insert if missing
    PROCEDURE ensure_batch_status(p_name IN VARCHAR2) IS
        v_id NUMBER;
        v_new_id NUMBER;
    BEGIN
        -- Try resolve without raising error; if not found, insert minimal ref row (non-editable)
        v_id := get_reference_id('BATCH_STATUS', p_name, TRUNC(SYSDATE), 0);
        IF v_id IS NULL THEN
            SELECT sqomrda_ref_data_id.NEXTVAL INTO v_new_id FROM dual;
            INSERT INTO tbom_reference_data (
                id, ref_data_type, ref_data_name, editable, description,
                effect_from_dat, effect_to_dat,
                created_dat, last_update_dat, create_uid, last_update_uid
            ) VALUES (
                v_new_id,
                'BATCH_STATUS', p_name, 'N', 'Auto-seeded for sample data',
                TRUNC(SYSDATE), DATE '4712-12-31',
                SYSTIMESTAMP, SYSTIMESTAMP, USER, USER
            );
        END IF;
    END;

    -- Generic ensure for reference data types
    PROCEDURE ensure_ref(p_type IN VARCHAR2, p_name IN VARCHAR2, p_editable IN CHAR DEFAULT 'N') IS
        v_id NUMBER;
        v_new_id NUMBER;
    BEGIN
        v_id := get_reference_id(p_type, p_name, TRUNC(SYSDATE), 0);
        IF v_id IS NULL THEN
            SELECT sqomrda_ref_data_id.NEXTVAL INTO v_new_id FROM dual;
            INSERT INTO tbom_reference_data (
                id, ref_data_type, ref_data_name, editable, description,
                effect_from_dat, effect_to_dat,
                created_dat, last_update_dat, create_uid, last_update_uid
            ) VALUES (
                v_new_id,
                p_type, p_name, p_editable, 'Auto-seeded by seed script',
                TRUNC(SYSDATE), DATE '4712-12-31',
                SYSTIMESTAMP, SYSTIMESTAMP, USER, USER
            );
        END IF;
    END;


BEGIN
    -- Initialize random generator with a safe seed
    DBMS_RANDOM.SEED(ABS(TRUNC(DBMS_RANDOM.VALUE(1, 99999999))));

    -- Ensure special BATCH_STATUS values exist (if missing)
    FOR i IN 1 .. v_batch_statuses.COUNT LOOP
        ensure_batch_status(v_batch_statuses(i));
    END LOOP;

    -- Ensure all DOCUMENT_STATUS values used exist
    FOR i IN 1 .. v_doc_statuses.COUNT LOOP
        ensure_ref('DOCUMENT_STATUS', v_doc_statuses(i), 'N');
    END LOOP;
    -- Ensure base SOURCE_SYSTEM values
    FOR i IN 1 .. v_source_systems.COUNT LOOP
        ensure_ref('SOURCE_SYSTEM', v_source_systems(i), 'N');
    END LOOP;
    -- Ensure DOCUMENT_TYPE values
    FOR i IN 1 .. v_doc_types.COUNT LOOP
        ensure_ref('DOCUMENT_TYPE', v_doc_types(i), 'N');
    END LOOP;
    -- Ensure DOCUMENT_NAME values
    FOR i IN 1 .. v_policy_names.COUNT LOOP
        ensure_ref('DOCUMENT_NAME', v_policy_names(i), 'N');
    END LOOP;
    FOR i IN 1 .. v_invoice_names.COUNT LOOP
        ensure_ref('DOCUMENT_NAME', v_invoice_names(i), 'N');
    END LOOP;

    -- Generate 23 document requests
    FOR i IN 1 .. 23 LOOP
        v_src := pick(v_source_systems);
        v_type := pick(v_doc_types);
        IF v_type = 'POLICY' THEN
            v_name := pick(v_policy_names);
        ELSE
            v_name := pick(v_invoice_names);
        END IF;
        v_doc_status_name := pick(v_doc_statuses);

        -- tbom_document_requests: allocate ID and set audit timestamps explicitly
        SELECT sqomrda_doc_request_id.NEXTVAL INTO v_req_id FROM dual;
        INSERT INTO tbom_document_requests (
            id,
            omrda_source_system_id,
            omrda_document_type_id,
            omrda_document_name_id,
            omrda_doc_status_id,
            created_dat, last_update_dat,
            create_uid_header, create_uid_token
        ) VALUES (
            v_req_id,
            get_reference_id('SOURCE_SYSTEM',   v_src),
            get_reference_id('DOCUMENT_TYPE',   v_type),
            get_reference_id('DOCUMENT_NAME',   v_name),
            get_reference_id('DOCUMENT_STATUS', v_doc_status_name),
            SYSTIMESTAMP, SYSTIMESTAMP,
            'SEEDER', 'SEEDER'
        );

        -- Mandatory metadata values
        v_meta_id := sqomrme_metadata_value_id.NEXTVAL;
        INSERT INTO tbom_requests_metadata_values (id, omdrt_id, omrda_id, metadata_value)
        VALUES (v_meta_id, v_req_id, get_reference_id('METADATA_KEY','REQUEST_CORRELATION_ID'), 'REQ-' || TO_CHAR(i,'FM0000') || '-' || TO_CHAR(TRUNC(DBMS_RANDOM.VALUE(1000,9999))));
        -- Compute env and lang in PL/SQL, then insert
        v_env_val := pick(v_envs);
        v_lang_val := pick(v_langs);

        v_meta_id := sqomrme_metadata_value_id.NEXTVAL;
        INSERT INTO tbom_requests_metadata_values (id, omdrt_id, omrda_id, metadata_value)
        VALUES (v_meta_id, v_req_id, get_reference_id('METADATA_KEY','SOURCE_ENVIRONMENT'), v_env_val);

        v_meta_id := sqomrme_metadata_value_id.NEXTVAL;
        INSERT INTO tbom_requests_metadata_values (id, omdrt_id, omrda_id, metadata_value)
        VALUES (v_meta_id, v_req_id, get_reference_id('METADATA_KEY','LANGUAGE_CODE'), v_lang_val);

        v_meta_id := sqomrme_metadata_value_id.NEXTVAL;
        INSERT INTO tbom_requests_metadata_values (id, omdrt_id, omrda_id, metadata_value)
        VALUES (v_meta_id, v_req_id, get_reference_id('METADATA_KEY','FOOTER_ID'), TO_CHAR(TRUNC(DBMS_RANDOM.VALUE(0,4))));
        -- Optional metadata keys: add 0..4 without duplicates
        DECLARE
            TYPE t_keys IS TABLE OF VARCHAR2(100) INDEX BY VARCHAR2(100);
            v_used t_keys;
            v_extra NUMBER := rnd(0,4);
            v_added NUMBER := 0;
            v_key   VARCHAR2(100);
        BEGIN
            WHILE v_added < v_extra LOOP
                v_key := pick(v_opt_meta_keys);
                IF NOT v_used.EXISTS(v_key) THEN
                    v_meta_id := sqomrme_metadata_value_id.NEXTVAL;
                    INSERT INTO tbom_requests_metadata_values (id, omdrt_id, omrda_id, metadata_value)
                    VALUES (v_meta_id, v_req_id, get_reference_id('METADATA_KEY', v_key), TO_CHAR(TRUNC(DBMS_RANDOM.VALUE(1000,999999))));
                    v_used(v_key) := 'Y';
                    v_added := v_added + 1;
                END IF;
            END LOOP;
        END;

        -- Determine allowed batch statuses based on document status
        DECLARE
            TYPE t_bs_arr IS VARRAY(10) OF VARCHAR2(100);
            v_allowed t_bs_arr;
        BEGIN
            IF v_doc_status_name = 'QUEUED' THEN
                v_batch_count := 0; -- no batches
            ELSIF v_doc_status_name = 'PROCESSING' THEN
                v_allowed := t_bs_arr('PROCESSING_OMS', 'PROCESSING_THUNDERHEAD');
                v_batch_count := TRUNC(DBMS_RANDOM.VALUE(1,3)); -- 1..2 batches
            ELSIF v_doc_status_name = 'STOPPED' THEN
                v_allowed := t_bs_arr('STOPPED_THUNDERHEAD');
                v_batch_count := 1; -- exactly one batch
            ELSIF v_doc_status_name = 'FAILED' THEN
                v_allowed := t_bs_arr('FAILED_OMS', 'FAILED_THUNDERHEAD');
                v_batch_count := TRUNC(DBMS_RANDOM.VALUE(1,3)); -- 1..2 batches
            ELSIF v_doc_status_name = 'COMPLETED' THEN
                v_allowed := t_bs_arr('COMPLETED');
                v_batch_count := 1; -- exactly one batch
            ELSE
                v_allowed := t_bs_arr('PROCESSING_OMS');
                v_batch_count := TRUNC(DBMS_RANDOM.VALUE(1,2));
            END IF;

            v_has_non_proc_oms := FALSE;

            FOR b IN 1 .. v_batch_count LOOP
                -- Pick a status from allowed set
                v_batch_status_name := v_allowed(TRUNC(DBMS_RANDOM.VALUE(1, v_allowed.COUNT + 1)));

                -- th_batch_id rule
                IF v_batch_status_name IN ('PROCESSING_OMS','FAILED_OMS') THEN
                    v_th_batch_id := 10000;
                ELSE
                    v_th_batch_id := TRUNC(DBMS_RANDOM.VALUE(10001,99999));
                END IF;

                -- event/sync/retry rules
                IF v_batch_status_name IN ('STOPPED_THUNDERHEAD','FAILED_OMS','FAILED_THUNDERHEAD','COMPLETED') THEN
                    v_event_status := yn_rand();
                ELSE
                    v_event_status := 'N';
                END IF;

                IF v_batch_status_name IN ('PROCESSING_OMS','FAILED_OMS') THEN
                    v_sync_status := 'N';
                    v_retry_count := 0;
                ELSE
                    v_sync_status := 'Y';
                    v_retry_count := TRUNC(DBMS_RANDOM.VALUE(0,5));
                END IF;

                IF v_batch_status_name = 'COMPLETED' THEN
                    v_dms_doc_id := TRUNC(DBMS_RANDOM.VALUE(100000,999999));
                ELSE
                    v_dms_doc_id := NULL;
                END IF;

                IF v_batch_status_name <> 'PROCESSING_OMS' THEN
                    v_has_non_proc_oms := TRUE;
                END IF;

                -- allocate batch id and set audit explicitly
                SELECT sqomthb_th_batch_id.NEXTVAL INTO v_batch_id FROM dual;
                INSERT INTO tbom_th_batches (
                    id,
                    omdrt_id,
                    th_batch_id,
                    omrda_th_status_id,
                    batch_name,
                    dms_document_id,
                    sync_status,
                    event_status,
                    retry_count,
                    created_dat, last_update_dat, create_uid_header, create_uid_token
                ) VALUES (
                    v_batch_id,
                    v_req_id,
                    v_th_batch_id,
                    get_reference_id('BATCH_STATUS', v_batch_status_name),
                    'batch_' || TO_CHAR(v_req_id) || '_' || TO_CHAR(b),
                    v_dms_doc_id,
                    v_sync_status,
                    v_event_status,
                    v_retry_count,
                    SYSTIMESTAMP, SYSTIMESTAMP, 'SEEDER', 'SEEDER'
                );

                -- Error details for failed statuses
                IF v_batch_status_name IN ('FAILED_OMS','FAILED_THUNDERHEAD') THEN
                    DECLARE
                        v_errs NUMBER := TRUNC(DBMS_RANDOM.VALUE(1,3));
                        v_cat  VARCHAR2(100);
                    BEGIN
                        FOR e IN 1 .. v_errs LOOP
                            v_cat := pick(v_err_categories);
                            v_err_id := sqomedl_error_details_id.NEXTVAL;
                            INSERT INTO tbom_error_details (
                                id, omtbe_id, error_category, error_description
                            ) VALUES (
                                v_err_id,
                                v_batch_id,
                                v_cat,
                                'Simulated ' || v_cat || ' for request ' || TO_CHAR(v_req_id) || ' batch #' || TO_CHAR(b) || ' (err ' || TO_CHAR(e) || ')'
                            );
                        END LOOP;
                    END;
                END IF;
            END LOOP; -- batches per request
        END;

        -- Build JSON / XML blobs
        v_json := '{' ||
                  '"requestIndex":' || i || ',' ||
                  '"source":"' || v_src || '",' ||
                  '"type":"' || v_type || '",' ||
                  '"name":"' || v_name || '"' ||
                  '}';

        IF v_has_non_proc_oms THEN
            v_xml := '<request>' ||
                     '<index>' || i || '</index>' ||
                     '<source>' || v_src || '</source>' ||
                     '<type>' || v_type || '</type>' ||
                     '<name>' || v_name || '</name>' ||
                     '</request>';
        ELSE
            v_xml := NULL;
        END IF;

        INSERT INTO tbom_document_requests_blob (omdrt_id, json_request, xml_request)
        VALUES (v_req_id, v_json, v_xml);

    END LOOP; -- 23 requests

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
