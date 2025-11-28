--
-- File: get_reference_id.sql
-- Purpose: Utility function to resolve the active tbom_reference_data.id for a given (refDataType, refDataValue)
-- Summary: Returns the most recent effective row as of a given date (default: today). Optionally raises
--          a clear error if no row is found.
-- Usage: SELECT get_reference_id('DOCUMENT_TYPE','IV') FROM dual;
--        v_id := get_reference_id('SOURCE_SYSTEM','IVZRECPA', DATE '2024-01-01');
-- Changelog:
--   2025-10-22 - Initial version.
--

CREATE OR REPLACE FUNCTION get_reference_id(
    p_ref_data_type IN VARCHAR2,
    p_ref_data_value IN VARCHAR2,
    p_asof_date IN DATE DEFAULT TRUNC(SYSDATE),
    p_required IN NUMBER DEFAULT 1
) RETURN NUMBER
    IS
    v_id NUMBER;
BEGIN
    SELECT id
    INTO v_id
    FROM (SELECT id
          FROM tbom_reference_data
          WHERE ref_data_type = p_ref_data_type
            AND ref_data_value = p_ref_data_value
            AND effect_from_dat <= TRUNC(p_asof_date)
            AND effect_to_dat >= TRUNC(p_asof_date)
          ORDER BY effect_from_dat DESC)
    WHERE ROWNUM = 1;

    RETURN v_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        IF p_required = 1 THEN
            RAISE_APPLICATION_ERROR(-20002,
                                    'Reference not found for ' || p_ref_data_type || ' / ' || p_ref_data_value);
        END IF;
        RETURN NULL;
    WHEN OTHERS THEN
        IF p_required = 1 THEN
            RAISE; -- propagate unexpected errors when required
        END IF;
        RETURN NULL;
END get_reference_id;
/
