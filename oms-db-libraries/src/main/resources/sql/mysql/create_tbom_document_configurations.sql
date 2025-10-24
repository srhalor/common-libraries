--
-- File: create_tbom_document_configurations.sql
-- Purpose: Create and manage the tbom_document_configurations table and supporting objects.
-- Summary: Defines an effective-dated document configuration table (keyed by footer/spec/code/value),
--          supporting indexes and triggers that default timestamps/UIDs, prevent physical deletes,
--          and ensure non-overlapping historical versions by adjusting effect_to_dat for previous
--          rows on insert/update.
-- Usage: Run once as part of schema provisioning. Active rows use a distant-future effect_to_dat;
--        perform logical deletes by setting effect_to_dat to the desired (past) date.
-- Changelog:
--   2025-10-22 - Initial version.
--

-- Drop table and related objects
DROP TABLE IF EXISTS tbom_document_configurations;

CREATE TABLE tbom_document_configurations
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key for document configuration row.',
    omrda_footer_id       BIGINT       NOT NULL COMMENT 'Foreign key to tbom_reference_data (FOOTER_ID) identifying the footer.',
    omrda_app_doc_spec_id BIGINT       NOT NULL COMMENT 'Foreign key to tbom_reference_data (APP_DOC_SPEC) identifying the application document specification.',
    omrda_code_id         BIGINT       NOT NULL COMMENT 'Foreign key to tbom_reference_data (DOC_CONFIG_CODE) identifying the configuration code.',
    value                 VARCHAR(255) NOT NULL COMMENT 'Configuration value associated with the DOC_CONFIG_CODE.',
    description           VARCHAR(255) COMMENT 'Optional description of the document configuration value.',
    effect_from_dat       DATE         NOT NULL COMMENT 'Date from which this configuration row becomes effective.',
    effect_to_dat         DATE         NOT NULL COMMENT 'Date until which this configuration row remains effective.',
    created_dat           DATETIME     NOT NULL COMMENT 'Record creation timestamp.',
    last_update_dat       DATETIME     NOT NULL COMMENT 'Record last update timestamp.',
    create_uid            VARCHAR(20)  NOT NULL COMMENT 'User ID who created the record.',
    last_update_uid       VARCHAR(20)  NOT NULL COMMENT 'User ID who last updated the record.',
    CONSTRAINT omrda_omdcn_fk1 FOREIGN KEY (omrda_footer_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT omrda_omdcn_fk2 FOREIGN KEY (omrda_app_doc_spec_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT omrda_omdcn_fk3 FOREIGN KEY (omrda_code_id)
        REFERENCES tbom_reference_data (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT ='Effective-dated document configuration key/value mappings. References tbom_reference_data for footer, application document specification, and configuration code.';

-- Create indexes
CREATE INDEX omdcn_01 ON tbom_document_configurations (omrda_footer_id);
CREATE INDEX omdcn_02 ON tbom_document_configurations (omrda_app_doc_spec_id);
CREATE INDEX omdcn_03 ON tbom_document_configurations (omrda_code_id);
CREATE INDEX omdcn_04 ON tbom_document_configurations (effect_from_dat);
CREATE INDEX omdcn_05 ON tbom_document_configurations (effect_to_dat);
-- Composite index on business key to speed historization queries
CREATE INDEX omdcn_06 ON tbom_document_configurations (omrda_footer_id, omrda_app_doc_spec_id, omrda_code_id, value);

-- Triggers: default timestamps/UIDs and historization
DELIMITER $$
-- BEFORE INSERT: default effect dates, timestamps, and user IDs
CREATE TRIGGER omdcn_01t_bir
    BEFORE INSERT
    ON tbom_document_configurations
    FOR EACH ROW
BEGIN
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

-- BEFORE UPDATE: update last_update_dat and default last_update_uid
CREATE TRIGGER omdcn_02t_bur
    BEFORE UPDATE
    ON tbom_document_configurations
    FOR EACH ROW
BEGIN
    SET NEW.last_update_dat = NOW();
    IF NEW.last_update_uid IS NULL OR NEW.last_update_uid = '' THEN
        SET NEW.last_update_uid = CURRENT_USER();
    END IF;
END$$

-- AFTER INSERT: close overlapping previous versions for the same business key
CREATE TRIGGER omdcn_03t_air
    AFTER INSERT
    ON tbom_document_configurations
    FOR EACH ROW
BEGIN
    UPDATE tbom_document_configurations
    SET effect_to_dat = DATE_SUB(NEW.effect_from_dat, INTERVAL 1 SECOND)
    WHERE omrda_footer_id = NEW.omrda_footer_id
      AND omrda_app_doc_spec_id = NEW.omrda_app_doc_spec_id
      AND omrda_code_id = NEW.omrda_code_id
      AND value = NEW.value
      AND id <> NEW.id
      AND effect_to_dat >= NEW.effect_from_dat;
END$$

-- AFTER UPDATE: ensure no overlapping versions remain (if effect_from_dat changed)
CREATE TRIGGER omdcn_04t_aur
    AFTER UPDATE
    ON tbom_document_configurations
    FOR EACH ROW
BEGIN
    IF OLD.effect_from_dat IS NOT NULL
        AND NEW.effect_from_dat IS NOT NULL
        AND NEW.effect_from_dat <> OLD.effect_from_dat THEN
        UPDATE tbom_document_configurations
        SET effect_to_dat = DATE_SUB(NEW.effect_from_dat, INTERVAL 1 SECOND)
        WHERE omrda_footer_id = NEW.omrda_footer_id
          AND omrda_app_doc_spec_id = NEW.omrda_app_doc_spec_id
          AND omrda_code_id = NEW.omrda_code_id
          AND value = NEW.value
          AND id <> NEW.id
          AND effect_to_dat >= NEW.effect_from_dat;
    END IF;
END$$

-- BEFORE DELETE: block physical deletes and instruct callers to perform logical delete via UPDATE
CREATE TRIGGER omdcn_05t_bdr
    BEFORE DELETE
    ON tbom_document_configurations
    FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT =
            'Physical deletes are disabled on tbom_document_configurations; perform a logical delete by updating effect_to_dat to a system date';
END$$
DELIMITER ;
