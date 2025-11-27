-- Insert script for tbom_document_requests and related tables (Oracle)
-- Generates 20 document requests with:
--   - 1:1 tbom_document_requests_blob row
--   - 4–8 tbom_requests_metadata_values rows (mandatory: REQUEST_CORRELATION_ID, SOURCE_ENVIRONMENT, LANGUAGE_CODE)
--   - 1–4 tbom_th_batches rows per request
--   - 1–4 tbom_error_details rows per batch
-- Uses get_reference_id(ref_data_type, ref_data_name) to resolve FK ids.
-- Dependencies: tbom_reference_data (with DOCUMENT_TYPE, DOCUMENT_NAME, SOURCE_SYSTEM,
--              DOCUMENT_STATUS, BATCH_STATUS, METADATA_KEY), get_reference_id,
--              tbom_document_requests, tbom_document_requests_blob,
--              tbom_requests_metadata_values, tbom_th_batches, tbom_error_details.

DECLARE
    -- Simple VARRAY type for picking random values
    TYPE t_vc_arr IS VARRAY(20) OF VARCHAR2(100);

    v_source_systems   t_vc_arr := t_vc_arr('CIBT', 'ARCADE');
    v_doc_types        t_vc_arr := t_vc_arr('POLICY', 'INVOICE');
    v_policy_names     t_vc_arr := t_vc_arr('POSHOOFF');
    v_invoice_names    t_vc_arr := t_vc_arr('IVZRECPA', 'IVBRKCOM', 'IVCLFCAP');
    v_doc_statuses     t_vc_arr := t_vc_arr('NEW', 'PROCESSING', 'IN_THUNDERHEAD', 'STOPPED', 'FAILED', 'COMPLETED');
    v_batch_statuses   t_vc_arr := t_vc_arr('SUBMITTED', 'PROCESSING', 'STOPPED', 'FAILED', 'COMPLETED');
    v_envs             t_vc_arr := t_vc_arr('DEV', 'SIT', 'UAT');
    v_langs            t_vc_arr := t_vc_arr('EN', 'ES', 'FR', 'DE', 'NL');

    -- Optional metadata keys beyond mandatory three
    v_other_meta_keys  t_vc_arr := t_vc_arr('FOOTER_ID', 'ATRADIUS_ORG_ID', 'CUSTOMER_ID', 'POLICY_ID', 'INVOICE_ID');
    v_error_categories t_vc_arr := t_vc_arr('VALIDATION_ERROR', 'TIMEOUT', 'INTERNAL_ERROR', 'MISSING_DATA');

    -- Move scalar declarations before local subprograms to avoid PLS-00103
    v_req_id           NUMBER;
    v_batch_id         NUMBER;
    v_source           VARCHAR2(50);
    v_type             VARCHAR2(50);
    v_name             VARCHAR2(50);
    v_status           VARCHAR2(50);
    v_json             CLOB;
    v_xml              CLOB;
    v_batch_status_name VARCHAR2(50);

    -- Helper: pick a random element from a varray
    FUNCTION pick(p_arr t_vc_arr) RETURN VARCHAR2 IS
    BEGIN
        RETURN p_arr(TRUNC(DBMS_RANDOM.VALUE(1, p_arr.COUNT + 1)));
    END;

    -- Helper to add a metadata row for a request
    PROCEDURE add_metadata(p_req_id NUMBER, p_key VARCHAR2, p_val VARCHAR2) IS
    BEGIN
        INSERT INTO tbom_requests_metadata_values (omdrt_id, omrda_id, metadata_value)
        VALUES (p_req_id, get_reference_id('METADATA_KEY', p_key), p_val);
    END;
BEGIN
    -- Generate 20 document requests with related data
    FOR i IN 1 .. 20
        LOOP
            v_source := pick(v_source_systems);
            v_type := pick(v_doc_types);
            IF v_type = 'POLICY' THEN
                v_name := pick(v_policy_names);
            ELSE
                v_name := pick(v_invoice_names);
            END IF;
            v_status := pick(v_doc_statuses);

            -- Insert document request (trigger will set PK/timestamps; we set creator explicitly for clarity)
            INSERT INTO tbom_document_requests (omrda_source_system_id, omrda_document_type_id, omrda_document_name_id,
                                                omrda_doc_status_id,
                                                create_uid_header, create_uid_token)
            VALUES (get_reference_id('SOURCE_SYSTEM', v_source),
                    get_reference_id('DOCUMENT_TYPE', v_type),
                    get_reference_id('DOCUMENT_NAME', v_name),
                    get_reference_id('DOCUMENT_STATUS', v_status),
                    'SEEDER', 'SEEDER')
            RETURNING id INTO v_req_id;

            -- 1:1 blob row (json required by schema, xml optional)
            v_json := '{"request_index":' || i || ',"source":"' || v_source || '","type":"' || v_type || '","name":"' ||
                      v_name || '"}';
            v_xml := '<request><index>' || i || '</index><source>' || v_source || '</source><type>' || v_type ||
                     '</type><name>' || v_name || '</name></request>';
            INSERT INTO tbom_document_requests_blob (omdrt_id, json_request, xml_request)
            VALUES (v_req_id, v_json, v_xml);

            -- Metadata values: mandatory + random until total is 4..8
            DECLARE
                TYPE t_used_keys IS TABLE OF VARCHAR2(100) INDEX BY VARCHAR2(100);
                v_used  t_used_keys;
                v_total NUMBER := TRUNC(DBMS_RANDOM.VALUE(4, 9)); -- inclusive 4..8
                v_added NUMBER := 0;
                v_key   VARCHAR2(100);
            BEGIN
                -- Mandatory: REQUEST_CORRELATION_ID
                add_metadata(v_req_id, 'REQUEST_CORRELATION_ID',
                             'REQ-' || TO_CHAR(i, 'FM0000') || '-' || TRUNC(DBMS_RANDOM.VALUE(1000, 10000)));
                v_used('REQUEST_CORRELATION_ID') := 'Y';
                v_added := v_added + 1;

                -- Mandatory: SOURCE_ENVIRONMENT
                v_key := 'SOURCE_ENVIRONMENT';
                add_metadata(v_req_id, v_key, pick(v_envs));
                v_used(v_key) := 'Y';
                v_added := v_added + 1;

                -- Mandatory: LANGUAGE_CODE
                v_key := 'LANGUAGE_CODE';
                add_metadata(v_req_id, v_key, pick(v_langs));
                v_used(v_key) := 'Y';
                v_added := v_added + 1;

                -- Add additional random metadata keys (no duplicates) until reaching v_total
                WHILE v_added < v_total
                    LOOP
                        v_key := pick(v_other_meta_keys);
                        IF NOT v_used.EXISTS(v_key) THEN
                            IF v_key = 'FOOTER_ID' THEN
                                -- Footer ids in reference data are '0'..'4'
                                add_metadata(v_req_id, v_key, TO_CHAR(TRUNC(DBMS_RANDOM.VALUE(0, 5))));
                            ELSE
                                add_metadata(v_req_id, v_key, TO_CHAR(TRUNC(DBMS_RANDOM.VALUE(1000, 999999))));
                            END IF;
                            v_used(v_key) := 'Y';
                            v_added := v_added + 1;
                        END IF;
                    END LOOP;
            END;

            -- 1..4 batches per request, each with 1..4 error details
            DECLARE
                v_batch_count NUMBER := TRUNC(DBMS_RANDOM.VALUE(1, 5)); -- 1..4
                v_err_count   NUMBER;
                v_cat         VARCHAR2(100);
            BEGIN
                FOR b IN 1 .. v_batch_count
                    LOOP
                    -- Precompute batch status name to avoid calling local function in SQL
                    v_batch_status_name := pick(v_batch_statuses);
                    INSERT INTO tbom_th_batches (omdrt_id, th_batch_id, omrda_th_status_id, batch_name,
                                                 sync_status, event_status,
                                                 create_uid_header, create_uid_token)
                    VALUES (v_req_id,
                            TRUNC(DBMS_RANDOM.VALUE(10000, 99999)),
                            get_reference_id('BATCH_STATUS', v_batch_status_name),
                            'BATCH-' || TO_CHAR(i, 'FM00') || '-' || TO_CHAR(b, 'FM00'),
                            'N', 'N',
                            'SEEDER', 'SEEDER')
                    RETURNING id INTO v_batch_id;

                    -- Create 1..4 error details for this batch
                    v_err_count := TRUNC(DBMS_RANDOM.VALUE(1, 5));
                    FOR e IN 1 .. v_err_count
                        LOOP
                            v_cat := pick(v_error_categories);
                            INSERT INTO tbom_error_details (omtbe_id, error_category, error_description)
                            VALUES (v_batch_id,
                                    v_cat,
                                    'Simulated ' || v_cat || ' for batch ' || TO_CHAR(i) || '-' || TO_CHAR(b) ||
                                    ' (err ' || TO_CHAR(e) || ')');
                        END LOOP;
                    END LOOP;
            END;
        END LOOP;

    COMMIT;
END;
/
