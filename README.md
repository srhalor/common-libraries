# common-libraries (Maven monorepo)

Multi-module monorepo for shared **Spring Boot–aligned** libraries. All common setup and versions live in `libraries-parent`, which itself inherits `spring-boot-starter-parent`.

- Java: 21
- Shared parent: `com.shdev:libraries-parent:0.1.0`
- Modules: `common-utilities`, `security-utilities`, `oms-db-utilities`
- Each library publishes with sources and Javadocs

---

## 1) Build & install (Windows)

From the repo root (`common-libraries/`):

```bat
:: Build, run tests, and install all modules to ~/.m2
mvnw.cmd clean install

:: Build only one module plus its required deps
mvnw.cmd -pl security-utilities -am clean install
```

Prerequisites
- JDK 21 on PATH
- Maven 3.9+ (or the bundled Maven Wrapper `mvnw.cmd`)

---

## 2) Project layout

```text
common-libraries/
├─ pom.xml                 # root aggregator (no logic; just lists modules)
├─ libraries-parent/       # shared parent (inherits Spring Boot parent)
├─ common-utilities/       # general-purpose helpers (logging, strings, etc.)
├─ security-utilities/     # security helpers; depends on common-utilities
└─ oms-db-utilities/       # OMS persistence library (entities, mappers, services)
```

Key points
- **Root POM**: only aggregates modules; does not contain plugin or version logic.
- **libraries-parent**: single source of truth for:
  - Spring Boot parent version
  - Java/Maven constraints
  - MapStruct / annotation processing config
  - Internal library versions (`common-utilities`, `security-utilities`, `oms-db-utilities`).
- **Libraries can use each other** without specifying versions (managed by `libraries-parent`).

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
