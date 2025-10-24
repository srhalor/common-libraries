# common-libraries monorepo – Requirements (Concise)

Owner: Shailesh
Last updated: 2025-10-24

## 0) Purpose
- Single Maven monorepo to develop, test, and publish multiple custom libraries for Spring Boot applications.
- Centralize ALL versioning (project, Spring Boot, dependencies, plugins) in one parent to simplify upgrades and keep services consistent.
- Allow inter-module dependencies and provide a simple consumer experience in host services without including the Boot parent in the service.

---

## 1) Architecture and Layout
- Root aggregator POM (packaging=pom) orchestrates the build.
- libraries-parent (packaging=pom) is the shared parent and inherits Spring Boot’s parent:
  - Parent: org.springframework.boot:spring-boot-starter-parent:3.5.7
  - properties: java.version and any versions not covered by Boot (e.g., mapstruct)
  - dependencyManagement: manage internal modules (and any extras not covered by Boot)
  - Attach sources and javadocs for better IDE/repo consumption
- Library modules (packaging=jar): common-utilities, security-utilities, oms-db-libraries, others as needed.

Structure
```
common-libraries/
├─ pom.xml                  # root aggregator
├─ libraries-parent/
│  └─ pom.xml               # parent (inherits spring-boot-starter-parent)
├─ common-utilities/
│  └─ pom.xml
├─ security-utilities/
│  └─ pom.xml
└─ oms-db-libraries/
   └─ pom.xml
```

---

## 2) Conventions and Constraints
- Java 21; build with Maven Wrapper (mvnw/mvnw.cmd).
- All modules inherit from libraries-parent. No child module defines <version>.
- Do not set versions for dependencies/plugins that are managed by Boot parent; only define versions for extras not covered by Boot.
- Enforce rules via Maven Enforcer (Java/Maven versions, dependency convergence).
- Inter-module dependencies are allowed (e.g., security-utilities -> common-utilities); avoid cycles.

---

## 3) Parent (libraries-parent) – Required capabilities
- Inherits spring-boot-starter-parent to get Spring Boot’s dependency and plugin management.
- Properties
  - java.version
  - versions for key libraries not covered by Boot (e.g., mapstruct)
- dependencyManagement
  - Declare internal modules for consumer convenience at ${project.version}
- Build plugins
  - Attach -sources.jar and -javadoc.jar for all modules

Illustrative snippet
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.5.7</version>
  <relativePath/>
</parent>

<properties>
  <java.version>21</java.version>
  <mapstruct.version>1.6.3</mapstruct.version>
</properties>
```

---

## 4) Modules – Minimal rules
- Each module inherits from libraries-parent.
- Declare only artifactId/name and direct dependencies (no versions if managed by Boot parent).
- For Boot configuration metadata in a module, add optional spring-boot-configuration-processor.
- If a module uses MapStruct or Lombok, add those dependencies and configure processors per-module when needed.

Inter-module dependency example (no version needed)
```xml
<dependency>
  <groupId>com.shdev</groupId>
  <artifactId>common-utilities</artifactId>
</dependency>
```

---

## 5) Using libraries in Spring Boot services (no Spring Boot parent in service)
Host service POM inherits from libraries-parent and declares Boot starters normally.
```xml
<parent>
  <groupId>com.shdev</groupId>
  <artifactId>libraries-parent</artifactId>
  <version>X.Y.Z</version>
  <relativePath/>
</parent>

<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>com.shdev</groupId>
    <artifactId>common-utilities</artifactId>
  </dependency>
</dependencies>
```
Why this works: libraries-parent inherits the Spring Boot parent, so services don’t need to include spring-boot-starter-parent themselves.

---

## 6) Versioning and Releases
- Semantic Versioning for the monorepo: MAJOR.MINOR.PATCH applies to all modules via project.version.
- Bump project version once; children inherit automatically.
- Recommended: release all modules together for each version to keep the BOM/parent expectations consistent.

Windows-friendly workflows
```bat
:: Bump repo-wide version
mvnw.cmd -B versions:set -DnewVersion=1.2.0 -DprocessAllModules -DgenerateBackupPoms=false

:: Update Spring Boot parent version (in libraries-parent)
mvnw.cmd -B -pl libraries-parent -am versions:update-parent -DparentVersion=3.5.8 -DgenerateBackupPoms=false

:: Build and deploy all modules
mvnw.cmd -B -DskipTests deploy

:: Deploy a single module (build required deps too)
mvnw.cmd -B -pl security-utilities -am -DskipTests deploy
```
Notes
- Reactor builds compute order automatically; avoid cyclic dependencies.
- For releases, ensure all artifacts referenced by the parent/BOM exist.

---

## 7) Publishing (own repository)
Recommended: Sonatype Nexus Repository OSS
- Configure distributionManagement in libraries-parent and credentials in CI settings.xml.

---

## 8) CI/CD (minimal)
- Build on PRs/branches: ./mvnw -B verify
- Release on tag vX.Y.Z: ./mvnw -B deploy (with settings.xml and distributionManagement configured)

---

## 9) Acceptance Criteria
- [x] Root aggregator builds modules (common-utilities, security-utilities, oms-db-libraries).
- [x] libraries-parent inherits Spring Boot parent and centralizes any extra versions and plugin configuration.
- [x] Modules inherit from libraries-parent and do not declare versions for managed deps/plugins.
- [x] Host Spring Boot services inherit from libraries-parent (not Boot parent directly) and consume modules without versions.
- [x] Inter-module dependencies work without specifying versions.
- [x] CI can deploy snapshots/releases to the chosen repository via distributionManagement + settings.xml.
