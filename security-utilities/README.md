# Security Utilities

JWT authentication and authorization utilities for Spring Boot microservices.

## Overview

Provides reusable security components for implementing JWT-based authentication with role-based authorization in Spring Boot applications.

## Features

- **JWT Authentication Filter** - Validates JWT tokens with security-service
- **Origin Headers Filter** - Validates Atradius-specific request headers
- **Automatic Role Extraction** - Parses roles from JWT and integrates with Spring Security
- **MDC Integration** - Adds user/client info to MDC for logging and auditing
- **Auto-Configuration** - Spring Boot auto-configuration for easy integration

## Components

### Filters

#### JwtAuthenticationFilter
- Validates JWT tokens by calling security-service token info endpoint
- Extracts `userRole` field (colon-separated: "ADMIN:USER")
- Converts roles to Spring Security `GrantedAuthority`
- Sets Spring Security authentication context
- Adds user info to MDC for logging

#### OriginHeadersFilter
- Validates Atradius-specific headers (Origin-Service, Origin-Application, Origin-User)
- Supports strict and non-strict modes
- Adds headers to MDC for structured logging

### DTOs

#### TokenInfoDto
Type-safe record for security-service token info response:
```java
public record TokenInfoDto(
    String issuer,
    String subject,
    String client,
    String domain,
    String userRole,  // "ADMIN:USER:MANAGER"
    ...
)
```

### Services

#### JwtValidationService
- Encapsulates JWT token validation logic
- Calls security-service `/oauth2/rest/token/info` endpoint
- Returns structured `TokenInfoDto`
- Handles validation errors

### Utilities

#### RoleParser
Utility for parsing and converting roles:
```java
// Parse colon-separated roles
List<String> roles = RoleParser.parseRoles("ADMIN:USER");
// Result: ["ADMIN", "USER"]

// Convert to Spring Security authorities
Collection<GrantedAuthority> authorities = RoleParser.toAuthorities(roles);
// Result: [ROLE_ADMIN, ROLE_USER]

// Combined parsing and conversion
authorities = RoleParser.parseAndConvertToAuthorities("ADMIN:USER");
```

### Constants

#### SecurityConstants
- `DEFAULT_ROLE` - Default role when none found
- `ROLE_PREFIX` - Spring Security role prefix ("ROLE_")
- `SCOPE_PREFIX` - OAuth2 scope prefix ("SCOPE_")
- `ROLE_DELIMITER` - Role delimiter in userRole field (":")

## Integration

### Maven Dependency

```xml
<dependency>
    <groupId>com.shdev</groupId>
    <artifactId>security-utilities</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Configuration

Add to your `application.yml`:

```yaml
security:
  filter:
    jwt-enabled: true
    headers-enabled: true
    strict-header-mode: true
    token-validation-url: http://localhost:8090/oauth2/rest/token/info
    url-patterns:
      - /*
    excluded-paths: []  # Spring Security handles access control
```

### Spring Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

## Usage

### Automatic Integration

Filters are automatically configured via Spring Boot auto-configuration. No manual bean registration needed.

### Role-Based Authorization

Once integrated, use Spring Security annotations:

```java
@RestController
@RequestMapping("/api")
public class MyController {
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/reports")
    public Report createReport() {
        return reportService.create();
    }
}
```

## Authorization Patterns

### Common Authorization Patterns

```java
// Single role required
@PreAuthorize("hasRole('ADMIN')")

// One of multiple roles required
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPERVISOR')")

// Specific authority/scope required
@PreAuthorize("hasAuthority('SCOPE_read')")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")

// Multiple authorities required (AND)
@PreAuthorize("hasRole('ADMIN') and hasRole('MANAGER')")

// Either authority (OR)
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")

// User accessing their own resource
@PreAuthorize("#userId == authentication.name")
@GetMapping("/user/{userId}/profile")
public Profile getProfile(@PathVariable String userId) { ... }

// Complex business rules
@PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #id == authentication.name)")
@GetMapping("/employee/{id}")
public Employee getEmployee(@PathVariable String id) { ... }

// Check authentication property
@PreAuthorize("authentication.principal.username == 'special-user'")

// Combine role and parameter check
@PreAuthorize("hasRole('ADMIN') and #action == 'delete'")
@PostMapping("/resource/{id}/{action}")
public void manageResource(@PathVariable String id, @PathVariable String action) { ... }
```

### Access User Info

User information is automatically added to MDC:

```java
String userId = MdcUtil.get(HeaderConstants.MDC_USER_ID_TOKEN);
String client = MdcUtil.get(HeaderConstants.MDC_CLIENT);
String domain = MdcUtil.get(HeaderConstants.MDC_DOMAIN);
```

Or access via Spring Security:

```java
@GetMapping("/profile")
public Profile getProfile(Authentication auth) {
    String username = auth.getName();
    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    return profileService.get(username);
}
```

## JWT Token Format

Expected JWT token payload:

```json
{
  "sub": "user123",
  "client": "app_client",
  "domain": "DEV_JET_WebGateDomain",
  "roles": ["ADMIN", "USER"]
}
```

Security-service returns `userRole` field in token info response:

```json
{
  "sub": "user123",
  "userRole": "ADMIN:USER",
  ...
}
```

## Architecture

```
Request with JWT Token
    ↓
JwtAuthenticationFilter (Order: 1)
  - Extract token from Authorization header
  - Validate with security-service
  - Parse userRole field
  - Set Spring Security context
    ↓
OriginHeadersFilter (Order: 2)
  - Validate Atradius headers
  - Add to MDC
    ↓
Spring Security Authorization
  - Check SecurityConfig rules
  - Evaluate @PreAuthorize annotations
    ↓
Controller (if authorized)
```

## Dependencies

- `spring-boot-starter-security`
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `com.shdev:common-utilities`

## Package Structure

```
com.shdev.security
├── authentication/     # Spring Security tokens
├── config/            # Auto-configuration
├── constants/         # Security constants
├── dto/              # Data transfer objects
├── filter/           # Servlet filters
├── service/          # Business logic
└── util/             # Utility classes
```

## Version

Current version: **0.1.0**

## Related

- [common-utilities](../common-utilities/README.md) - Common utilities and constants
- [monitoring-service](../../monitoring-service/README.md) - Example usage

---

**Part of common-libraries ecosystem**

