# OMS Database Library

A reusable persistence library for the OMS ecosystem, providing JPA entities, repositories, mappers, and small services around Oracle-backed tables:
- TBOM_REFERENCE_DATA
- TBOM_DOCUMENT_CONFIGURATIONS
- TBOM_DOCUMENT_REQUESTS and TBOM_DOCUMENT_REQUESTS_BLOB
- TBOM_REQUESTS_METADATA_VALUES
- TBOM_TH_BATCHES
- TBOM_ERROR_DETAILS

Highlights
- Generic, service-agnostic code. No REST, messaging, or service-specific logic.
- Lombok-based logging and reduced boilerplate.
- MapStruct mappers for DTO ↔ entity conversions.
- Utilities for defaults.
- Enums/constants for reference data types and statuses.

What’s included
- Entities: com.shdev.omsdatabase.entity.*
- Repositories: com.shdev.omsdatabase.repository.*
- Services: com.shdev.omsdatabase.service.*
  - ReferenceDataService for type-safe ref data lookups (incl. dynamic METADATA_KEY helpers)
  - DocumentRequestService, ThBatchService, DocumentConfigService, RequestsMetadataValueService, DocumentRequestBlobService, ErrorDetailService
- Mappers: com.shdev.omsdatabase.mapper.* (MapStruct)
- Utilities: com.shdev.omsdatabase.util.* (AuditEntityListener)
- Constants/Enums: com.shdev.omsdatabase.constants.* (RefDataType, DocumentStatus, BatchStatus)

Consume in other services
Add the dependency to your service (make sure this artifact/version is available via your repo or local Maven cache):

```xml
<dependency>
  <groupId>com.shdev</groupId>
  <artifactId>oms-database</artifactId>
  <version>0.0.2-SNAPSHOT</version>
</dependency>
```

Build and install locally (for development)
- This installs the library into your local Maven repository so other local projects can resolve it.

```cmd
mvnw.cmd -DskipTests install
```

Host service dependencies and JPA configuration
- Provide your own DataSource, JPA properties, and transaction manager in the host service. This library does not autoconfigure a DataSource.
- Add the Oracle JDBC driver to your host service (example coordinates; choose a version appropriate for your environment):

```xml
<dependency>
  <groupId>com.oracle.database.jdbc</groupId>
  <artifactId>ojdbc11</artifactId>
  <version>REPLACE_WITH_VERSION</version>
</dependency>
```

Example Spring properties (host service)

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=OMSUSER
spring.datasource.password=omsuserpass
spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.open-in-view=false
# Optional if you want to pin the default schema
spring.jpa.properties.hibernate.default_schema=OMSUSER
```

Dynamic metadata keys
- Metadata keys are managed as reference data rows (type=METADATA_KEY). Use ReferenceDataService for lookups:

```java
Long keyId = referenceDataService.getMetadataKeyIdOrThrow("REQUEST_CORRELATION_ID");
boolean exists = referenceDataService.existsMetadataKey("CUSTOMER_ID");
var keys = referenceDataService.listMetadataKeys();
```

Spring Data JPA usage (example)
- Inject repository or service and call methods as needed:

```java
@Service
class Sample {
  private final ReferenceDataService refService;
  private final DocumentRequestService requestService;

  Sample(ReferenceDataService refService, DocumentRequestService requestService) {
    this.refService = refService;
    this.requestService = requestService;
  }

  void demo() {
    Long statusId = refService.getIdByTypeAndNameOrThrow(RefDataType.DOCUMENT_STATUS, "NEW");
    // For metadata keys (dynamic):
    Long footerKeyId = refService.getMetadataKeyIdOrThrow("FOOTER_ID");
    // Build a DTO, then persist using DocumentRequestService
    // requestService.create(dto);
  }
}
```

SQL initialization
- DDL: see src/main/resources/sql/oracle/create_*.sql (these are packaged with the JAR)
- Seed data: src/main/resources/sql/insert/* (also packaged)
- Apply the DDL and seed scripts from your SQL client connected as OMSUSER (or your chosen schema owner).

Local Oracle with Docker (optional)
- A minimal Oracle XE compose is provided. Pulling images from container-registry.oracle.com may require logging in and accepting terms.

```cmd
:: From the repo root
docker compose -f docker\oracle-db\docker-compose.yaml up -d
```

- The compose mounts docker/oracle-db/create_schema.sql which creates user OMSUSER/omsuserpass in PDB XEPDB1.
- After the container is healthy, connect with your SQL client and run the DDL from src/main/resources/sql/oracle, then seed from src/main/resources/sql/insert.

## Audit integration (timestamps and user IDs)
- This library does not depend on Spring Security.
- Database triggers are expected to populate timestamps (CREATED_DAT, LAST_UPDATE_DAT) and default DB user values when not provided by the application.
- User fields are populated from SLF4J MDC (thread context) if present. Set the following MDC keys in your host service per request/thread:
  - userIdHeader → used for CREATE_UID_HEADER
  - userIdToken  → used for CREATE_UID_TOKEN
  - userId       → used for CREATE_UID and LAST_UPDATE_UID

Tiny Spring filter/interceptor example (host service)

```java
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class AuditMdcFilter extends HttpFilter {
  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    try {
      // Example extraction logic; adapt to your headers/authentication
      String headerUser = req.getHeader("X-User-Id");
      String tokenUser = (String) req.getAttribute("tokenUserId");
      String genericUser = headerUser != null ? headerUser : tokenUser;

      if (headerUser != null) MDC.put("userIdHeader", headerUser);
      if (tokenUser != null) MDC.put("userIdToken", tokenUser);
      if (genericUser != null) MDC.put("userId", genericUser);

      chain.doFilter(req, res);
    } finally {
      MDC.remove("userIdHeader");
      MDC.remove("userIdToken");
      MDC.remove("userId");
    }
  }
}
```

After setting MDC keys
- Just call the services:
  - requestService.create(dto)
  - requestService.update(id, dto)
  - thBatchService.create(dto)
  - thBatchService.update(id, dto)
  - configService.create(dto)
  - configService.update(id, dto)
- The JPA entity listener will pull audit users from the MDC; timestamps and default DB user will be set by your triggers.

Notes
- Timestamps: left null by the library and set by DB triggers (@DynamicInsert/@DynamicUpdate ensure nulls are omitted so defaults/triggers apply).
- User fields: populated automatically from MDC if present; otherwise left null for triggers to fill.
- Packaged resources: the JAR includes only Oracle DDL (create_*.sql) and insert scripts.

Development
- Java 21
- Spring Boot parent for dependency management
- MapStruct 1.6.x, Lombok

Notes
- Logging is available via Lombok @Slf4j; adjust levels in your host service logging config.

## Misc
- A MySQL Docker Compose file exists under docker/sql-db but this library targets Oracle and only packages Oracle DDL/seed scripts. The MySQL compose is optional and not used by the library itself.
