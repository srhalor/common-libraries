# OMS database design

## Overview

This document details the database schema for the OMS (Output Management System). The schema manages document requests, reference data, batch processing, and associated metadata. The design emphasizes normalization, extensibility, auditability, and data integrity and supports a wide range of document and batch processing scenarios.

## Table of contents

- [Entity descriptions](#entity-descriptions)
  - [`tbom_reference_data`](#tbom_reference_data)
  - [`tbom_document_configurations`](#tbom_document_configurations)
  - [`tbom_document_requests`](#tbom_document_requests)
  - [`tbom_document_requests_blob`](#tbom_document_requests_blob)
  - [`tbop_request_metadata_values`](#tbop_request_metadata_values)
  - [`tbom_th_batches`](#tbom_th_batches)
  - [`tbom_error_details`](#tbom_error_details)
- [Relationships](#relationships)
- [Design decisions & rationale](#design-decisions--rationale)
- [Example data flow](#example-data-flow)

## Logo Rovo Entity descriptions

Below are the main entities (tables) in the schema, along with their purposes, key fields, relationships, and design considerations.

### `tbom_reference_data`

#### Purpose

Central reference-data table used by OMS for document types, document names, metadata keys, source systems, footer ids, doc config codes, etc.

#### Key fields

- `id` NUMBER PRIMARY KEY (sequence: `sqomrda_ref_data_id`)
- `ref_data_type` VARCHAR2(50) NOT NULL — e.g. DOCUMENT_TYPE, DOCUMENT_NAME, METADATA_KEY, SOURCE_SYSTEM
- `ref_data_value` VARCHAR2(100) NOT NULL — the value (e.g. Invoice, IVZRECPA)
- `description` VARCHAR2(255) — optional
- `editable` CHAR(1) DEFAULT 'N' NOT NULL — Y/N flag
- `effect_from_dat` DATE NOT NULL — effective from
- `effect_to_dat` DATE NOT NULL — effective to
- `created_dat` TIMESTAMP NOT NULL
- `last_update_dat` TIMESTAMP NOT NULL
- `create_uid` VARCHAR2(20) NOT NULL
- `last_update_uid` VARCHAR2(20) NOT NULL

#### Indexes

- `omrda_01..omrda_04`: indexes on `ref_data_type`, `ref_data_value`, `effect_from_dat`, `effect_to_dat`.

#### Sequence

- `sqomrda_ref_data_id` — assigns primary keys on insert when `id` is not provided.

#### Triggers & behavior

- Compound trigger `omrda_01t_bir_bur` (BEFORE EACH ROW + AFTER STATEMENT):
  - On INSERT: assigns `id` from sequence if missing; defaults `effect_from_dat` to `TRUNC(SYSDATE)` and `effect_to_dat` to `DATE '4712-12-31'` when not provided; sets `created_dat` when missing and always sets `last_update_dat` to `SYSTIMESTAMP`; defaults `create_uid`/`last_update_uid` to the DB `USER` when missing.
  - After statement: closes overlapping previous versions for the same (`ref_data_type`, `ref_data_value`) by setting their `effect_to_dat` to one second before the new `effect_from_dat`.
- Prevent-delete trigger `omrda_02_bdr`: raises application error (-20021) to block physical deletes; logical deletes should be done by updating `effect_to_dat`.

#### Historization rule

New/updated rows that set or change `effect_from_dat` cause prior rows with overlapping validity for the same business key to be closed (their `effect_to_dat` set to one second before the new `effect_from_dat`).

#### Usage examples

Storing all possible document types, names, and metadata keys in a single, extensible table.

#### Sample query

```sql
SELECT * FROM tbom_reference_data;
```

---

### `tbom_document_configurations`

#### Purpose

Effective-dated document configuration key/value mappings. References `tbom_reference_data` for footer, application document specification, and configuration code. Used to resolve default settings (e.g., `SIGNEE_1`, `TOPIC_NAME`) per footer/app-doc-spec/code over time.

#### Key fields

- `id` NUMBER PRIMARY KEY (sequence: `sqomdcn_doc_config_id`)
- `omrda_footer_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `FOOTER_ID`)
- `omrda_app_doc_spec_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `APP_DOC_SPEC`)
- `omrda_code_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `DOC_CONFIG_CODE`)
- `value` VARCHAR2(255) NOT NULL — configuration value
- `description` VARCHAR2(255) — optional
- `effect_from_dat` DATE NOT NULL
- `effect_to_dat` DATE NOT NULL
- `created_dat` TIMESTAMP NOT NULL
- `last_update_dat` TIMESTAMP NOT NULL
- `create_uid` VARCHAR2(20) NOT NULL
- `last_update_uid` VARCHAR2(20) NOT NULL

#### Foreign keys

- `omrda_omdcn_fk1/2/3`: FKs for `(omrda_footer_id, omrda_app_doc_spec_id, omrda_code_id)` → `tbom_reference_data(id)`.

#### Indexes

- `omdcn_01..omdcn_05`: indexes on `(omrda_footer_id, omrda_app_doc_spec_id, omrda_code_id, effect_from_dat, effect_to_dat)`.
- `omdcn_06`: composite business key index on `(omrda_footer_id, omrda_app_doc_spec_id, omrda_code_id, value)`.

#### Sequence

- `sqomdcn_doc_config_id` — assigns primary keys on insert when `id` is not provided.

#### Triggers & behavior

- Compound trigger `omdcn_01t_bir_bur` (BEFORE EACH ROW + AFTER STATEMENT): on insert assigns `id` from sequence if missing; defaults `effect_from_dat`/`effect_to_dat` and audit timestamps/UIDs; after statement closes overlapping previous versions for the same business key.
- Prevent-delete trigger `omdcn_02t_bdr`: raises application error (-20022) to block physical deletes.

#### Historization rule

Inserting/updating a row with `effect_from_dat` for an existing business key closes any previously active rows for that same key by setting their `effect_to_dat` to one second before the new `effect_from_dat`.

#### Usage examples

Storing default values for a specific footer id, app document spec (IV, ShortOffer, *), and code (SIGNEE_1, SIGNEE_2, TOPIC_NAME, etc.).

#### Sample query

```sql
SELECT * FROM tbom_document_configurations; 
```

---

### `tbom_document_requests`

#### Purpose

Stores document requests and their overall status, along with key details like source system, document type and document name.

#### Key fields

- `id` NUMBER PRIMARY KEY (sequence: `sqomrda_doc_request_id`)
- `omrda_source_system_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `SOURCE_SYSTEM`)
- `omrda_document_type_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `DOCUMENT_TYPE`)
- `omrda_document_name_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `DOCUMENT_NAME`)
- `omrda_doc_status_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `DOCUMENT_STATUS`)
- `created_dat` TIMESTAMP NOT NULL
- `last_update_dat` TIMESTAMP NOT NULL
- `create_uid_header` VARCHAR2(20) NOT NULL — user id from request header
- `create_uid_token` VARCHAR2(20) NOT NULL — user id from JWT token

#### Foreign keys

- `omrda_omdrt_fk1/2/3/4`: FKs for `(omrda_source_system_id, omrda_document_type_id, omrda_document_name_id, omrda_doc_status_id)` → `tbom_reference_data(id)`.

#### Indexes

- `omdrt_01..omdrt_05`: indexes on `(created_dat, omrda_doc_status_id, omrda_source_system_id, omrda_document_type_id, omrda_document_name_id)`.

#### Sequence

- `sqomrda_doc_request_id` — assigns primary keys on insert when `id` is not provided.

#### Triggers

- `omdrt_01t_bir` BEFORE INSERT: assigns `id`, sets `created_dat` and `last_update_dat` to `SYSTIMESTAMP`, defaults `create_uid_header`/`create_uid_token`.
- `omdrt_02t_bur` BEFORE UPDATE: sets `last_update_dat` to `SYSTIMESTAMP`.

#### Usage examples

Each row represents a single document request from a source system, with type and name resolved via reference data.

#### Sample query

```sql
SELECT * FROM tbom_document_requests;
```

---

### `tbom_document_requests_blob`

#### Purpose

Stores JSON and optional XML request payloads associated 1:1 with `tbom_document_requests` (PK = FK on `omdrt_id`).

#### Key fields

- `omdrt_id` NUMBER PRIMARY KEY — matches `tbom_document_requests.id` (1:1)
- `json_request` CLOB — required
- `xml_request` CLOB — optional

#### Foreign key

- `omdrt_id` references `tbom_document_requests(id)` (ON DELETE CASCADE).

#### Usage examples

Retain the original request payload for auditing, troubleshooting, or reprocessing.

#### Sample query

```sql
SELECT * FROM tbom_document_requests_blob;
```

---

### `tbop_request_metadata_values`

#### Purpose

Stores metadata values for each document request.

#### Key fields

- `id` NUMBER PRIMARY KEY (sequence: `sqomrme_metadata_value_id`)
- `omdrt_id` NUMBER NOT NULL — FK to `tbom_document_requests.id`
- `omrda_id` NUMBER NOT NULL — FK to `tbom_reference_data.id` (must refer to a `METADATA_KEY`)
- `metadata_value` VARCHAR2(255) NOT NULL

#### Foreign keys

- `omdrt_omrme_fk1`: `omdrt_id` → `tbom_document_requests(id)`
- `omrda_omrme_fk2`: `omrda_id` → `tbom_reference_data(id)`

#### Indexes

- `omrme_01..omrme_03`: indexes on `(omdrt_id, omrda_id, metadata_value)`.

#### Sequence

- `sqomrme_metadata_value_id` — assigns primary keys on insert when `id` is not provided.

#### Trigger

- `omrme_01t_bir` BEFORE INSERT: assigns `id` from sequence if missing.

#### Usage examples

Store dynamic metadata (customer ID, policy number) per request.

#### Sample query

```sql
SELECT * FROM tbop_request_metadata_values;
```

---

### `tbom_th_batches`

#### Purpose

Stores Thunderhead batch details associated with document requests.

#### Key fields

- `id` NUMBER PRIMARY KEY (sequence: `sqomthb_th_batch_id`)
- `omdrt_id` NUMBER NOT NULL — FK to `tbom_document_requests(id)`
- `th_batch_id` NUMBER DEFAULT 10000 NOT NULL — external Thunderhead batch id
- `omrda_th_status_id` NUMBER NOT NULL — FK to `tbom_reference_data(id)` (type `BATCH_STATUS`)
- `batch_name` VARCHAR2(100) NOT NULL
- `dms_document_id` NUMBER — nullable
- `sync_status` CHAR(1) DEFAULT 'N' NOT NULL
- `event_status` CHAR(1) DEFAULT 'N' NOT NULL
- `retry_count` NUMBER DEFAULT 0 NOT NULL
- `created_dat` TIMESTAMP NOT NULL
- `last_update_dat` TIMESTAMP NOT NULL
- `create_uid_header` VARCHAR2(20) NOT NULL
- `create_uid_token` VARCHAR2(20) NOT NULL

#### Foreign keys

- `omdrt_omtbe_fk1`: `omdrt_id` → `tbom_document_requests(id)`
- `omrda_omtbe_fk2`: `omrda_th_status_id` → `tbom_reference_data(id)` (type `BATCH_STATUS`)

#### Indexes

- `omtbe_01..omtbe_06`: indexes on `(omrda_th_status_id, th_batch_id, sync_status, event_status, omdrt_id, retry_count)`.

#### Sequence

- `sqomthb_th_batch_id` — assigns primary keys on insert when `id` is not provided.

#### Triggers

- `omtbe_01t_bir` BEFORE INSERT: assigns `id`, sets audit timestamps and default UIDs.
- `omtbe_01t_bur` BEFORE UPDATE: sets `last_update_dat` to `SYSTIMESTAMP`.

#### Usage examples

Track lifecycle and status of batch jobs for each document request.

#### Sample query

```sql
SELECT * FROM tbom_th_batches;
```

---

### `tbom_error_details`

#### Purpose

Captures error details encountered during processing of a Thunderhead batch.

#### Key fields

- `id` NUMBER PRIMARY KEY (sequence: `sqomedl_error_details_id`)
- `omtbe_id` NUMBER NOT NULL — FK to `tbom_th_batches(id)`
- `error_category` VARCHAR2(100) NOT NULL
- `error_description` CLOB NOT NULL

#### Foreign keys

- `omtbe_omedl_fk`: `omtbe_id` → `tbom_th_batches(id)`

#### Indexes

- `omedl_01`: index on `omtbe_id`.

#### Sequence

- `sqomedl_error_details_id` — assigns primary keys on insert when `id` is not provided.

#### Trigger

- `omedl_01t_bir` BEFORE INSERT: assigns `id` from sequence if missing.

#### Usage examples

Capture error details during batch processing for audit and troubleshooting.

#### Sample query

```sql
SELECT * FROM tbom_error_details;
```

---

## Relationships

- `tbom_document_requests` references `tbom_reference_data` for source system, document type, and document name.
- `tbom_document_requests_blob` has a 1:1 relationship with `tbom_document_requests` (same primary key).
- `tbop_request_metadata_values` links `tbom_document_requests` to `tbom_reference_data` (one-to-many).
- `tbom_th_batches` links to `tbom_document_requests` (one-to-many).
- `tbom_error_details` links to `tbom_th_batches` (one-to-many).

## Design decisions & rationale

- Reference data centralization: manage all reference data in a single table (`tbom_reference_data`) for flexibility and simpler lookups.
- Auditability: tables include audit fields and triggers to maintain timestamps and creator/updater UIDs.
- Extensibility: schema supports new document types and metadata without structural changes.
- Data integrity: foreign keys enforce referential integrity.
- Performance: indexes on frequently queried columns and foreign keys help query performance.
- Separation of concerns: raw request payloads are stored separately from structured request data to support efficient querying plus full auditability.

## Thunderhead data backup

During status gathering, additional Thunderhead API calls are made and responses stored in backup tables (same structure as current OPUS DB). These tables retain details returned by the API for incident analysis because Thunderhead retains data only for a short period.

Backup tables (examples)

- `tbom_th_jobmessages`
- `tbom_th_batch_history`
- `tbom_th_batch_channeldetails`

## Example data flow

1. Reference data setup: admins populate `tbom_reference_data` with all valid document types, names, metadata keys, and source systems.
2. Reference mapping: `tbom_document_configurations` stores mappings for footer id, app doc spec (IV, ShortOffer, *), and code (SIGNEE_1, SIGNEE_2, TOPIC_NAME, etc.).
3. Document request creation: a new request is inserted into `tbom_document_requests` referencing the appropriate source system, document type, and name.
4. The raw request payload is stored in `tbom_document_requests_blob`.
5. Metadata key-value pairs are stored in `tbop_request_metadata_values`.
6. Batch processing: batches related to the request are tracked in `tbom_th_batches`.
7. Error details are added to `tbom_error_details` as needed.

## Diagram
Below is visual representation of the schema and relationships.

Drawio Diagram: [OMS Database Schema]

## Notes

- SQL sample queries shown are examples; adjust schema names and qualifiers as needed for your environment.
