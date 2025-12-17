# Common Libraries

Multi-module Maven monorepo for shared Spring Boot libraries.

## Overview

Provides reusable libraries for Spring Boot microservices:
- **common-utilities** - Common utilities, constants, and helpers
- **security-utilities** - JWT authentication and authorization
- **oms-db-utilities** - Database entities, repositories, and services

## Quick Start

### Prerequisites
- **JDK 21** or higher
- **Maven 3.9+** (or use included Maven Wrapper)

### Build All Modules

```bash
# Windows
mvnw.cmd clean install

# Unix/Mac
./mvnw clean install
```

### Build Single Module

```bash
# Build security-utilities and its dependencies
mvnw.cmd -pl security-utilities -am clean install
```

## Modules

### [common-utilities](common-utilities/README.md)
**Version**: 0.1.0

Shared utilities and constants:
- Header constants (Authorization, Atradius headers)
- MDC utilities for structured logging
- Header validators
- Type conversion utilities
- Common DTOs

**Maven**:
```xml
<dependency>
    <groupId>com.shdev</groupId>
    <artifactId>common-utilities</artifactId>
    <version>0.1.0</version>
</dependency>
```

### [security-utilities](security-utilities/README.md)
**Version**: 0.1.0

JWT authentication and role-based authorization:
- JWT authentication filter
- Origin headers validation filter
- Automatic role extraction from JWT
- Spring Security integration
- Auto-configuration support

**Maven**:
```xml
<dependency>
    <groupId>com.shdev</groupId>
    <artifactId>security-utilities</artifactId>
    <version>0.1.0</version>
</dependency>
```

### [oms-db-utilities](oms-db-utilities/README.md)
**Version**: 0.1.0

Database utilities and OMS entities:
- JPA entities with auditing
- Repository interfaces
- Service layer base classes
- MapStruct mappers
- Database configuration

**Maven**:
```xml
<dependency>
    <groupId>com.shdev</groupId>
    <artifactId>oms-db-utilities</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Project Structure

```
common-libraries/
├── pom.xml                      # Root aggregator
├── libraries-parent/            # Parent POM with shared config
│   └── pom.xml
├── common-utilities/            # Common utilities
│   ├── pom.xml
│   ├── README.md
│   └── src/
├── security-utilities/          # Security utilities
│   ├── pom.xml
│   ├── README.md
│   └── src/
└── oms-db-utilities/           # Database utilities
    ├── pom.xml
    ├── README.md
    └── src/
```

## Parent POM

**libraries-parent** provides:
- Spring Boot parent inheritance (v3.5.8)
- Java 21 configuration
- Shared dependency versions
- Plugin configurations (MapStruct, Lombok)
- Version management for all modules

All libraries inherit from `libraries-parent`:
```xml
<parent>
    <groupId>com.shdev</groupId>
    <artifactId>libraries-parent</artifactId>
    <version>0.1.0</version>
</parent>
```

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.8
- **Maven**: 3.9+
- **Lombok**: For boilerplate reduction
- **MapStruct**: For object mapping

## Version Management

Versions are managed centrally in `libraries-parent/pom.xml`:
- Spring Boot: 3.5.8
- Module versions: 0.1.0
- All dependencies aligned with Spring Boot

## Module Dependencies

```
oms-db-utilities
    └── common-utilities

security-utilities
    └── common-utilities

common-utilities
    └── (no internal dependencies)
```

## Usage in Microservices

Add to your service `pom.xml`:

```xml
<parent>
    <groupId>com.shdev</groupId>
    <artifactId>libraries-parent</artifactId>
    <version>0.1.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>com.shdev</groupId>
        <artifactId>security-utilities</artifactId>
    </dependency>
    <!-- No version needed - managed by parent -->
</dependencies>
```

## Development

### Build Specific Module

```bash
mvnw.cmd -pl common-utilities clean install
```

### Skip Tests

```bash
mvnw.cmd clean install -DskipTests
```

### Run Tests Only

```bash
mvnw.cmd test
```

---

## 3) Using the parent in a microservice

For a new Spring Boot microservice (e.g. `monitoring-service`), point its `pom.xml` to `libraries-parent` and then add library dependencies **without** explicit versions.

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>com.shdev</groupId>
    <artifactId>libraries-parent</artifactId>
    <version>0.1.0</version>  <!-- use a released version from your repo -->
    <relativePath/>           <!-- resolve from local/remote repo -->
  </parent>

  <artifactId>monitoring-service</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <name>monitoring-service</name>

  <dependencies>
    <!-- Standard Spring Boot starters -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Shared libraries (no versions, managed by libraries-parent) -->
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>common-utilities</artifactId>
    </dependency>
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>security-utilities</artifactId>
    </dependency>
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>oms-db-utilities</artifactId>
    </dependency>

    <!-- Optional extras / tests -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>runtime</scope>
      <optional>true</optional>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

Why this works
- `libraries-parent` already inherits `spring-boot-starter-parent`, so the service **does not** need its own Spring Boot parent.
- Internal libraries’ versions are managed via `<dependencyManagement>` in `libraries-parent` (`shdev.libraries.version`).

If a service must keep a different parent (e.g. an existing corporate parent):

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>libraries-parent</artifactId>
      <version>0.1.0</version>   <!-- pinned BOM version -->
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.shdev</groupId>
    <artifactId>common-utilities</artifactId>
  </dependency>
  <dependency>
    <groupId>com.shdev</groupId>
    <artifactId>security-utilities</artifactId>
  </dependency>
  <dependency>
    <groupId>com.shdev</groupId>
    <artifactId>oms-db-utilities</artifactId>
  </dependency>
</dependencies>
```

---

## 4) Version management (single source of truth)

All internal library versions are centralized in `libraries-parent`:

- Children declare their parent version and normally **inherit** their own `<version>` from it:
  ```xml
  <parent>
    <groupId>com.shdev</groupId>
    <artifactId>libraries-parent</artifactId>
    <version>0.1.0</version>
    <relativePath>../libraries-parent/pom.xml</relativePath>
  </parent>
  ```
- Inter-module dependencies (e.g. `security-utilities` → `common-utilities`) omit `<version>` and rely on `<dependencyManagement>` in `libraries-parent`.

Typical maintenance flow (from `common-libraries/`):

```bat
:: See current libraries-parent version
mvnw.cmd -q -pl libraries-parent -DforceStdout help:evaluate -Dexpression=project.version

:: Bump libraries-parent (and property shdev.libraries.version) to 0.1.1 and update children
mvnw.cmd -pl libraries-parent -am versions:set -DnewVersion=0.1.1 -DgenerateBackupPoms=false
mvnw.cmd versions:update-parent -DparentVersion=[0.1.1] -DgenerateBackupPoms=false -DallowSnapshots=true
```

Guidelines
- Prefer updating versions in `libraries-parent` instead of editing child POMs directly.
- For Spring Boot upgrades, change the `<parent><version>` in `libraries-parent` and let children inherit.

---

## 5) Publishing to Nexus (optional)

`libraries-parent/pom.xml` defines `distributionManagement` for ShDev Nexus:
- Releases: `internal-releases` → `https://nexus.shdev.local/repository/maven-releases/`
- Snapshots: `internal-snapshots` → `https://nexus.shdev.local/repository/maven-snapshots/`

Example `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>internal-releases</id>
      <username>${env.NEXUS_USERNAME}</username>
      <password>${env.NEXUS_PASSWORD}</password>
    </server>
    <server>
      <id>internal-snapshots</id>
      <username>${env.NEXUS_USERNAME}</username>
      <password>${env.NEXUS_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

Deploy (from `common-libraries/`):

```bat
mvnw.cmd -DskipTests deploy
```

Artifacts for each module include `-sources.jar` and `-javadoc.jar` for better IDE and repository browsing.

---

## 6) FAQ (short)

- **Can one library use another (e.g. security → common)?**  
  Yes. Just add the dependency without a version; `libraries-parent` manages it.

- **How do services use these libraries?**  
  Either:
  - Use `libraries-parent` as the service parent, or
  - Import `libraries-parent` as a BOM in `<dependencyManagement>`.

- **Do I have to release all modules together?**  
  Recommended: yes, so the BOM (`libraries-parent`) version always refers to a consistent set of library artifacts.

- **Do services share the same version as libraries?**  
  No. Services have their own `<version>`; libraries share the version driven by `libraries-parent` (via `shdev.libraries.version`).
