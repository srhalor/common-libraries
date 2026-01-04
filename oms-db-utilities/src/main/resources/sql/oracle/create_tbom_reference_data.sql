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
    ref_data_value  VARCHAR2(100)       NOT NULL,
    editable        CHAR(1) DEFAULT 'N' NOT NULL,
    description     VARCHAR2(255),
    effect_from_dat TIMESTAMP           NOT NULL,
    effect_to_dat   TIMESTAMP           NOT NULL,
    created_dat     TIMESTAMP           NOT NULL,
    last_update_dat TIMESTAMP           NOT NULL,
    create_uid      VARCHAR2(20)        NOT NULL,
    last_update_uid VARCHAR2(20)        NOT NULL
);

-- Add comments to columns
COMMENT ON TABLE tbom_reference_data IS 'Central table to store reference data values used by oms system (document types, document names, metadata keys, source systems, etc.).';
COMMENT ON COLUMN tbom_reference_data.id IS 'Primary Key for reference data.';
COMMENT ON COLUMN tbom_reference_data.ref_data_type IS 'Type of reference data (e.g. DOCUMENT_TYPE, DOCUMENT_NAME, METADATA_KEY, SOURCE_SYSTEM).';
COMMENT ON COLUMN tbom_reference_data.ref_data_value IS 'Value for the reference data type (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).';
COMMENT ON COLUMN tbom_reference_data.editable IS 'Y/N flag indicating whether the reference data is editable or not.';
COMMENT ON COLUMN tbom_reference_data.description IS 'Optional description for the reference data metadataValue.';
COMMENT ON COLUMN tbom_reference_data.effect_from_dat IS 'Date from which this reference is effective.';
COMMENT ON COLUMN tbom_reference_data.effect_to_dat IS 'Date till which this reference is effective.';
COMMENT ON COLUMN tbom_reference_data.created_dat IS 'Record creation timestamp.';
COMMENT ON COLUMN tbom_reference_data.last_update_dat IS 'Record last update timestamp.';
COMMENT ON COLUMN tbom_reference_data.create_uid IS 'User ID who created the record.';
COMMENT ON COLUMN tbom_reference_data.last_update_uid IS 'User ID who last updated the record.';

-- Create indexes
CREATE INDEX omrda_01 ON tbom_reference_data (ref_data_type);
CREATE INDEX omrda_02 ON tbom_reference_data (ref_data_value);
CREATE INDEX omrda_03 ON tbom_reference_data (effect_from_dat);
CREATE INDEX omrda_04 ON tbom_reference_data (effect_to_dat);

-- Compound trigger to set PK, timestamps, user ids and implement logical historization on insert/update
-- On UPDATE of data fields: updates current row (keeping same ID) and creates historical snapshot with OLD values
CREATE OR REPLACE TRIGGER omrda_01t_bir_bur
    FOR INSERT OR UPDATE
    ON tbom_reference_data
    COMPOUND TRIGGER

    -- Collection for INSERT operations (to close overlapping versions)
    TYPE t_key_rec IS RECORD
                      (
                          ref_type tbom_reference_data.ref_data_type%TYPE,
                          ref_name tbom_reference_data.ref_data_value%TYPE,
                          rec_id   tbom_reference_data.id%TYPE,
                          eff_from tbom_reference_data.effect_from_dat%TYPE
                      );
    TYPE t_key_tab IS TABLE OF t_key_rec;
    g_keys t_key_tab := t_key_tab();

    -- Collection for UPDATE operations (to create historical records with OLD data)
    TYPE t_history_rec IS RECORD
                      (
                          ref_type        tbom_reference_data.ref_data_type%TYPE,
                          ref_name        tbom_reference_data.ref_data_value%TYPE,
                          editable        tbom_reference_data.editable%TYPE,
                          description     tbom_reference_data.description%TYPE,
                          effect_from_dat tbom_reference_data.effect_from_dat%TYPE,
                          effect_to_dat   tbom_reference_data.effect_to_dat%TYPE,
                          created_dat     tbom_reference_data.created_dat%TYPE,
                          create_uid      tbom_reference_data.create_uid%TYPE,
                          last_update_uid tbom_reference_data.last_update_uid%TYPE
                      );
    TYPE t_history_tab IS TABLE OF t_history_rec;
    g_history t_history_tab := t_history_tab();

BEFORE EACH ROW IS
    v_data_changed BOOLEAN := FALSE;
    v_update_time TIMESTAMP;
BEGIN
    -- Ensure PK is set (use sequence on insert)
    IF INSERTING THEN
        IF :NEW.id IS NULL THEN
            SELECT sqomrda_ref_data_id.NEXTVAL INTO :NEW.id FROM dual;
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
        g_keys(g_keys.COUNT).ref_type := :NEW.ref_data_type;
        g_keys(g_keys.COUNT).ref_name := :NEW.ref_data_value;
        g_keys(g_keys.COUNT).rec_id := :NEW.id;
        g_keys(g_keys.COUNT).eff_from := :NEW.effect_from_dat;
    END IF;

    -- For UPDATE operations: detect data changes and prepare historical record
    IF UPDATING THEN
        v_update_time := SYSTIMESTAMP;

        -- Check if any data fields changed
        v_data_changed := (
            :OLD.ref_data_type != :NEW.ref_data_type OR
            :OLD.ref_data_value != :NEW.ref_data_value OR
            NVL(:OLD.editable, 'X') != NVL(:NEW.editable, 'X') OR
            NVL(:OLD.description, 'X') != NVL(:NEW.description, 'X') OR
            (:OLD.effect_to_dat != :NEW.effect_to_dat AND :NEW.effect_to_dat >= SYSTIMESTAMP)
        );

        IF v_data_changed THEN
            -- Collect OLD data for historical record insertion in AFTER STATEMENT
            g_history.EXTEND;
            g_history(g_history.COUNT).ref_type := :OLD.ref_data_type;
            g_history(g_history.COUNT).ref_name := :OLD.ref_data_value;
            g_history(g_history.COUNT).editable := :OLD.editable;
            g_history(g_history.COUNT).description := :OLD.description;
            g_history(g_history.COUNT).effect_from_dat := :OLD.effect_from_dat;
            g_history(g_history.COUNT).effect_to_dat := v_update_time - INTERVAL '1' SECOND;
            g_history(g_history.COUNT).created_dat := :OLD.created_dat;
            g_history(g_history.COUNT).create_uid := :OLD.create_uid;
            g_history(g_history.COUNT).last_update_uid := USER;

            -- Update CURRENT row with NEW data (keep same ID)
            -- Set effect_from_dat to current timestamp
            :NEW.effect_from_dat := v_update_time;
            -- Ensure effect_to_dat is distant future if not explicitly set to a past date
            IF :NEW.effect_to_dat < v_update_time THEN
                :NEW.effect_to_dat := :NEW.effect_to_dat;  -- Keep the logical delete date
            ELSE
                :NEW.effect_to_dat := TO_TIMESTAMP('4712-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS');
            END IF;
        END IF;
    END IF;

    -- Always set last update timestamp (and default uids when missing)
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
                    UPDATE tbom_reference_data
                    SET effect_to_dat = g_keys(i).eff_from - INTERVAL '1' SECOND
                    WHERE ref_data_type = g_keys(i).ref_type
                      AND ref_data_value = g_keys(i).ref_name
                      AND id <> g_keys(i).rec_id
                      AND effect_to_dat >= g_keys(i).eff_from;  -- Only close if overlapping
                END LOOP;
        END IF;

        -- For each updated row with data changes, insert historical record with OLD values
        IF g_history.COUNT > 0 THEN
            FOR i IN 1 .. g_history.COUNT
                LOOP
                    INSERT INTO tbom_reference_data (
                        ref_data_type,
                        ref_data_value,
                        editable,
                        description,
                        effect_from_dat,
                        effect_to_dat,
                        created_dat,
                        last_update_dat,
                        create_uid,
                        last_update_uid
                    ) VALUES (
                        g_history(i).ref_type,
                        g_history(i).ref_name,
                        g_history(i).editable,
                        g_history(i).description,
                        g_history(i).effect_from_dat,
                        g_history(i).effect_to_dat,
                        g_history(i).created_dat,
                        SYSTIMESTAMP,
                        g_history(i).create_uid,
                        g_history(i).last_update_uid
                    );
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
