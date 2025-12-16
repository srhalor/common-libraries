# Monitoring UI design

## Overview

Provide a concise operational summary of the OMS frontend screens for design review and operations: Service Health + Document Requests dashboard, Reference Data catalog, and Document Request Explorer.

## Table of contents

- [Security & authentication](#security--authentication)
- [Dashboard (Service Health / Document Requests Overview)](#dashboard-service-health--document-requests-overview)
- [Reference data](#reference-data)
  - [Reference data types](#reference-data-types)
  - [Reference data (values)](#reference-data-values)
  - [Document configurations / Footer defaults](#document-configurations--footer-defaults)
- [Pagination & filtering (reference data)](#pagination--filtering-reference-data)
- [Document Request Explorer](#document-request-explorer)
  - [Filters](#filters)
  - [Basic table design](#basic-table-design)
  - [View details (popup)](#view-details-popup)
  - [Pagination (requests)](#pagination-requests)

---

## Security & authentication

- Initial flow: On first load the UI calls a token endpoint using Basic Auth (client id + secret) to obtain a short-lived JWT.
- The JWT is held only in memory and attached to subsequent API calls via `Authorization: Bearer <jwt>`.
- Auto-refresh: schedule refresh before expiry; on 401, attempt a single refresh and retry once.
- Secrets must not be embedded in the client; prefer a backend proxy for production. For local development use temporary dev-only values or a proxy.

---

## Dashboard (Service Health / Document Requests Overview)

Purpose: Quick operational view showing service health and document processing activity with summary tiles, distribution charts and a requests grid.

Services health

- Shows health of different OMS services by invoking service's health API which intern gathers health of other services.
- Overall pill + service chips: display an overall status badge and individual service chips.
- Chip tooltips: show `lastChecked` time and a brief message (if available).
- Refresh: manual refresh button; auto-refresh interval ~60s (suspend when tab hidden).

Colors (status palette)

- UP — Positive 700: #406D1E
- DOWN — Error 700: #AF0524
- UNKNOWN — Warning 500: #EC8213

Document request overview

- Quick time ranges: Today, Last 7 Days, This Month, or a custom date range.
- Distribution chart shows counts by status and source.
- Example status colors:
  - Queued — Olive 200: #E5D5A1
  - Processing in OMS — Informative 200: #BEE3FF
  - Processing in Thunderhead — Informative 400: #5DB8FD
  - Completed — Positive 400: #8ACE4D
  - Failed — Error 400: #FA7070
  - Stopped — Warning 400: #F5A62F

Screenshot: Dashboard overview

---

## Reference data

Manage reference data used across the application. This area is organized into three tabs: Reference data types, Reference data values, and Document Configurations / Footer defaults.

### Reference data types

Purpose: Manage the canonical categories used across the app (e.g., `FOOTER_ID`, `APP_DOC_SPEC`).

Key controls:
- Add / Edit / Delete type (Edit/Delete hidden for non-editable types)
- Search / filter
- Pagination

Screenshot: Reference data types

### Reference data (values)

Purpose: Manage values for a selected reference data type (the actual key/value entries used by the system).

Key controls:
- Add / Edit / Delete value (Edit/Delete hidden for non-editable values)
- Search / filter
- Pagination

Screenshot: Reference data values

### Document configurations / Footer defaults

Purpose: Maintain document configuration mappings used when generating documents (linked to reference data types/values).

Key controls:
- Add / Edit / Delete mapping
- Search / filter
- Pagination

Screenshot: Document configurations

---

## Pagination & filtering (reference data)

Overview: All three Reference Data tabs implement client-side filtering and pagination for a consistent and responsive UX. Filtering is applied to the in-memory data and pagination is performed after filtering.

Controls and behavior

- Filter textbox: located at the top of each tab (left of the Add button). Users can type any substring (case-insensitive) to filter rows. The filter matches meaningful fields (name, description, effective dates for values; value/description and mapped names for footer defaults).
- Pagination controls: shown at the bottom of each grid and include First / Prev / numbered page buttons (5-page window) / Next / Last.
- Page size selector: located at the bottom next to the pagination controls. Default page size = 10 (options: 5, 10, 25, 50).
- Interaction: filtering is applied before pagination so totals and page counts reflect filtered results. Changing the filter or the page size resets the current page to the first page. Add/Edit/Delete operations refresh content and reset to page 1.

Notes and tradeoffs

- Client-side implementation: filtering and paging run in the browser on the loaded rows for fast, responsive UI.
- Reason: reference data is small (typically a few hundred rows), so client-side filtering/paging has negligible performance impact and is simpler to develop.

---

## Document Request Explorer

Purpose: Inspect/search individual document requests, view metadata, batches, and raw payloads (JSON/XML).

We are considering two UI approaches for the Document Request page. Both support the same core actions (view metadata, inspect batches, preview/download JSON/XML) but differ in layout and filtering UX.

### Filters

- Request IDs (multi)
- Batch IDs (multi)
- Metadata chips (key/value)
- Date range
- Status (multi)
- Source system (multi)
- Document Type (multi)
- Document Name (multi)

### Basic table design

- Layout: Requests are shown in a compact table/grid with one row per request.
- Actions: "View details" opens a popup screen which has tabs for Metadata, Batches, Errors, and Payload (JSON/XML). The popup provides options to load/download JSON and XML and shows metadata and batch details.
- Filters: Fixed filter inputs (one per field) at the top of the table for quick, familiar filtering (e.g., request id, status, type, date range etc.).
- Reprocess Document Request: Selecting rows + clicking the "Reprocess Document Request" button submits selected requests for re-processing.

Screenshot: Requests list

### View details (popup)

- Tabs: Metadata | Batches | Errors | Payload (JSON/XML)
- Metadata tab: show key/value pairs and allow copy or highlight metadata chips.
- Batches tab: list Thunderhead batches related to the request, with status, batch id, DMS id and action links.
- Errors tab: show error entries tied to batches (message, timestamp, severity).
- Payload tab: present JSON in a formatted, collapsible tree and the XML payload in a syntax-highlighted viewer; allow download.

Screenshot: View details - metadata
Screenshot: View details - payload

### Pagination (requests)

Overview: Standard page controls at the bottom (page numbers, page size). As this is a potentially large dataset, server-side paging will be used for the requests grid.

Controls and behavior (server-side paging)

- Pagination controls: First / Prev / numbered page window / Next / Last.
- Page size selector: Default page size = 10 (options: 5, 10, 25, 50).
- Interaction: Filtering requests will invoke the server API with the current filter + paging parameters. Changing filter or page size resets the current page to page 1.

---

## Notes

- Keep UI components accessible: proper labels, keyboard navigation and ARIA roles for the table, tabs and popup.
- Where possible, provide concise inline help text or tooltips for filters and actions.
- Consider adding a small analytics/tracing hook to measure slow queries and page load times for long-running requests.

---

*End of design doc*
