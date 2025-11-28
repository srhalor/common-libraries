# OMS Database Design Document

## Overview
This document details the database schema for the OMS (Output Management System) system. The schema is designed to efficiently manage document requests, reference data, batch processing, and associated metadata. The design emphasizes normalization, extensibility, auditability, and data integrity, supporting a wide range of document and batch processing scenarios.

## Entity Descriptions
Below are the main entities (tables) in the schema, along with their purposes, key fields, relationships, and design considerations.

### tbom_reference_data
- **Purpose:**
  - Central reference-data table used by OMS for document types, document names, metadata keys, source systems, footer ids, doc config codes, etc.
- **Physical schema:**
  - `id` NUMBER PRIMARY KEY (populated from sequence `sqomrda_ref_data_id`) — Primary Key for reference data.
  - `ref_data_type` VARCHAR2(50) NOT NULL — Type of reference data (e.g. DOCUMENT_TYPE, DOCUMENT_NAME, METADATA_KEY, SOURCE_SYSTEM).
  - `ref_data_value` VARCHAR2(100) NOT NULL — Name or value of the reference data (e.g. Invoice, IVZRECPA, 103, IV, MetadataKey1).
  - `description` VARCHAR2(255) — Optional description for the reference data value.
  - `effect_from_dat` DATE NOT NULL — Date from which this reference is effective.
  - `effect_to_dat` DATE NOT NULL — Date till which this reference is effective.
  - `created_dat` TIMESTAMP NOT NULL — Record creation timestamp.
  - `last_update_dat` TIMESTAMP NOT NULL — Record last update timestamp.
  - `create_uid` VARCHAR2(20) NOT NULL — User ID who created the record.
  - `last_update_uid` VARCHAR2(20) NOT NULL — User ID who last updated the record.
- **Indexes:**
  - Indexes on `ref_data_type`, `ref_data_value`, `effect_from_dat`, `effect_to_dat` for lookups and range scans (created by `omrda_01`..`omrda_04`).
- **Sequence:**
  - `sqomrda_ref_data_id` used to assign primary keys on insert when `id` is not provided.
- **Triggers & behavior:**
  - Compound trigger `omrda_01t_bir_bur` (BEFORE EACH ROW + AFTER STATEMENT):
    - On INSERT: assigns `id` from `sqomrda_ref_data_id` if missing; defaults `effect_from_dat` to `TRUNC(SYSDATE)` and `effect_to_dat` to `DATE '4712-12-31'` when not provided; sets `created_dat` when missing and always sets `last_update_dat` to `SYSTIMESTAMP`; defaults `create_uid`/`last_update_uid` to `USER` when missing.
    - The trigger collects changed rows into a temporary collection and, AFTER STATEMENT, closes overlapping previous versions for the same `(ref_data_type, ref_data_value)` by setting their `effect_to_dat` to one second before the new `effect_from_dat` (implemented as `new.effect_from_dat - 1/86400`).
  - Prevent-delete trigger `omrda_02_bdr`: raises an application error (`-20021`) to block physical deletes; logical deletes should be done by updating `effect_to_dat`.
- **Historization rule:**
  - New or updated rows that set or change `effect_from_dat` will cause prior rows with overlapping validity for the same `(ref_data_type, ref_data_value)` to be closed (their `effect_to_dat` set to one second before the new `effect_from_dat`).

- **Sample Data:** `SELECT * FROM tbom_reference_data;`

| id | ref_data_type   | ref_data_value          | description                                                | effect_from_dat | effect_to_dat | created_dat                  | last_update_dat              | create_uid | last_update_uid |
|---:|-----------------|------------------------|------------------------------------------------------------|----------------:|---------------|------------------------------|------------------------------|------------|-----------------|
|  1 | REF_DATA_TYPE   | DOCUMENT_TYPE          | "Type of document (e.g. INVOICE, POLICY)"                  |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.835348` | `2025-10-22 16:35:50.835357` | OMSUSER    | OMSUSER         |
|  2 | REF_DATA_TYPE   | DOCUMENT_NAME          | "Specific document names (e.g. IVZRECPA, POSHOOFF)"        |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.848406` | `2025-10-22 16:35:50.848414` | OMSUSER    | OMSUSER         |
|  3 | REF_DATA_TYPE   | METADATA_KEY           | Keys for metadata associated with documents                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.848724` | `2025-10-22 16:35:50.848727` | OMSUSER    | OMSUSER         |
|  4 | REF_DATA_TYPE   | DOCUMENT_STATUS        | Status of document requests                                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.849003` | `2025-10-22 16:35:50.849006` | OMSUSER    | OMSUSER         |
|  5 | REF_DATA_TYPE   | BATCH_STATUS           | Status of document batches                                 |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.849374` | `2025-10-22 16:35:50.849379` | OMSUSER    | OMSUSER         |
|  6 | REF_DATA_TYPE   | SOURCE_SYSTEM          | Source systems generating document requests                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.849697` | `2025-10-22 16:35:50.849703` | OMSUSER    | OMSUSER         |
|  7 | REF_DATA_TYPE   | APP_DOC_SPEC           | Application document specifications for configurations     |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.849960` | `2025-10-22 16:35:50.849963` | OMSUSER    | OMSUSER         |
|  8 | REF_DATA_TYPE   | FOOTER_ID              | Footer identifiers for document footers                    |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.850232` | `2025-10-22 16:35:50.850235` | OMSUSER    | OMSUSER         |
|  9 | REF_DATA_TYPE   | DOC_CONFIG_CODE        | Document configuration codes for various settings          |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.850454` | `2025-10-22 16:35:50.850457` | OMSUSER    | OMSUSER         |
| 10 | SOURCE_SYSTEM   | CIBT                   | CIBT source system                                         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.850815` | `2025-10-22 16:35:50.850820` | OMSUSER    | OMSUSER         |
| 11 | SOURCE_SYSTEM   | ARCADE                 | ARCADE source system                                       |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.851188` | `2025-10-22 16:35:50.851192` | OMSUSER    | OMSUSER         |
| 12 | DOCUMENT_TYPE   | POLICY                 | Policy Documents                                           |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.851532` | `2025-10-22 16:35:50.851536` | OMSUSER    | OMSUSER         |
| 13 | DOCUMENT_TYPE   | INVOICE                | Invoice Documents                                          |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.851919` | `2025-10-22 16:35:50.851923` | OMSUSER    | OMSUSER         |
| 14 | DOCUMENT_NAME   | POSHOOFF               | Short offer policy document                                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.852208` | `2025-10-22 16:35:50.852211` | OMSUSER    | OMSUSER         |
| 15 | DOCUMENT_NAME   | IVZRECPA               | Zero reconciliation or premium adjustment invoice document |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.852460` | `2025-10-22 16:35:50.852463` | OMSUSER    | OMSUSER         |
| 16 | DOCUMENT_NAME   | IVBRKCOM               | Broker Commission Statement invoice document               |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.852696` | `2025-10-22 16:35:50.852699` | OMSUSER    | OMSUSER         |
| 17 | DOCUMENT_NAME   | IVCLFCAP               | Notice of Credit Limit Fee Cap Breach invoice document     |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.852950` | `2025-10-22 16:35:50.852953` | OMSUSER    | OMSUSER         |
| 18 | METADATA_KEY    | REQUEST_CORRELATION_ID | Metadata key for request correlation id                    |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.853246` | `2025-10-22 16:35:50.853250` | OMSUSER    | OMSUSER         |
| 19 | METADATA_KEY    | SOURCE_ENVIRONMENT     | Metadata key for source environment                        |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.853693` | `2025-10-22 16:35:50.853698` | OMSUSER    | OMSUSER         |
| 20 | METADATA_KEY    | LANGUAGE_CODE          | Metadata key for language code                             |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.854046` | `2025-10-22 16:35:50.854051` | OMSUSER    | OMSUSER         |
| 21 | METADATA_KEY    | FOOTER_ID              | Metadata key for policy number                             |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.854322` | `2025-10-22 16:35:50.854325` | OMSUSER    | OMSUSER         |
| 22 | METADATA_KEY    | ATRADIUS_ORG_ID        | Metadata key for Atradius organization identifier          |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.854557` | `2025-10-22 16:35:50.854560` | OMSUSER    | OMSUSER         |
| 23 | METADATA_KEY    | CUSTOMER_ID            | Metadata key for customer identifier                       |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.854770` | `2025-10-22 16:35:50.854773` | OMSUSER    | OMSUSER         |
| 24 | METADATA_KEY    | POLICY_ID              | Metadata key for policy number                             |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.855079` | `2025-10-22 16:35:50.855083` | OMSUSER    | OMSUSER         |
| 25 | METADATA_KEY    | INVOICE_ID             | Metadata key for policy number                             |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.855357` | `2025-10-22 16:35:50.855361` | OMSUSER    | OMSUSER         |
| 26 | DOCUMENT_STATUS | NEW                    | New document request received                              |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.855722` | `2025-10-22 16:35:50.855739` | OMSUSER    | OMSUSER         |
| 27 | DOCUMENT_STATUS | PROCESSING             | Document request is being processed                        |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.856105` | `2025-10-22 16:35:50.856109` | OMSUSER    | OMSUSER         |
| 28 | DOCUMENT_STATUS | IN_THUNDERHEAD         | Document request is being processed in thunderhead         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.856694` | `2025-10-22 16:35:50.856704` | OMSUSER    | OMSUSER         |
| 29 | DOCUMENT_STATUS | STOPPED                | Document request processing stopped                        |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.857724` | `2025-10-22 16:35:50.857732` | OMSUSER    | OMSUSER         |
| 30 | DOCUMENT_STATUS | FAILED                 | Document request processing failed                         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.858123` | `2025-10-22 16:35:50.858126` | OMSUSER    | OMSUSER         |
| 31 | DOCUMENT_STATUS | COMPLETED              | Document request processed successfully                    |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.858426` | `2025-10-22 16:35:50.858430` | OMSUSER    | OMSUSER         |
| 32 | BATCH_STATUS    | SUBMITTED              | Batch submitted to thunderhead successfully                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.858684` | `2025-10-22 16:35:50.858686` | OMSUSER    | OMSUSER         |
| 33 | BATCH_STATUS    | PROCESSING             | Batch is being processed by thunderhead                    |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.858973` | `2025-10-22 16:35:50.858976` | OMSUSER    | OMSUSER         |
| 34 | BATCH_STATUS    | STOPPED                | Batch processing stopped in thunderhead                    |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.859252` | `2025-10-22 16:35:50.859255` | OMSUSER    | OMSUSER         |
| 35 | BATCH_STATUS    | FAILED                 | Batch processing failed in thunderhead                     |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.859528` | `2025-10-22 16:35:50.859532` | OMSUSER    | OMSUSER         |
| 36 | BATCH_STATUS    | COMPLETED              | Batch processed successfully in thunderhead                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.859838` | `2025-10-22 16:35:50.859841` | OMSUSER    | OMSUSER         |
| 37 | APP_DOC_SPEC    | *                      | Default configuration for all document names               |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.860175` | `2025-10-22 16:35:50.860179` | OMSUSER    | OMSUSER         |
| 38 | APP_DOC_SPEC    | PO                     | Policy documents name starting with PO                     |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.860448` | `2025-10-22 16:35:50.860452` | OMSUSER    | OMSUSER         |
| 39 | APP_DOC_SPEC    | IV                     | Invoice documents name starting with IV                    |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.860772` | `2025-10-22 16:35:50.860776` | OMSUSER    | OMSUSER         |
| 40 | FOOTER_ID       | 0                      | Default footer for documents                               |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.861107` | `2025-10-22 16:35:50.861112` | OMSUSER    | OMSUSER         |
| 41 | FOOTER_ID       | 1                      | Footer id 1 for documents                                  |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.861613` | `2025-10-22 16:35:50.861617` | OMSUSER    | OMSUSER         |
| 42 | FOOTER_ID       | 2                      | Footer id 2 for documents                                  |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.861870` | `2025-10-22 16:35:50.861874` | OMSUSER    | OMSUSER         |
| 43 | FOOTER_ID       | 3                      | Footer id 3 for documents                                  |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.862119` | `2025-10-22 16:35:50.862122` | OMSUSER    | OMSUSER         |
| 44 | FOOTER_ID       | 4                      | Footer id 4 for documents                                  |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.862582` | `2025-10-22 16:35:50.862589` | OMSUSER    | OMSUSER         |
| 45 | DOC_CONFIG_CODE | SIGNEE_1               | Footer reference code for signee 1                         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.863062` | `2025-10-22 16:35:50.863067` | OMSUSER    | OMSUSER         |
| 46 | DOC_CONFIG_CODE | SIGNEE_2               | Footer reference code for signee 2                         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.863402` | `2025-10-22 16:35:50.863405` | OMSUSER    | OMSUSER         |
| 47 | DOC_CONFIG_CODE | TOPIC_NAME             | Footer reference code for topic name                       |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.863651` | `2025-10-22 16:35:50.863654` | OMSUSER    | OMSUSER         |
| 48 | DOC_CONFIG_CODE | BATCH_CONFIG_ID        | Thunderhead batch config id                                |    `2020-01-01` | `4712-12-31`  | `2025-10-22 16:35:50.864038` | `2025-10-22 16:35:50.864043` | OMSUSER    | OMSUSER         |

### tbom_document_configurations
- **Purpose:**
  - Effective-dated document configuration key/value mappings. References `tbom_reference_data` for footer, application document specification, and configuration code. Used to resolve default settings (e.g., SIGNEE_1, TOPIC_NAME) per footer/app-doc-spec/code over time.
- **Physical schema (Oracle):**
  - `id` NUMBER PRIMARY KEY (populated from sequence `sqomdcn_doc_config_id`) — Primary key for document configuration row.
  - `omrda_footer_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type FOOTER_ID.
  - `omrda_app_doc_spec_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type APP_DOC_SPEC.
  - `omrda_code_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type DOC_CONFIG_CODE.
  - `value` VARCHAR2(255) NOT NULL — Configuration value associated with the DOC_CONFIG_CODE.
  - `description` VARCHAR2(255) — Optional description of the configuration value.
  - `effect_from_dat` DATE NOT NULL — Date/time from which this configuration row becomes effective.
  - `effect_to_dat` DATE NOT NULL — Date/time until which this configuration row remains effective.
  - `created_dat` TIMESTAMP NOT NULL — Record creation timestamp.
  - `last_update_dat` TIMESTAMP NOT NULL — Record last update timestamp.
  - `create_uid` VARCHAR2(20) NOT NULL — User ID who created the record.
  - `last_update_uid` VARCHAR2(20) NOT NULL — User ID who last updated the record.
- **Foreign keys:**
  - `omrda_omdcn_fk1/2/3`: FKs for (`omrda_footer_id`, `omrda_app_doc_spec_id`, `omrda_code_id`) → `tbom_reference_data(id)`.
- **Indexes:**
  - `omdcn_01`..`omdcn_05`: indexes on (`omrda_footer_id`, `omrda_app_doc_spec_id`, `omrda_code_id`, `effect_from_dat`, `effect_to_dat`)
  - `omdcn_06`: composite business key index on (`omrda_footer_id`, `omrda_app_doc_spec_id`, `omrda_code_id`, `value`) to accelerate historization lookups.
- **Sequence:**
  - `sqomdcn_doc_config_id` used to assign primary keys on insert when `id` is not provided.
- **Triggers & behavior (Oracle):**
  - Compound trigger `omdcn_01t_bir_bur` (BEFORE EACH ROW + AFTER STATEMENT):
    - On INSERT: assigns `id` from sequence if missing; defaults `effect_from_dat` to `TRUNC(SYSDATE)` and `effect_to_dat` to `DATE '4712-12-31'` when not provided; sets `created_dat` when missing and always sets `last_update_dat` to `SYSTIMESTAMP`; defaults `create_uid`/`last_update_uid` to `USER` when missing.
    - Collects affected business keys and, AFTER STATEMENT, closes overlapping previous versions for the same `(omrda_footer_id, omrda_app_doc_spec_id, omrda_code_id, value)` by setting their `effect_to_dat` to one second before the new `effect_from_dat` (implemented as `new.effect_from_dat - 1/86400`).
  - Prevent-delete trigger `omdcn_02t_bdr`: raises an application error (`-20022`) to block physical deletes; logical deletes should be done by updating `effect_to_dat`.
- **Historization rule:**
  - Inserting or updating a row with `effect_from_dat` for an existing business key closes any previously active rows for that same key by setting their `effect_to_dat` to one second before the new `effect_from_dat`.
- **Usage examples:**
  - Storing default values for specific footer id, app document spec (IV, ShortOffer, *), and code (SIGNEE_1, SIGNEE_2, TOPIC_NAME, etc.), with time-based activation/deactivation via `effect_from_dat`/`effect_to_dat`.
  - Note: Oracle `DATE` stores date with time-of-day; the triggers use a one-second boundary for version closure.

- **Sample Data:** `SELECT * FROM tbom_document_configurations;`

| id | omrda_footer_id | omrda_app_doc_spec_id | omrda_code_id | value   | description                                             | effect_from_dat | effect_to_dat | created_dat                  | last_update_dat              | create_uid | last_update_uid |
|---:|-----------------|-----------------------|---------------|---------|---------------------------------------------------------|----------------:|---------------|------------------------------|------------------------------|------------|-----------------|
|  1 | 41              | 37                    | 46            | GBMHEN1 | Default signee 1 for all documents                      |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.177867` | `2025-10-22 18:19:38.177875` | OMSUSER    | OMSUSER         |
|  2 | 41              | 37                    | 47            | DEATES1 | Default signee 2 for all documents                      |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.187183` | `2025-10-22 18:19:38.187189` | OMSUSER    | OMSUSER         |
|  3 | 41              | 39                    | 46            | EMPTY   | Default signee 1 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.187533` | `2025-10-22 18:19:38.187536` | OMSUSER    | OMSUSER         |
|  4 | 41              | 39                    | 47            | ESMNOD1 | Default signee 2 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.187800` | `2025-10-22 18:19:38.187803` | OMSUSER    | OMSUSER         |
|  5 | 41              | 40                    | 46            | GBMHEN1 | Default signee 1 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188063` | `2025-10-22 18:19:38.188066` | OMSUSER    | OMSUSER         |
|  6 | 41              | 40                    | 47            | DEATES1 | Default signee 2 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188325` | `2025-10-22 18:19:38.188328` | OMSUSER    | OMSUSER         |
|  7 | 43              | 39                    | 46            | NLTKAA1 | Default signee 1 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188621` | `2025-10-22 18:19:38.188624` | OMSUSER    | OMSUSER         |
|  8 | 43              | 39                    | 47            | ESMNOD1 | Default signee 2 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188871` | `2025-10-22 18:19:38.188874` | OMSUSER    | OMSUSER         |
|  9 | 43              | 40                    | 46            | ESMNOD1 | Signee 1 for documents starting with IV and footer id 1 |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.189278` | `2025-10-22 18:19:38.189282` | OMSUSER    | OMSUSER         |
| 10 | 43              | 40                    | 47            | DEATES1 | Signee 2 for documents starting with IV and footer id 1 |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.189733` | `2025-10-22 18:19:38.189736` | OMSUSER    | OMSUSER         |

- **Sample Data with Join:** Query to get document configurations with actual reference data names:

```sql
SELECT f.id,
       rf.ref_data_value AS footer_id,
       ra.ref_data_value AS app_doc_spec,
       rc.ref_data_value AS code,
       f.value,
       f.description,
       f.effect_from_dat,
       f.effect_to_dat,
       f.created_dat,
       f.last_update_dat,
       f.create_uid,
       f.last_update_uid
FROM tbom_document_configurations f
         LEFT JOIN tbom_reference_data rf ON rf.id = f.omrda_footer_id
         LEFT JOIN tbom_reference_data ra ON ra.id = f.omrda_app_doc_spec_id
         LEFT JOIN tbom_reference_data rc ON rc.id = f.omrda_code_id
ORDER BY f.id;
```

| id | footer_id | app_doc_spec | code     | value   | description                                             | effect_from_dat | effect_to_dat | created_dat                  | last_update_dat              | create_uid | last_update_uid |
|---:|-----------|--------------|----------|---------|---------------------------------------------------------|----------------:|---------------|------------------------------|------------------------------|------------|-----------------|
|  1 | 0         | *            | SIGNEE_1 | GBMHEN1 | Default signee 1 for all documents                      |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.177867` | `2025-10-22 18:19:38.177875` | OMSUSER    | OMSUSER         |
|  2 | 0         | *            | SIGNEE_2 | DEATES1 | Default signee 2 for all documents                      |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.187183` | `2025-10-22 18:19:38.187189` | OMSUSER    | OMSUSER         |
|  3 | 0         | POSHOOFF     | SIGNEE_1 | EMPTY   | Default signee 1 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.187533` | `2025-10-22 18:19:38.187536` | OMSUSER    | OMSUSER         |
|  4 | 0         | POSHOOFF     | SIGNEE_2 | ESMNOD1 | Default signee 2 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.187800` | `2025-10-22 18:19:38.187803` | OMSUSER    | OMSUSER         |
|  5 | 0         | IV           | SIGNEE_1 | GBMHEN1 | Default signee 1 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188063` | `2025-10-22 18:19:38.188066` | OMSUSER    | OMSUSER         |
|  6 | 0         | IV           | SIGNEE_2 | DEATES1 | Default signee 2 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188325` | `2025-10-22 18:19:38.188328` | OMSUSER    | OMSUSER         |
|  7 | 2         | POSHOOFF     | SIGNEE_1 | NLTKAA1 | Default signee 1 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188621` | `2025-10-22 18:19:38.188624` | OMSUSER    | OMSUSER         |
|  8 | 2         | POSHOOFF     | SIGNEE_2 | ESMNOD1 | Default signee 2 for documents starting with IV         |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.188871` | `2025-10-22 18:19:38.188874` | OMSUSER    | OMSUSER         |
|  9 | 2         | IV           | SIGNEE_1 | ESMNOD1 | Signee 1 for documents starting with IV and footer id 1 |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.189278` | `2025-10-22 18:19:38.189282` | OMSUSER    | OMSUSER         |
| 10 | 2         | IV           | SIGNEE_2 | DEATES1 | Signee 2 for documents starting with IV and footer id 1 |    `2020-01-01` | `4712-12-31`  | `2025-10-22 18:19:38.189733` | `2025-10-22 18:19:38.189736` | OMSUSER    | OMSUSER         |


### tbom_document_requests
- **Purpose:**
  - Table to store document requests and its overall status, along with key details like source system, document type and document name.
- **Physical schema (Oracle):**
  - `id` NUMBER PRIMARY KEY (populated from sequence `sqomrda_doc_request_id`) — Primary key for document request.
  - `omrda_source_system_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type SOURCE_SYSTEM.
  - `omrda_document_type_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type DOCUMENT_TYPE.
  - `omrda_document_name_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type DOCUMENT_NAME.
  - `omrda_doc_status_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type DOCUMENT_STATUS indicating processing status.
  - `created_dat` TIMESTAMP NOT NULL — Record creation timestamp.
  - `last_update_dat` TIMESTAMP NOT NULL — Record last update timestamp.
  - `create_uid_header` VARCHAR2(20) NOT NULL — User ID from request header when creating the record.
  - `create_uid_token` VARCHAR2(20) NOT NULL — User ID from JWT token when creating the record.
- **Foreign keys:**
  - `omrda_omdrt_fk1/2/3/4`: FKs for (`omrda_source_system_id`, `omrda_document_type_id`, `omrda_document_name_id`, `omrda_doc_status_id`) → `tbom_reference_data(id)`.
- **Indexes:**
  - `omdrt_01`..`omdrt_05`: indexes on (`created_dat`, `omrda_doc_status_id`, `omrda_source_system_id`, `omrda_document_type_id`, `omrda_document_name_id`).
- **Sequence:**
  - `sqomrda_doc_request_id` used to assign primary keys on insert when `id` is not provided.
- **Triggers (Oracle):**
  - `omdrt_01t_bir` BEFORE INSERT: assigns `id` from sequence (if missing); sets `created_dat` and `last_update_dat` to `SYSTIMESTAMP`; defaults `create_uid_header`/`create_uid_token` to `USER` when not provided.
  - `omdrt_02t_bur` BEFORE UPDATE: sets `last_update_dat` to `SYSTIMESTAMP`.
- **Usage Examples:**
  - Each row represents a single document request from a source system, with type and name resolved via reference data.
- **Sample Data:** `SELECT * FROM tbom_document_requests;`

| id | omrda_source_system_id | omrda_document_type_id | omrda_document_name_id | omrda_doc_status_id | created_dat                  | last_update_dat              | create_uid_header | create_uid_token |
|---:|------------------------|------------------------|------------------------|---------------------|------------------------------|------------------------------|-------------------|------------------|
|  1 | 13                     | 1                      | 3                      | 8                   | `2025-10-17 09:39:59.594015` | `2025-10-17 09:39:59.594028` | GBHTAP1           | DEV_OMS          |
|  2 | 12                     | 2                      | 4                      | 9                   | `2025-10-17 09:39:59.650021` | `2025-10-17 09:39:59.650029` | GBHTAP1           | DEV_OMS          |


- **Sample Data with Join:** Query to get document requests with actual reference data names:

```sql
SELECT d.id,
       rf.ref_data_value AS source_system,
       ra.ref_data_value AS document_type,
       rb.ref_data_value AS document_name,
       rc.ref_data_value AS doc_status,
       d.created_dat,
       d.last_update_dat,
       d.create_uid_header,
       d.create_uid_token
FROM tbom_document_requests d
       LEFT JOIN tbom_reference_data rf ON rf.id = d.omrda_source_system_id
       LEFT JOIN tbom_reference_data ra ON ra.id = d.omrda_document_type_id
       LEFT JOIN tbom_reference_data rb ON rb.id = d.omrda_document_name_id
       LEFT JOIN tbom_reference_data rc ON rc.id = d.omrda_doc_status_id
ORDER BY D.id;
```
| id | source_system | document_type | document_name | doc_status | created_dat                  | last_update_dat              | create_uid_header | create_uid_token |
|---:|---------------|---------------|---------------|------------|------------------------------|------------------------------|-------------------|------------------|
|  1 | ARCADE        | INVOICE       | IVZRECPA      | REQUESTED  | `2025-10-17 09:39:59.594015` | `2025-10-17 09:39:59.594028` | GBHTAP1           | DEV_OMS          |
|  2 | CIBT          | POLICY        | ShortOffer    | COMPLETED  | `2025-10-17 09:39:59.594015` | `2025-10-17 09:39:59.594028` | GBHTAP1           | DEV_OMS          |

### tbom_document_requests_blob
- **Purpose:**
  - Stores JSON and optional XML request payloads associated 1:1 with `tbom_document_requests` (PK=FK on `omdrt_id`).
- **Key Fields (Oracle):**
  - `omdrt_id`: NUMBER PRIMARY KEY. Identifier matching `tbom_document_requests.id` (1:1); serves as both primary key and foreign key.
  - `json_request`: CLOB. Request payload in JSON format; required.
  - `xml_request`: CLOB. Request payload in XML format; optional.
- **Foreign Key:**
  - `omdrt_id` references `tbom_document_requests(id)` (ON DELETE CASCADE).
- **Usage Examples:**
  - Retaining the original document request data for auditing, troubleshooting, or reprocessing.
- **Sample Data:** `SELECT * FROM tbom_document_requests_blob;`

| omdrt_id | json_request                      | xml_request                           |
|----------|-----------------------------------|---------------------------------------|
| 1        | "{""document_type"":""Invoice""}" | <document_type>Invoice<document_type> |
| 2        | "{""document_type"":""Policy""}"  | <document_type>Policy<document_type>  |

### tbom_requests_metadata_values
- **Purpose:**
  - Table to store actual metadata values for each document request.
- **Physical schema (Oracle):**
  - `id` NUMBER PRIMARY KEY (populated from sequence `sqomrme_metadata_value_id`) — Primary key for metadata value.
  - `omdrt_id` NUMBER NOT NULL — Foreign key to `tbom_document_requests.id` (the owning document request).
  - `omrda_id` NUMBER NOT NULL — Foreign key to `tbom_reference_data.id`; must refer to a `METADATA_KEY` entry.
  - `metadata_value` VARCHAR2(255) NOT NULL — Concrete value for the referenced metadata key (string up to 255 chars).
- **Foreign keys:**
  - `omdrt_omrme_fk1`: `omdrt_id` → `tbom_document_requests(id)` (no ON DELETE CASCADE).
  - `omrda_omrme_fk2`: `omrda_id` → `tbom_reference_data(id)` (no ON DELETE CASCADE).
- **Indexes:**
  - `omrme_01`..`omrme_03`: indexes on (`omdrt_id`, `omrda_id`, `metadata_value`) for efficient lookups.
- **Sequence:**
  - `sqomrme_metadata_value_id` used to assign primary keys on insert when `id` is not provided.
- **Trigger (Oracle):**
  - `omrme_01t_bir` BEFORE INSERT: assigns `id` from `sqomrme_metadata_value_id` if missing.
- **Usage Examples:**
  - Storing dynamic metadata (e.g., customer ID, policy number) for each document request, with keys managed in reference data.
- **Sample Data:** `SELECT * FROM tbom_requests_metadata_values;`

| id | omdrt_id | omrda_id | metadata_value |
|---:|----------|----------|----------------|
|  1 | 1        | 5        | 1245647        |
|  2 | 1        | 7        | EN             |
|  3 | 2        | 5        | 4572451        |
|  4 | 2        | 6        | 48756          |

- **Sample Data with Join:** Query to get metadata values with actual reference data names:

```sql
SELECT mv.id,
       mv.omdrt_id as document_request_id,
       rf.ref_data_value AS metadata_key,
       mv.metadata_value
FROM tbom_requests_metadata_values mv
       LEFT JOIN tbom_reference_data rf ON rf.id = mv.omrda_id
ORDER BY mv.id;
```

| id | document_request_id | metadata_key  | metadata_value |
|---:|---------------------|---------------|----------------|
|  1 | 1                   | CUSTOMER_ID   | 1245647        |
|  2 | 1                   | LANGUAGE_CODE | EN             |
|  3 | 2                   | CUSTOMER_ID   | 4572451        |
|  4 | 2                   | POLICY_NUMBER | 48756          |

### tbom_th_batches
- **Purpose:**
  - Table to store Thunderhead batch details associated with document requests.
- **Physical schema (Oracle):**
  - `id` NUMBER PRIMARY KEY (populated from sequence `sqomthb_th_batch_id`) — Internal primary key for the batch row (not the Thunderhead ID).
  - `omdrt_id` NUMBER NOT NULL — FK to `tbom_document_requests(id)`. One request can have multiple batches.
  - `th_batch_id` NUMBER DEFAULT 10000 NOT NULL — External Thunderhead batch identifier; defaults to 10000 when not provided.
  - `omrda_th_status_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` with type `BATCH_STATUS` indicating batch processing status.
  - `batch_name` VARCHAR2(100) NOT NULL — Human-readable batch name as provided by Thunderhead or client.
  - `dms_document_id` NUMBER — Identifier of the generated document in DMS (nullable until available).
  - `sync_status` NUMBER(1) DEFAULT 0 NOT NULL — 0/1 flag indicating whether the batch status has been synchronized back to OMS.
  - `event_status` NUMBER(1) DEFAULT 0 NOT NULL — 0/1 flag indicating whether the batch status event has been published.
  - `retry_count` NUMBER DEFAULT 0 NOT NULL — Number of retry attempts for synchronization and event publishing.
  - `created_dat` TIMESTAMP NOT NULL — Record creation timestamp set at insert.
  - `last_update_dat` TIMESTAMP NOT NULL — Record last update timestamp set on insert/update.
  - `create_uid_header` VARCHAR2(20) NOT NULL — User ID (from request header) that created the record; defaults to USER when missing.
  - `create_uid_token` VARCHAR2(20) NOT NULL — User ID (from JWT token) that created the record; defaults to USER when missing.
- **Foreign keys:**
  - `omdrt_omtbe_fk1`: `omdrt_id` → `tbom_document_requests(id)`.
  - `omrda_omtbe_fk2`: `omrda_th_status_id` → `tbom_reference_data(id)` (type `BATCH_STATUS`).
- **Indexes:**
  - `omtbe_01`..`omtbe_06`: indexes on (`omrda_th_status_id`, `th_batch_id`, `sync_status`, `event_status`, `omdrt_id`, `retry_count`).
- **Sequence:**
  - `sqomthb_th_batch_id` used to assign primary keys on insert when `id` is not provided.
- **Triggers (Oracle):**
  - `omtbe_01t_bir` BEFORE INSERT: assigns `id` from sequence (if missing); sets `created_dat` (when missing) and `last_update_dat` to `SYSTIMESTAMP`; defaults `create_uid_header`/`create_uid_token` to `USER` when not provided.
  - `omtbe_01t_bur` BEFORE UPDATE: sets `last_update_dat` to `SYSTIMESTAMP`.
- **Usage Examples:**
  - Tracking the lifecycle and status of batch jobs for each document request.
- **Sample Data:** `SELECT * FROM tbom_th_batches;`

| id | omdrt_id | omrda_th_status_id | batch_name  | dms_document_id | sync_status | event_status | retry_count | created_dat                  | last_update_dat              | create_uid_header | create_uid_token |
|---:|----------|--------------------|-------------|-----------------|-------------|--------------|-------------|------------------------------|------------------------------|-------------------|------------------|
|  1 | 1        | 10                 | Batch_1_001 | 5001            | FALSE       | FALSE        | 0           | `2025-10-17 10:15:30.123456` | `2025-10-17 10:15:30.123467` | GBHTAP1           | DEV_OMS          |
|  2 | 2        | 11                 | Batch_2_001 | 5002            | TRUE        | TRUE         | 1           | `2025-10-17 10:20:45.234567` | `2025-10-17 10:20:45.234578` | GBHTAP1           | DEV_OMS          |

- **Sample Data with Join:** Query to get Thunderhead batches with actual reference data names:

```sql
SELECT b.id,
       b.omdrt_id AS document_request_id,
       rf.ref_data_value AS th_status,
       b.batch_name,
       b.dms_document_id,
       b.sync_status,
       b.event_status,
       b.retry_count,
       b.created_dat,
       b.last_update_dat,
       b.create_uid_header,
       b.create_uid_token
FROM tbom_th_batches b
         LEFT JOIN tbom_reference_data rf ON rf.id = b.omrda_th_status_id
ORDER BY b.id;
```

| id | document_request_id | th_status | batch_name  | dms_document_id | sync_status | event_status | retry_count | created_dat                  | last_update_dat              | create_uid_header | create_uid_token |
|---:|---------------------|-----------|-------------|-----------------|-------------|--------------|-------------|------------------------------|------------------------------|-------------------|------------------|
|  1 | 1                   | SUBMITTED | Batch_1_001 | 5001            | FALSE       | FALSE        | 0           | `2025-10-17 10:15:30.123456` | `2025-10-17 10:15:30.123467` | GBHTAP1           | DEV_OMS          |
|  2 | 2                   | PROCESSED | Batch_2_001 | 5002            | TRUE        | TRUE         | 1           | `2025-10-17 10:20:45.234567` | `2025-10-17 10:20:45.234578` | GBHTAP1           | DEV_OMS          |

### tbom_error_details
- **Purpose:**
  - Captures error details encountered during processing of a Thunderhead batch.
- **Physical schema (Oracle):**
  - `id` NUMBER PRIMARY KEY (populated from sequence `sqomedl_error_details_id`) — Internal primary key for error details (not an external identifier).
  - `omtbe_id` NUMBER NOT NULL — Foreign key to `tbom_th_batches(id)` identifying the owning batch.
  - `error_category` VARCHAR2(100) NOT NULL — Error category or type (e.g., VALIDATION_ERROR, TIMEOUT, INTERNAL_ERROR).
  - `error_description` CLOB NOT NULL — Detailed error description or message.
- **Foreign keys:**
  - `omtbe_omedl_fk`: `omtbe_id` → `tbom_th_batches(id)`.
- **Indexes:**
  - `omedl_01`: index on `omtbe_id` for efficient lookups by batch.
- **Sequence:**
  - `sqomedl_error_details_id` used to assign primary keys on insert when `id` is not provided.
- **Trigger (Oracle):**
  - `omedl_01t_bir` BEFORE INSERT: assigns `id` from sequence if missing.
- **Usage Examples:**
  - Capturing error details during batch processing for audit and troubleshooting.
- **Sample Data:** `SELECT * FROM tbom_error_details;`

| id | omtbe_id | error_category | error_description                                              |
|---:|----------|----------------|----------------------------------------------------------------|
|  1 | 1        | INTERNAL_ERROR | Internal server error occurred during batch processing.        |
|  2 | 2        | TIMEOUT_ERROR  | Timeout occurred while communicating with Thunderhead service. |

## Relationships
- `tbom_document_requests` references `tbom_reference_data` for source system, document type, and document name.
- `tbom_document_requests_blob` has a 1:1 relationship with `tbom_document_requests` (same primary key).
- `tbom_requests_metadata_values` links document requests from `tbom_document_requests` to metadata keys from `tbom_reference_data` (one-to-many).
- `tbom_th_batches` links to `tbom_document_requests` (one-to-many).
- `tbom_error_details` links to `tbom_th_batches` (one-to-many).

## Design Decisions & Rationale
- **Reference Data Centralization:**
  - All reference data is managed in a single table (`tbom_reference_data`) to maximize flexibility, reduce schema changes, and simplify lookups.
- **Auditability:**
  - Tables include appropriate audit fields based on their role:
    - `tbom_reference_data`, `tbom_document_configurations`: `created_dat`, `last_update_dat`, `create_uid`, `last_update_uid` with triggers to maintain them.
    - `tbom_document_requests`, `tbom_th_batches`: `created_dat`, `last_update_dat`, plus creator identifiers `create_uid_header` and `create_uid_token` with triggers to default/set them.
- **Extensibility:**
  - The schema supports new document types, metadata, and batch processes without requiring structural changes, thanks to the use of reference and mapping tables.
- **Data Integrity:**
  - Foreign key constraints enforce referential integrity across all relationships, preventing orphaned or inconsistent data.
- **Performance:**
  - Indexes are created on frequently queried columns and foreign keys to ensure efficient data retrieval and joins.
- **Separation of Concerns:**
  - Raw request payloads are stored separately from structured request data, supporting both efficient querying and full auditability.

## Example Data Flow
- **Reference Data Setup:**
   - Admins populate `tbom_reference_data` with all valid document types, names, metadata keys, and source systems.
-  **Reference Mapping:**
  - `tbom_document_configurations` is used to store reference mapping for footer id, app doc spec (IV, ShortOffer, * etc.) and code (SIGNEE_1, SIGNEE_2, TOPIC_NAME etc).
- **Document Request Creation:**
   - A new request is inserted into `tbom_document_requests`, referencing the appropriate source system, document type, and name.
   - The raw request payload is stored in `tbom_document_requests_blob`.
