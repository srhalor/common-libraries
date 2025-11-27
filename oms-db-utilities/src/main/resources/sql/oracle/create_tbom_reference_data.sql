--
-- File: create_tbom_reference_data.sql
-- Purpose: Create and manage the tbom_reference_data table and supporting objects.
-- Summary: Defines the reference-data table (effective-dated rows), supporting indexes, a
--          sequence and triggers that assign keys, default timestamps/UIDs, prevent physical
--          deletes, and ensure non-overlapping historical versions by adjusting effect_to_dat
--          for previous rows.
-- Usage: Run once as part of schema provisioning. Active rows use a distant-future effect_to_dat;
--        perform logical deletes by setting effect_to_dat to the desired (past) date.
-- Changelog: 2025-10-22 - Initial version.
--

-- Drop table and related objects
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE tbom_reference_data CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

-- Drop sequence if exists
BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE sqomrda_ref_data_id';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN -- sequence does not exist
            RAISE;
        END IF;
END;
/

-- Create sequence for PK
CREATE SEQUENCE sqomrda_ref_data_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE tbom_reference_data
(
    id              NUMBER PRIMARY KEY,
    ref_data_type   VARCHAR2(50)        NOT NULL,
    ref_data_name   VARCHAR2(100)       NOT NULL,
    editable        CHAR(1) DEFAULT 'N' NOT NULL,
    description     VARCHAR2(255),
    effect_from_dat DATE                NOT NULL,
    effect_to_dat   DATE                NOT NULL,
    created_dat     TIMESTAMP           NOT NULL,
    last_update_dat TIMESTAMP           NOT NULL,
    create_uid      VARCHAR2(20)        NOT NULL,
    last_update_uid VARCHAR2(20)        NOT NULL
);

-- Add comments to columns
COMMENT ON TABLE tbom_reference_data IS 'Central table to store reference data values used by oms system (document types, document names, metadata keys, source systems, etc.).';
COMMENT ON COLUMN tbom_reference_data.id IS 'Primary Key for reference data.';
COMMENT ON COLUMN tbom_reference_data.ref_data_type IS 'Type of reference data (e.g. DOCUMENT_TYPE, DOCUMENT_NAME, METADATA_KEY, SOURCE_SYSTEM).';
COMMENT ON COLUMN tbom_reference_data.ref_data_name IS 'Name or value of the reference data (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).';
COMMENT ON COLUMN tbom_reference_data.ref_data_name IS 'Name or value of the reference data (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).';
COMMENT ON COLUMN tbom_reference_data.description IS 'Optional description for the reference data value.';
COMMENT ON COLUMN tbom_reference_data.effect_from_dat IS 'Date from which this reference is effective.';
COMMENT ON COLUMN tbom_reference_data.effect_to_dat IS 'Date till which this reference is effective.';
COMMENT ON COLUMN tbom_reference_data.created_dat IS 'Record creation timestamp.';
COMMENT ON COLUMN tbom_reference_data.last_update_dat IS 'Record last update timestamp.';
COMMENT ON COLUMN tbom_reference_data.create_uid IS 'User ID who created the record.';
COMMENT ON COLUMN tbom_reference_data.last_update_uid IS 'User ID who last updated the record.';

-- Create indexes
CREATE INDEX omrda_01 ON tbom_reference_data (ref_data_type);
CREATE INDEX omrda_02 ON tbom_reference_data (ref_data_name);
CREATE INDEX omrda_03 ON tbom_reference_data (effect_from_dat);
CREATE INDEX omrda_04 ON tbom_reference_data (effect_to_dat);

-- Compound trigger to set PK, timestamps, user ids and implement logical historization on insert/update
CREATE OR REPLACE TRIGGER omrda_01t_bir_bur
    FOR INSERT OR UPDATE
    ON tbom_reference_data
    COMPOUND TRIGGER
    TYPE t_key_rec IS RECORD
                      (
                          ref_type tbom_reference_data.ref_data_type%TYPE,
                          ref_name tbom_reference_data.ref_data_name%TYPE,
                          rec_id   tbom_reference_data.id%TYPE,
                          eff_from tbom_reference_data.effect_from_dat%TYPE
                      );

    TYPE t_key_tab IS TABLE OF t_key_rec;
    g_keys t_key_tab := t_key_tab();

BEFORE EACH ROW IS
BEGIN
    -- Ensure PK is set (use sequence on insert)
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT sqomrda_ref_data_id.NEXTVAL INTO :NEW.id FROM dual;
        END IF;
        -- default effect_from_dat to today if not provided
        IF :NEW.effect_from_dat IS NULL THEN
            :NEW.effect_from_dat := TRUNC(SYSDATE);
        END IF;
        -- default effect_to_dat to distant future if not provided
        IF :NEW.effect_to_dat IS NULL THEN
            :NEW.effect_to_dat := DATE '4712-12-31';
        END IF;
        -- created timestamp
        IF :NEW.created_dat IS NULL THEN
            :NEW.created_dat := SYSTIMESTAMP;
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

    -- collect keys for post-statement historization (only for insert/update)
    g_keys.EXTEND;
    g_keys(g_keys.COUNT).ref_type := :NEW.ref_data_type;
    g_keys(g_keys.COUNT).ref_name := :NEW.ref_data_name;
    g_keys(g_keys.COUNT).rec_id := :NEW.id;
    g_keys(g_keys.COUNT).eff_from := :NEW.effect_from_dat;
END BEFORE EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        -- For each inserted/updated row, close overlapping previous versions
        IF g_keys.COUNT > 0 THEN
            FOR i IN 1 .. g_keys.COUNT
                LOOP
                    UPDATE tbom_reference_data
                    SET effect_to_dat = g_keys(i).eff_from - (1 / 86400)
                    WHERE ref_data_type = g_keys(i).ref_type
                      AND ref_data_name = g_keys(i).ref_name
                      AND id <> g_keys(i).rec_id
                      AND effect_to_dat >= g_keys(i).eff_from;
                END LOOP;
        END IF;
    END AFTER STATEMENT;
    END omrda_01t_bir_bur;
/

-- Prevent physical deletes: raise error and instruct callers to perform a logical delete with UPDATE
CREATE OR REPLACE TRIGGER omrda_02_bdr
    BEFORE DELETE
    ON tbom_reference_data
    FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(-20021,
                            'Physical deletes are disabled on tbom_reference_data; perform a logical delete by updating effect_to_dat to SYSTIMESTAMP');
END omrda_02_bdr;
