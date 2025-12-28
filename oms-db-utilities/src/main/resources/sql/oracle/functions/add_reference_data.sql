--
-- File: add_reference_data.sql
-- Purpose: Helper function + convenience procedure to insert (or reuse) tbom_reference_data rows.
-- Summary:
--   * Function add_reference_data(...) returns the ID of an existing active row (refDataType+refDataValue) or inserts a new version.
--   * Supports effect-dated historization (new effect_from_dat creates a new version; triggers close previous version).
--   * Editable flag stored as 'Y'/'N'. Defaults: effect_from_dat=SYSTIMESTAMP, effect_to_dat=TO_TIMESTAMP('4712-12-31 23:59:59').
-- Usage Examples:
--   SELECT add_reference_data('DOCUMENT_STATUS','COMPLETED','N','Completed status') FROM dual;
--   -- New future-dated version:
--   SELECT add_reference_data('DOCUMENT_STATUS','COMPLETED','N','Future definition', TO_TIMESTAMP('2030-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS')) FROM dual;
-- Notes:
--   * Active version criteria: SYSTIMESTAMP BETWEEN effect_from_dat AND effect_to_dat
--   * A future-dated version (effect_from_dat > SYSTIMESTAMP) will still be inserted even if active version exists.
--   * Triggers manage ID, timestamps, and historization (closing previous versions).
--

CREATE OR REPLACE FUNCTION add_reference_data(
    p_type        IN tbom_reference_data.ref_data_type%TYPE,
    p_name        IN tbom_reference_data.ref_data_value%TYPE,
    p_editable    IN CHAR DEFAULT 'N',        -- 'Y' or 'N'
    p_description IN tbom_reference_data.description%TYPE DEFAULT NULL,
    p_effect_from IN TIMESTAMP DEFAULT NULL,
    p_effect_to   IN TIMESTAMP DEFAULT NULL
) RETURN NUMBER
IS
    v_id          tbom_reference_data.id%TYPE;
    v_effect_from TIMESTAMP := NVL(p_effect_from, SYSTIMESTAMP);
    v_effect_to   TIMESTAMP := NVL(p_effect_to, TO_TIMESTAMP('4712-12-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'));
BEGIN
    -- Try to find an active current version first (idempotent behavior)
    SELECT id INTO v_id
    FROM tbom_reference_data r
    WHERE r.ref_data_type = p_type
      AND r.ref_data_value = p_name
      AND SYSTIMESTAMP BETWEEN r.effect_from_dat AND r.effect_to_dat
    FETCH FIRST 1 ROWS ONLY;

    RETURN v_id; -- Return existing active version
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        -- Insert new version with explicit audit values
        SELECT sqomrda_ref_data_id.NEXTVAL INTO v_id FROM dual;
        INSERT INTO tbom_reference_data (
            id, ref_data_type, ref_data_value, editable, description,
            effect_from_dat, effect_to_dat,
            created_dat, last_update_dat, create_uid, last_update_uid
        ) VALUES (
            v_id, p_type, p_name, UPPER(SUBSTR(NVL(p_editable,'N'),1,1)), p_description,
            v_effect_from, v_effect_to,
            SYSTIMESTAMP, SYSTIMESTAMP, USER, USER
        );
        RETURN v_id;
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20031, 'add_reference_data failed for ' || p_type || '/' || p_name || ': ' || SQLERRM);
        RETURN NULL; -- satisfy function return path
END add_reference_data;
/

-- Convenience procedure variant (no return)
CREATE OR REPLACE PROCEDURE add_reference_data_proc(
    p_type        IN tbom_reference_data.ref_data_type%TYPE,
    p_name        IN tbom_reference_data.ref_data_value%TYPE,
    p_editable    IN CHAR DEFAULT 'N',
    p_description IN tbom_reference_data.description%TYPE DEFAULT NULL,
    p_effect_from IN TIMESTAMP DEFAULT NULL,
    p_effect_to   IN TIMESTAMP DEFAULT NULL
) IS
    v_dummy NUMBER;
BEGIN
    v_dummy := add_reference_data(p_type, p_name, p_editable, p_description, p_effect_from, p_effect_to);
END add_reference_data_proc;
/

-- Optional synonym grants (uncomment if exposing outside schema)
-- GRANT EXECUTE ON add_reference_data TO PUBLIC;
-- GRANT EXECUTE ON add_reference_data_proc TO PUBLIC;
