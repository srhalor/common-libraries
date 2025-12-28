--
-- File: create_tbom_document_configurations.sql
-- Purpose: Create and manage the tbom_document_configurations table and supporting objects.
-- Summary: Defines an effective-dated document configuration table (keyed by footer/spec/code/value),
--          supporting indexes, a sequence and triggers that assign keys, default timestamps/UIDs,
--          prevent physical deletes, and ensure non-overlapping historical versions by adjusting
--          effect_to_dat for previous rows on insert/update.
-- Usage: Run once as part of schema provisioning. Active rows use a distant-future effect_to_dat;
--        perform logical deletes by setting effect_to_dat to the desired (past) date.
-- Changelog:
--   2025-10-22 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_document_configurations CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- Drop sequence if exists
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE sqomdcn_doc_config_id';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- sequence does not exist
            RAISE;
        END IF;
END;
/

-- Create sequence for PK
CREATE SEQUENCE sqomdcn_doc_config_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE tbom_document_configurations
(
    id                    NUMBER PRIMARY KEY,
    omrda_footer_id       NUMBER        NOT NULL,
    omrda_app_doc_spec_id NUMBER        NOT NULL,
    omrda_code_id         NUMBER        NOT NULL,
    value                 VARCHAR2(255) NOT NULL,
    description           VARCHAR2(255),
    effect_from_dat       TIMESTAMP     NOT NULL,
    effect_to_dat         TIMESTAMP     NOT NULL,
    created_dat           TIMESTAMP     NOT NULL,
    last_update_dat       TIMESTAMP     NOT NULL,
    create_uid            VARCHAR2(20)  NOT NULL,
    last_update_uid       VARCHAR2(20)  NOT NULL
);

-- Add comments to table and columns
COMMENT ON TABLE tbom_document_configurations IS 'Effective-dated document configuration key/value mappings. References tbom_reference_data for footer, application document specification, and configuration code.';
COMMENT ON COLUMN tbom_document_configurations.id IS 'Primary key for document configuration row.';
COMMENT ON COLUMN tbom_document_configurations.omrda_footer_id IS 'Foreign key to tbom_reference_data (FOOTER_ID) identifying the footer.';
COMMENT ON COLUMN tbom_document_configurations.omrda_app_doc_spec_id IS 'Foreign key to tbom_reference_data (APP_DOC_SPEC) identifying the application document specification.';
COMMENT ON COLUMN tbom_document_configurations.omrda_code_id IS 'Foreign key to tbom_reference_data (DOC_CONFIG_CODE) identifying the configuration code.';
COMMENT ON COLUMN tbom_document_configurations.value IS 'Configuration value associated with the DOC_CONFIG_CODE.';
COMMENT ON COLUMN tbom_document_configurations.description IS 'Optional description of the document configuration value.';
COMMENT ON COLUMN tbom_document_configurations.effect_from_dat IS 'Date/time from which this configuration row becomes effective.';
COMMENT ON COLUMN tbom_document_configurations.effect_to_dat IS 'Date/time until which this configuration row remains effective.';
COMMENT ON COLUMN tbom_document_configurations.created_dat IS 'Record creation timestamp.';
COMMENT ON COLUMN tbom_document_configurations.last_update_dat IS 'Record last update timestamp.';
COMMENT ON COLUMN tbom_document_configurations.create_uid IS 'User ID who created the record.';
COMMENT ON COLUMN tbom_document_configurations.last_update_uid IS 'User ID who last updated the record.';

-- Create indexes
CREATE INDEX omdcn_01 ON tbom_document_configurations (omrda_footer_id);
CREATE INDEX omdcn_02 ON tbom_document_configurations (omrda_app_doc_spec_id);
CREATE INDEX omdcn_03 ON tbom_document_configurations (omrda_code_id);
CREATE INDEX omdcn_04 ON tbom_document_configurations (effect_from_dat);
CREATE INDEX omdcn_05 ON tbom_document_configurations (effect_to_dat);
-- Composite index on business key to speed historization queries
CREATE INDEX omdcn_06 ON tbom_document_configurations (omrda_footer_id, omrda_app_doc_spec_id, omrda_code_id, value);

-- Add foreign key constraints
ALTER TABLE tbom_document_configurations
    ADD CONSTRAINT omrda_omdcn_fk1 FOREIGN KEY (omrda_footer_id)
        REFERENCES tbom_reference_data (id);
ALTER TABLE tbom_document_configurations
    ADD CONSTRAINT omrda_omdcn_fk2 FOREIGN KEY (omrda_app_doc_spec_id)
        REFERENCES tbom_reference_data (id);
ALTER TABLE tbom_document_configurations
    ADD CONSTRAINT omrda_omdcn_fk3 FOREIGN KEY (omrda_code_id)
        REFERENCES tbom_reference_data (id);

-- Compound trigger to set PK, timestamps, user ids and implement logical historization on insert/update
-- On UPDATE of data fields: closes current version and creates new version automatically
CREATE OR REPLACE TRIGGER omdcn_01t_bir_bur
    FOR INSERT OR UPDATE
    ON tbom_document_configurations
    COMPOUND TRIGGER

    -- Collection for INSERT operations (to close overlapping versions)
    TYPE t_key_rec IS RECORD
                      (
                          footer_id       tbom_document_configurations.omrda_footer_id%TYPE,
                          app_doc_spec_id tbom_document_configurations.omrda_app_doc_spec_id%TYPE,
                          code_id         tbom_document_configurations.omrda_code_id%TYPE,
                          cfg_value       tbom_document_configurations.value%TYPE,
                          rec_id          tbom_document_configurations.id%TYPE,
                          eff_from        tbom_document_configurations.effect_from_dat%TYPE
                      );
    TYPE t_key_tab IS TABLE OF t_key_rec;
    g_keys t_key_tab := t_key_tab();

    -- Collection for UPDATE operations (to create new versions)
    TYPE t_version_rec IS RECORD
                      (
                          old_id          tbom_document_configurations.id%TYPE,
                          footer_id       tbom_document_configurations.omrda_footer_id%TYPE,
                          app_doc_spec_id tbom_document_configurations.omrda_app_doc_spec_id%TYPE,
                          code_id         tbom_document_configurations.omrda_code_id%TYPE,
                          cfg_value       tbom_document_configurations.value%TYPE,
                          description     tbom_document_configurations.description%TYPE,
                          effect_from_dat tbom_document_configurations.effect_from_dat%TYPE,
                          effect_to_dat   tbom_document_configurations.effect_to_dat%TYPE,
                          create_uid      tbom_document_configurations.create_uid%TYPE,
                          last_update_uid tbom_document_configurations.last_update_uid%TYPE
                      );
    TYPE t_version_tab IS TABLE OF t_version_rec;
    g_versions t_version_tab := t_version_tab();

BEFORE EACH ROW IS
    v_data_changed BOOLEAN := FALSE;
BEGIN
    -- Ensure PK is set (use sequence on insert)
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT sqomdcn_doc_config_id.NEXTVAL INTO :NEW.id FROM dual;
        END IF;
        -- default effect_from_dat to current timestamp if not provided
        IF :NEW.effect_from_dat IS NULL THEN
            :NEW.effect_from_dat := SYSTIMESTAMP;
        END IF;
        -- default effect_to_dat to distant future if not provided
        IF :NEW.effect_to_dat IS NULL THEN
            :NEW.effect_to_dat := TO_TIMESTAMP('4712-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS');
        END IF;
        -- created timestamp
        IF :NEW.created_dat IS NULL THEN
            :NEW.created_dat := SYSTIMESTAMP;
        END IF;

        -- Collect for post-statement processing
        g_keys.EXTEND;
        g_keys(g_keys.COUNT).footer_id := :NEW.omrda_footer_id;
        g_keys(g_keys.COUNT).app_doc_spec_id := :NEW.omrda_app_doc_spec_id;
        g_keys(g_keys.COUNT).code_id := :NEW.omrda_code_id;
        g_keys(g_keys.COUNT).cfg_value := :NEW.value;
        g_keys(g_keys.COUNT).rec_id := :NEW.id;
        g_keys(g_keys.COUNT).eff_from := :NEW.effect_from_dat;
    END IF;

    -- For UPDATE operations: detect data changes and prepare versioning
    IF UPDATING THEN
        -- Check if any data fields changed
        -- Note: effect_to_dat is checked, but if it's the ONLY field changing to a past timestamp,
        -- it's treated as a logical delete (no versioning)
        v_data_changed := (
            :OLD.omrda_footer_id != :NEW.omrda_footer_id OR
            :OLD.omrda_app_doc_spec_id != :NEW.omrda_app_doc_spec_id OR
            :OLD.omrda_code_id != :NEW.omrda_code_id OR
            :OLD.value != :NEW.value OR
            NVL(:OLD.description, 'X') != NVL(:NEW.description, 'X') OR
            :OLD.effect_from_dat != :NEW.effect_from_dat OR
            (:OLD.effect_to_dat != :NEW.effect_to_dat AND :NEW.effect_to_dat >= SYSTIMESTAMP)
        );

        IF v_data_changed THEN
            -- Collect new version data for insertion in AFTER STATEMENT
            g_versions.EXTEND;
            g_versions(g_versions.COUNT).old_id := :OLD.id;
            g_versions(g_versions.COUNT).footer_id := :NEW.omrda_footer_id;
            g_versions(g_versions.COUNT).app_doc_spec_id := :NEW.omrda_app_doc_spec_id;
            g_versions(g_versions.COUNT).code_id := :NEW.omrda_code_id;
            g_versions(g_versions.COUNT).cfg_value := :NEW.value;
            g_versions(g_versions.COUNT).description := :NEW.description;
            g_versions(g_versions.COUNT).effect_from_dat := SYSTIMESTAMP;
            g_versions(g_versions.COUNT).effect_to_dat := :NEW.effect_to_dat;  -- Use the new effect_to_dat
            g_versions(g_versions.COUNT).create_uid := :OLD.create_uid;
            g_versions(g_versions.COUNT).last_update_uid := USER;

            -- Close current version (set effect_to_dat to 1 second before current timestamp)
            :NEW.effect_to_dat := SYSTIMESTAMP - INTERVAL '1' SECOND;

            -- Prevent further changes to other fields - only closing this version
            :NEW.omrda_footer_id := :OLD.omrda_footer_id;
            :NEW.omrda_app_doc_spec_id := :OLD.omrda_app_doc_spec_id;
            :NEW.omrda_code_id := :OLD.omrda_code_id;
            :NEW.value := :OLD.value;
            :NEW.description := :OLD.description;
            :NEW.effect_from_dat := :OLD.effect_from_dat;
        END IF;
    END IF;

    -- always set last update timestamp (and default uids when missing)
    :NEW.last_update_dat := SYSTIMESTAMP;
    IF :NEW.create_uid IS NULL THEN
        :NEW.create_uid := USER;
    END IF;
    IF :NEW.last_update_uid IS NULL THEN
        :NEW.last_update_uid := USER;
    END IF;
END BEFORE EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        -- For each inserted row, close overlapping previous versions
        -- A record is overlapping if: old.effect_to_dat >= new.effect_from_dat
        -- A record is NOT overlapping if: old.effect_to_dat < new.effect_from_dat
        IF g_keys.COUNT > 0 THEN
            FOR i IN 1 .. g_keys.COUNT
                LOOP
                    UPDATE tbom_document_configurations
                    SET effect_to_dat = g_keys(i).eff_from - INTERVAL '1' SECOND
                    WHERE omrda_footer_id = g_keys(i).footer_id
                      AND omrda_app_doc_spec_id = g_keys(i).app_doc_spec_id
                      AND omrda_code_id = g_keys(i).code_id
                      AND value = g_keys(i).cfg_value
                      AND id <> g_keys(i).rec_id
                      AND effect_to_dat >= g_keys(i).eff_from;  -- Only close if overlapping
                END LOOP;
        END IF;

        -- For each updated row with data changes, insert new version
        IF g_versions.COUNT > 0 THEN
            FOR i IN 1 .. g_versions.COUNT
                LOOP
                    INSERT INTO tbom_document_configurations (
                        omrda_footer_id,
                        omrda_app_doc_spec_id,
                        omrda_code_id,
                        value,
                        description,
                        effect_from_dat,
                        effect_to_dat,
                        created_dat,
                        last_update_dat,
                        create_uid,
                        last_update_uid
                    ) VALUES (
                        g_versions(i).footer_id,
                        g_versions(i).app_doc_spec_id,
                        g_versions(i).code_id,
                        g_versions(i).cfg_value,
                        g_versions(i).description,
                        g_versions(i).effect_from_dat,
                        g_versions(i).effect_to_dat,
                        SYSTIMESTAMP,
                        SYSTIMESTAMP,
                        g_versions(i).create_uid,
                        g_versions(i).last_update_uid
                    );
                END LOOP;
        END IF;
    END AFTER STATEMENT;
    END omdcn_01t_bir_bur;
/

-- Prevent physical deletes: raise error and instruct callers to perform a logical delete with UPDATE
CREATE OR REPLACE TRIGGER omdcn_02t_bdr
    BEFORE DELETE
    ON tbom_document_configurations
    FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(
            -20022,
            'Physical deletes are disabled on tbom_document_configurations; perform a logical delete by updating effect_to_dat to SYSTIMESTAMP'
    );
END omdcn_02t_bdr;
/
