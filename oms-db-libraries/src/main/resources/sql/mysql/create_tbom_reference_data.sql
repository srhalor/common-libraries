--
-- File: create_tbom_reference_data.sql
-- Purpose: Create and manage the tbom_reference_data table and supporting objects.
-- Summary: Defines the reference-data table (effective-dated rows), supporting indexes and triggers
--          that default timestamps/UIDs, prevent physical deletes, and keep non-overlapping
--          historical versions by adjusting effect_to_dat for previous rows.
-- Usage: Run once as part of schema provisioning. Active rows use a distant-future effect_to_dat;
--        perform logical deletes by setting effect_to_dat to the desired (past) date.
-- Changelog: 2025-10-22 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_reference_data;

CREATE TABLE tbom_reference_data
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary Key for reference data.',
    ref_data_type   VARCHAR(50)  NOT NULL COMMENT 'Type of reference data (e.g. DOCUMENT_TYPE, DOCUMENT_NAME, METADATA_KEY, SOURCE_SYSTEM).',
    ref_data_name   VARCHAR(100) NOT NULL COMMENT 'Name or value of the reference data (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).',
    description     VARCHAR(255) COMMENT 'Optional description for the reference data value.',
    effect_from_dat DATE         NOT NULL COMMENT 'Date from which this reference is effective.',
    effect_to_dat   DATE         NOT NULL COMMENT 'Date till which this reference is effective .',
    created_dat     DATETIME     NOT NULL COMMENT 'Record creation timestamp.',
    last_update_dat DATETIME     NOT NULL COMMENT 'Record last update timestamp.',
    create_uid      VARCHAR(20)  NOT NULL COMMENT 'User ID who created the record.',
    last_update_uid VARCHAR(20)  NOT NULL COMMENT 'User ID who last updated the record.'
) COMMENT ='Central table to store reference data values used by oms system (document types, document names, metadata keys, source systems, etc.).';

-- Create indexes
CREATE INDEX omrda_01 ON tbom_reference_data (ref_data_type);
CREATE INDEX omrda_02 ON tbom_reference_data (ref_data_name);
CREATE INDEX omrda_03 ON tbom_reference_data (effect_from_dat);
CREATE INDEX omrda_04 ON tbom_reference_data (effect_to_dat);

-- Trigger to set created_dat, last_update_dat, create_uid and last_update_uid before insert
DELIMITER $$
CREATE TRIGGER omrda_01t_bir
    BEFORE INSERT
    ON tbom_reference_data
    FOR EACH ROW
BEGIN
    -- default effect dates
    IF NEW.effect_from_dat IS NULL THEN
        SET NEW.effect_from_dat = CURDATE();
    END IF;
    IF NEW.effect_to_dat IS NULL OR NEW.effect_to_dat = '0000-00-00' THEN
        SET NEW.effect_to_dat = '4712-12-31';
    END IF;

    IF NEW.created_dat IS NULL THEN
        SET NEW.created_dat = NOW();
    END IF;
    SET NEW.last_update_dat = NOW();
    IF NEW.create_uid IS NULL OR NEW.create_uid = '' THEN
        SET NEW.create_uid = CURRENT_USER();
    END IF;
    IF NEW.last_update_uid IS NULL OR NEW.last_update_uid = '' THEN
        SET NEW.last_update_uid = CURRENT_USER();
    END IF;
END$$

-- Trigger to set last_update_dat and last_update_uid before update
CREATE TRIGGER omrda_02t_bur
    BEFORE UPDATE
    ON tbom_reference_data
    FOR EACH ROW
BEGIN
    SET NEW.last_update_dat = NOW();
    IF NEW.last_update_uid IS NULL OR NEW.last_update_uid = '' THEN
        SET NEW.last_update_uid = CURRENT_USER();
    END IF;
END$$

-- AFTER INSERT: close overlapping previous versions (set effect_to_dat to new.effect_from_dat - 1 second)
CREATE TRIGGER omrda_03t_air
    AFTER INSERT
    ON tbom_reference_data
    FOR EACH ROW
BEGIN
    -- close previous active versions for same ref type/name
    UPDATE tbom_reference_data
    SET effect_to_dat = DATE_SUB(NEW.effect_from_dat, INTERVAL 1 SECOND)
    WHERE ref_data_type = NEW.ref_data_type
      AND ref_data_name = NEW.ref_data_name
      AND id <> NEW.id
      AND effect_to_dat >= NEW.effect_from_dat;
END$$

-- AFTER UPDATE: ensure no overlapping versions remain (if effect_from_dat changed)
CREATE TRIGGER omrda_04t_aur
    AFTER UPDATE
    ON tbom_reference_data
    FOR EACH ROW
BEGIN
    -- Only act when both old and new effect_from_dat are non-null and the value changed
    IF OLD.effect_from_dat IS NOT NULL
        AND NEW.effect_from_dat IS NOT NULL
        AND NEW.effect_from_dat <> OLD.effect_from_dat THEN
        UPDATE tbom_reference_data
        SET effect_to_dat = DATE_SUB(NEW.effect_from_dat, INTERVAL 1 SECOND)
        WHERE ref_data_type = NEW.ref_data_type
          AND ref_data_name = NEW.ref_data_name
          AND id <> NEW.id
          AND effect_to_dat >= NEW.effect_from_dat;
    END IF;
END$$
DELIMITER ;

-- BEFORE DELETE: block physical deletes and instruct callers to perform logical delete via UPDATE
DELIMITER $$
CREATE TRIGGER omrda_05t_bdr
    BEFORE DELETE
    ON tbom_reference_data
    FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
            'Physical deletes are disabled on tbom_reference_data; perform a logical delete by updating effect_to_dat to a system date';
END$$
DELIMITER ;
