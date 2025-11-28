# OMS DB Utilities (oms-db-utilities)

A reusable persistence library for the OMS ecosystem, providing JPA entities, MapStruct mappers, and small services for Oracle-backed tables:
- TBOM_REFERENCE_DATA
- TBOM_DOCUMENT_CONFIGURATIONS
- TBOM_DOCUMENT_REQUESTS (+ BLOB)
- TBOM_REQUESTS_METADATA_VALUES
- TBOM_TH_BATCHES
- TBOM_ERROR_DETAILS

Highlights
- Service-agnostic: no web or messaging dependencies.
- Strong DTO ↔ Entity mapping using MapStruct.
- Clean audit handling via JPA listeners and MDC.
- Oracle-first DDL + seed scripts packaged for convenience.

What’s included
- Entities: `com.shdev.omsdatabase.entity.*`
- Mappers: `com.shdev.omsdatabase.mapper.*`
  - ReferenceDataMapper (centralized reference-data helpers)
  - DocumentRequestMapper (request lifecycle + status updates)
  - DocumentConfigurationMapper (effective-dated config)
  - MetadataValueMapper (request metadata values)
  - ThBatchMapper (Thunderhead batches)
  - ErrorDetailMapper (batch errors)
- Services: `com.shdev.omsdatabase.service.*`
- Utilities: `com.shdev.omsdatabase.util.*` (audit listener)
- Constants/Enums: `com.shdev.omsdatabase.constants.*`

Dependency (use in your service)

```xml
<dependency>
  <groupId>com.shdev</groupId>
  <artifactId>oms-db-utilities</artifactId>
  <version>0.1.0</version>
</dependency>
```

Build & install locally

```cmd
mvnw.cmd -DskipTests install
```

JPA and Oracle setup in host service
- Provide your own DataSource + JPA config.
- Add Oracle JDBC driver:

```xml
<dependency>
  <groupId>com.oracle.database.jdbc</groupId>
  <artifactId>ojdbc11</artifactId>
  <version>REPLACE_WITH_VERSION</version>
</dependency>
```

Example Spring properties

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=OMSUSER
spring.datasource.password=omsuserpass
spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.default_schema=OMSUSER
```

Mappers overview & usage
MapStruct mappers are Spring components (componentModel=spring). Autowire them directly, or use the provided services that wrap them.

- ReferenceDataMapper
  - Central helper: `idToRefEntity(Long) → ReferenceDataEntity` (qualifiedByName="idToRefEntity")
  - Lite mapping: `toLite(ReferenceDataEntity)` for nested DTOs

- DocumentRequestMapper
  - Create: `toEntity(DocumentRequestInDto)`
    - Foreign keys via `idToRefEntity` (sourceSystem/type/name/status)
  - Update status only: `updateStatus(DocumentRequestInDto, @MappingTarget entity)`
  - Retrieve: `toDto(DocumentRequestEntity)` → `DocumentRequestOutDto`
  - Helper: `idToRequestEntity(Long)` (qualifiedByName="idToRequestEntity") for other mappers

- ThBatchMapper
  - Create: `toEntity(ThBatchInDto)`
    - requestId → omdrt via `idToRequestEntity`
    - batchStatusId → omrdaThStatus via `idToRefEntity`
  - Partial update: `updateEntity(ThBatchInDto, @MappingTarget)`
    - Nulls ignored; request link not changed
  - Retrieve: `toDto(ThBatchEntity)` → `ThBatchOutDto`
  - Helper: `idToThBatchEntity(Long)` (qualifiedByName="idToThBatchEntity") for other mappers

- DocumentConfigurationMapper
  - Create: `toEntity(DocumentConfigInDto)` (footer/appDocSpec/code via `idToRefEntity`)
  - Partial update: `updateEntity(DocumentConfigInDto, @MappingTarget)` (nulls ignored)
  - Retrieve: `toDto(DocumentConfigEntity)` → `DocumentConfigOutDto`

- MetadataValueMapper
  - Create: `toEntity(MetadataValueInDto)`
    - requestId → omdrt via `idToRequestEntity`
    - metadataKeyId → omrda via `idToRefEntity`
  - Retrieve: `toDto(RequestsMetadataValueEntity)` → `MetadataValueOutDto`

- ErrorDetailMapper
  - Create: `toEntity(ErrorDetailDto)`
    - batchId → omtbe via `idToThBatchEntity`
  - Retrieve: `toDto(ErrorDetailEntity)` → `ErrorDetailDto` (batchId is a Long)

Quick examples

```java
@Service
class Demo {
  private final DocumentRequestMapper requestMapper;
  private final ThBatchMapper thBatchMapper;
  private final MetadataValueMapper metaMapper;
  private final DocumentConfigurationMapper configMapper;
  private final ErrorDetailMapper errorMapper;

  Demo(DocumentRequestMapper requestMapper,
       ThBatchMapper thBatchMapper,
       MetadataValueMapper metaMapper,
       DocumentConfigurationMapper configMapper,
       ErrorDetailMapper errorMapper) {
    this.requestMapper = requestMapper;
    this.thBatchMapper = thBatchMapper;
    this.metaMapper = metaMapper;
    this.configMapper = configMapper;
    this.errorMapper = errorMapper;
  }

  void createRequest() {
    var in = new DocumentRequestInDto(1L, 2L, 3L, 4L);
    var entity = requestMapper.toEntity(in);
    // repo.save(entity);
  }

  void updateRequestStatus(DocumentRequestEntity entity) {
    var patch = new DocumentRequestInDto(null, null, null, 99L);
    requestMapper.updateStatus(patch, entity);
    // repo.save(entity);
  }

  void createThBatch() {
    var in = new ThBatchInDto(100L, 200L, 300L, "BATCH-1", 400L, true, false, 5L);
    var entity = thBatchMapper.toEntity(in);
    // repo.save(entity);
  }

  void saveMetadataValue() {
    var in = new MetadataValueInDto(101L, 202L, "VAL");
    var entity = metaMapper.toEntity(in);
    // repo.save(entity);
  }

  void saveError() {
    var in = new ErrorDetailDto(null, 55L, "VALIDATION_ERROR", "missing field");
    var e = errorMapper.toEntity(in);
    // repo.save(e);
  }
}
```

Audit integration (timestamps and user IDs)
- Timestamps (`createdDat`, `lastUpdateDat`) are DB-trigger managed (insertable=false, updatable=false)
- User fields are populated from SLF4J MDC by a JPA entity listener
- Set these MDC keys in your host service per request/thread:
  - `userIdHeader` → maps to `CREATE_UID_HEADER`
  - `userIdToken`  → maps to `CREATE_UID_TOKEN`
  - `userIdHeader`       → maps to `CREATE_UID` and `LAST_UPDATE_UID`

Minimal MDC filter example (host service)

```java
@Component
class AuditMdcFilter extends HttpFilter {
  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    try {
      String headerUser = req.getHeader("X-User-Id");
      String tokenUser = (String) req.getAttribute("tokenUserId");
      String genericUser = headerUser != null ? headerUser : tokenUser;

      if (headerUser != null) MDC.put("userIdHeader", headerUser);
      if (tokenUser != null) MDC.put("userIdToken", tokenUser);

      chain.doFilter(req, res);
    } finally {
      MDC.remove("userIdHeader");
      MDC.remove("userIdToken");
    }
  }
}
```

SQL initialization
- DDL: `src/main/resources/sql/oracle/*.sql`
- Seed: `src/main/resources/sql/insert/*`

Run local Oracle XE (optional)

```cmd
:: From repo root
docker compose -f docker\oracle-db\docker-compose.yaml up -d
```

Development notes
- Java 21
- Spring Boot for dependency management
- MapStruct 1.6.x, Lombok
- Booleans persisted as Y/N via `BooleanToStringConverter` (auto-apply)

Troubleshooting
- If unit tests fail due to mapper injection (NPE on nested mapper), ensure componentModel is `spring` and tests use a Spring context (as in `MapperTestConfig`).
- For partial updates, ensure `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` is applied to update methods.
