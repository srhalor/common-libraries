# Common Utilities

Shared utilities and constants for Spring Boot microservices.

## Overview

Provides common utilities, constants, DTOs, and helper classes used across all microservices in the ecosystem.

## Features

- **Header Constants** - Standardized HTTP header names
- **MDC Utilities** - MDC (Mapped Diagnostic Context) management for logging
- **Header Validators** - HTTP header validation utilities
- **Response DTOs** - Common response structures
- **Exception Handling** - Standardized exception handling
- **Type Converters** - Type conversion utilities

## Components

### Constants

#### HeaderConstants
Standard header names used across services:

```java
// Authentication headers
public static final String AUTHORIZATION = "Authorization";
public static final String OAUTH_IDENTITY_DOMAIN_NAME = "X-OAUTH-IDENTITY-DOMAIN-NAME";

// Atradius custom headers
public static final String ATRADIUS_ORIGIN_SERVICE = "Atradius-Origin-Service";
public static final String ATRADIUS_ORIGIN_APPLICATION = "Atradius-Origin-Application";
public static final String ATRADIUS_ORIGIN_USER = "Atradius-Origin-User";

// MDC keys for logging
public static final String MDC_USER_ID_TOKEN = "userIdToken";
public static final String MDC_USER_ID_HEADER = "userIdHeader";
public static final String MDC_CLIENT = "client";
public static final String MDC_DOMAIN = "domain";
public static final String MDC_REQUEST_ID = "requestId";
```

### Utilities

#### MdcUtil
Thread-safe MDC management:

```java
// Add to MDC
MdcUtil.put("key", "value");

// Retrieve from MDC
String value = MdcUtil.get("key");

// Remove from MDC
MdcUtil.remove("key");

// Clear all MDC
MdcUtil.clear();
```

#### HeaderValidator
HTTP header validation:

```java
// Extract Bearer token
String token = HeaderValidator.extractBearerToken(authHeader);
// Input: "Bearer eyJhbGci..."
// Output: "eyJhbGci..."

// Validate required headers
boolean valid = HeaderValidator.hasRequiredHeaders(request, 
    List.of("Header1", "Header2"));
```

#### TypeConversionUtil
Safe type conversions:

```java
// Convert to String
String str = TypeConversionUtil.toString(object);

// Convert to Long
Long num = TypeConversionUtil.toLong(object);

// Convert to List<String>
List<String> list = TypeConversionUtil.toStringList(object);
```

### DTOs

#### ApiResponse
Standardized API response wrapper:

```java
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    String timestamp
) {
    public static <T> ApiResponse<T> success(T data) { ... }
    public static <T> ApiResponse<T> error(String message) { ... }
}
```

#### ErrorResponse
Error response structure:

```java
public record ErrorResponse(
    String error,
    String message,
    String path,
    int status,
    String timestamp
) {}
```

## Integration

### Maven Dependency

```xml
<dependency>
    <groupId>com.shdev</groupId>
    <artifactId>common-utilities</artifactId>
    <version>0.1.0</version>
</dependency>
```

No additional configuration required.

## Usage Examples

### MDC for Structured Logging

```java
@RestController
public class MyController {
    
    @GetMapping("/api/data")
    public Data getData(HttpServletRequest request) {
        // Add request ID to MDC
        MdcUtil.put(HeaderConstants.MDC_REQUEST_ID, UUID.randomUUID().toString());
        
        // Add user info to MDC (set by security filters)
        // MDC automatically included in logs
        log.info("Fetching data");  // Logs will include MDC fields
        
        Data data = dataService.getData();
        
        // MDC is automatically cleared after request
        return data;
    }
}
```

### Log Output with MDC

```
2025-12-17 10:30:45 [http-nio-8080-exec-1] INFO MyController [req-123] [user-456] [client-789] - Fetching data
```

### Header Validation

```java
@Component
public class MyFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        // Extract and validate token
        String authHeader = request.getHeader(HeaderConstants.AUTHORIZATION);
        String token = HeaderValidator.extractBearerToken(authHeader);
        
        if (token == null) {
            // Handle missing token
            return;
        }
        
        // Proceed with token validation
        filterChain.doFilter(request, response);
    }
}
```

### Standardized Responses

```java
@RestController
public class ProductController {
    
    @GetMapping("/api/products")
    public ApiResponse<List<Product>> getProducts() {
        List<Product> products = productService.getAll();
        return ApiResponse.success(products);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleError(Exception e) {
        return ResponseEntity.status(500)
            .body(ApiResponse.error("Internal server error"));
    }
}
```

## Logging Configuration

Recommended logback pattern to include MDC fields:

```xml
<pattern>
    %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} [%X{requestId}] [%X{userIdToken}] [%X{client}] - %msg%n
</pattern>
```

Or in `application.yml`:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} [%X{requestId}] [%X{userIdToken}] [%X{client}] - %msg%n"
```

## Package Structure

```
com.shdev.common
├── constants/         # Shared constants
├── dto/              # Common DTOs
├── exception/        # Exception classes
└── util/             # Utility classes
```

## Dependencies

- `spring-boot-starter-web`
- `lombok`
- `slf4j-api`

## Version

Current version: **0.1.0**

## Related

- [security-utilities](../security-utilities/README.md) - JWT authentication filters
- [oms-db-utilities](../oms-db-utilities/README.md) - Database utilities

---

**Part of common-libraries ecosystem**

