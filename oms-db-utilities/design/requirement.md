## Service Architecture and Design Document
I have multiple services which performs different tasks as mentioned below:
1. Document_request_service:
    - API to accept new request. Sample request:

      ```json
      {"document_type":" INVOICE","document_name":"IVZRECPA","document_language":"FL","source_environment":"DEV","request_correlation_id":"REQ-0001-9749","document_metadata":[{"key":"INVOICE_ID","value":"490634"},{"key":"FOOTER_ID","value":"907"},{"key":"POLICY_ID","value":"14545"}],"document_content":{"Document":{}}}
      ```

    - accepts json request and stores it in tbom_document_requests, tbom_document_requests_blob, tbop_request_metadata_values
    - invoke document_processing_service and returns request id.

2. Document_processing_service:
    - Updates status as processing in tbom_document_requests
    - makes an entry in tbom_th_batches with th_batch_id as 10000.
    - Picks up configuration from tbom_document_configurations and updates json request.
    - converts JSON to XML and stores XML in tbom_document_requests_blob
    - Invokes thunderhead api and updated th_batch_id from response to tbom_th_batches
    - in case any errors at any stage add those errors to tbom_error_details
    - invokes document_notification_service for further processing.

3. document_notification_service:
    - Polls to Thunderhead to get processing status of batch. uses retry count to restrict few retries only.
    - Updates status and sync_status to tbom_th_batches.
    - In case of failure in thunderhead add errors to tbom_error_details.
    - Publishes event and update event_status flag.

4. document_monitoring_service: This is and rest api service which is used by UI.
    - UI screens to manage reference data accordingly API will have operations to manage it in tbom_reference_data, tbom_document_configurations
    - UI has screen to show all document processing details and accordingly this service will have APIs to handle it get it from DB.
    - UI also has re-process option when clicked it send request to Document_processing_service with request id which intern processes it by creating new batch entry.

## Requirement:
Based on this I would like to make this application as a library which can be used by all the above services. Could you please make necessary changes? please follow below recommendation as much as possible:

- Do not include anything service specific dependencies in pom.xml
- Do not include any service specific code in this library. All the code should be generic enough to be used by all services.
- Create separate modules if required for better separation of concerns.
- Ensure high cohesion and low coupling in the design.
- Ensure to follow SOLID principles.
- Ensure to follow best practices and design patterns wherever applicable.
- Do not change any table structures.
- Refer insert_tbom_reference_data.sql and insert_tbom_document_configurations.sql for sample data.
- Use Lombok to reduce boilerplate code, wherever applicable. Use @Slf4j for logging and add appropriate log statements.
- Add Javadocs for all the public methods and classes.
- Add comments wherever necessary to explain the logic.
- Create Utility classes wherever necessary to avoid code duplication.
- Create Constant classes, Enum classes, custom annotations wherever necessary.
- Ensure to handle exceptions properly and add custom exceptions wherever necessary.