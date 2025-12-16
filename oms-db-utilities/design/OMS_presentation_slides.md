# OMS Presentation — Slide Texts (copy into PPT template)

Purpose: This file contains ready-to-paste slide titles, body bullets, and short speaker notes for the OMS design review presentation (UI, API, DB). Use these to populate your `Presentation SC OMS Sprint 25.pptx` template.

Suggested timing (30-minute slot)
- Intro & Agenda: 2 min
- UI walkthrough: 10 min
- API design deep-dive: 8 min
- DB design highlights: 8 min
- Q&A: 2 min

---

## Slide 1 — Title
Title: OMS Design Overview
Subtitle: UI, API & DB — Sprint X (or date) — Approved Design
Footer: Presenter: <Your name> • Team: OMS • Duration: 30 min • Design status: Approved

Speaker note: Quick welcome; this is an overview of the already-approved design. Goal: provide handover, highlight operational considerations, and confirm owners for next steps.

---

## Slide 2 — Overview & Index
Title: Overview & Index
Bullets:
- 1. Objectives & Scope
- 2. UI Design — Dashboard, Reference Data, Request Explorer
- 3. API Design — Auth, Endpoints, Payloads, Errors
- 4. DB Design — Schema, historization, batches & errors
- 5. Example data flow, Decisions, Next steps, Q&A

Speaker note: This is an overview of the approved design; we'll summarize key points, operational impacts, and action items for handover.

---

## Slide 3 — Objectives & Scope
Title: Objectives & Scope
Bullets:
- Communicate the approved design and rationale for key choices
- Highlight operational and integration considerations for deployment
- Confirm API contracts, audit & security expectations, and handover ownership
- Identify any remaining implementation or rollout tasks

Speaker note: Emphasize that the design is approved; this session is for knowledge transfer, clarifications, and assigning follow-up tasks rather than for re-opening design decisions.

---

# UI section (approx. 10 minutes)

## Slide 4 — UI Overview
Title: UI Design — Overview
Bullets:
- Purpose: operational monitoring and document request inspection
- Primary screens: Service Health / Dashboard, Reference Data, Request Explorer
- Non-functional: responsive, accessible, in-memory filtering for small catalogs

Speaker note: Short orient to the three main user journeys.

---

## Slide 5 — Dashboard (Service Health)
Title: Dashboard — Service Health & Overview
Bullets:
- Health pills + service chips with last-checked details
- Summary tiles & distribution chart (status by source/date)
- Auto-refresh (≈60s), manual refresh, suspend when tab hidden
- Color-coded statuses (UP, DOWN, UNKNOWN) and request states

Speaker note: Emphasize monitoring intent and refresh behavior.

---

## Slide 6 — Reference Data
Title: Reference Data (Catalog)
Bullets:
- Tabs: Types | Values | Document Configurations (footer defaults)
- CRUD with edit/delete guard for non-editable entries
- Client-side filtering + paging (small datasets), consistent controls & page-size

Speaker note: Note reason for client-side approach: small catalogs, faster UX.

---

## Slide 7 — Document Request Explorer
Title: Document Request Explorer
Bullets:
- Filters: request-id, batch-id, metadata chips, date range, status, source, type, name
- Compact requests grid, server-side paging for large datasets
- Details popup: tabs for Metadata, Batches, Errors, Payload (JSON/XML)
- Actions: reprocess request, view/download payloads

Speaker note: Describe typical troubleshooting flow: locate request → inspect metadata/batches→ retrieve payload or reprocess.

---

# API section (approx. 8 minutes)

## Slide 8 — API Design — Overview
Title: API Design — Overview
Bullets:
- Purpose: serve UI and external integrations for document lifecycle
- Principles: small, well-documented contracts; clear error codes; versioning
- Auth: token-based (short-lived JWT) with refresh; prefer backend proxy for secrets

Speaker note: Set expectations for contract stability and auth model.

---

## Slide 9 — Authentication & Security
Title: Authentication & Security
Bullets:
- Standard JWT-based security implementation
- Re-uses security library for authentication
- Standard header support including `Atradius-Origin-User`
- Uses `Atradius-Origin-User` and `User` from JWT token during DB calls for auditing

Speaker note: Explain the JWT flow, how we reuse the security library, and how auditing is implemented using the `Atradius-Origin-User` header plus the JWT `User` claim during DB calls.

---

## Slide 10 — Key Endpoints (examples)
Title: Key Endpoints (examples)
Bullets:
- GET /health — service health summary
- GET /requests — list requests (filters + paging)
- GET /requests/{id} — request summary
- GET /requests/{id}/payload — JSON/XML payload
- POST /requests/{id}/reprocess — reprocess action
- GET/POST /reference-data — manage reference types/values (RBAC)

Speaker note: Explain list vs. single resource endpoints and RBAC where needed.

---

## Slide 11 — Payload Contracts & Error Handling
Title: Payloads & Error handling
Bullets:
- Use JSON for primary payloads; store full request as CLOB in DB for audit
- Define schemas for request and batch payloads (examples in `api-design-v2.md`)
- HTTP error model: standardized error object {code, message, details, timestamp}
- Retries: idempotency guidance and backoff for mutating endpoints

Speaker note: Point to `api-design-v2.md` for exact field-level contracts.

---

# DB section (approx. 8 minutes)

## Slide 12 — DB Design — Overview
Title: DB Design — Overview
Bullets:
- Centralized `tbom_reference_data` for types/values to minimize schema churn
- Effective-dated rows (historization) using `effect_from_dat` / `effect_to_dat`
- Audit fields + triggers to manage timestamps and version closure

Speaker note: High-level rationale: flexibility, auditability.

---

## Slide 13 — Key Tables & Purpose
Title: DB — Key Tables
Bullets:
- `tbom_reference_data` — ref-data types & values, historized
- `tbom_document_configurations` — effective-dated config mappings
- `tbom_document_requests` & `tbom_document_requests_blob` — request header + payload
- `tbop_request_metadata_values` — dynamic key-value metadata per request
- `tbom_th_batches` & `tbom_error_details` — batch lifecycle & errors

Speaker note: Highlight the FK structure and where payloads live for audit.

---

## Slide 14 — Historization & Triggers
Title: Historization & Triggers
Bullets:
- Compound triggers close overlapping versions on INSERT (one-second boundary)
- Logical delete via updating `effect_to_dat` (prevent physical delete triggers)
- Composite business-key indexes to speed historization and lookups

Speaker note: Note behavior when updating effective-dated records and why one-second boundary chosen.

---

## Slide 15 — Design Decisions & Tradeoffs
Title: Design Decisions & Tradeoffs
Bullets:
- Reference data centralization vs. many small lookup tables — chosen for extensibility
- Client-side filtering for small catalogs vs. server-side for requests (scale)
- Store raw payloads separately for efficient queries + full auditability
- Performance via targeted indexes and careful FK usage

Speaker note: Be prepared to discuss impact on migrations and operational procedures.

---

## Slide 16 — Example Data Flow (end-to-end)
Title: Example Data Flow
Bullets:
- 1) Admin populates `tbom_reference_data` and `tbom_document_configurations`
- 2) Client posts new request → row in `tbom_document_requests`; raw payload in blob table
- 3) Metadata saved to `tbop_request_metadata_values`; downstream batch created in `tbom_th_batches`
- 4) Errors logged in `tbom_error_details`; UI retrieves status via API/joins

Speaker note: Walk through a single trace from request to batch & potential error.

---

## Slide 17 — Summary & Next Steps
Title: Summary & Next Steps
Bullets:
- Recap: UI for ops + exploration, APIs for data & actions, DB for historized ref-data & audit
- Next steps: finalize API schemas (open items), add UI mockups for approval, finalize migration plan for ref-data
- Action items: assign owners for Open Items, schedule follow-up for unresolved questions

Speaker note: Confirm owners and deadlines.

---

## Slide 18 — Q&A
Title: Questions & Answers
Bullets:
- Open discussion
- Note any blockers & action owners

Speaker note: Invite specific questions about tradeoffs, migrations, auth, and performance.

---

## Appendices (optional slides to add)
- Appendix A: Detailed API contract samples (copy from `api-design-v2.md`)
- Appendix B: Sample SQL queries and example rows from `db-design-v2.md`
- Appendix C: UI mockups / wireframes (screenshots)

Tips
- Use the template’s Headline font/size for slide titles and the body text style for bullets.
- Put one screenshot per slide and add short captions.
- Add speaker notes (1–3 lines) using the notes above so you have prompts while presenting.


*End of file*
