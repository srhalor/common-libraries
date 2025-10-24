# common-libraries monorepo – Requirements (Concise)

Owner: Shailesh
Last updated: 2025-10-24

## 0) Purpose
- Single Maven monorepo to develop, test, and publish multiple custom libraries for Spring Boot applications.
- Centralize ALL versioning (project, Spring Boot, dependencies, plugins) in one parent/BOM to simplify upgrades and keep services consistent.
- Allow inter-module dependencies and provide a simple consumer experience in host services without using Spring Boot parent.

---

## 1) Architecture and Layout
- Root aggregator POM (packaging=pom) orchestrates the build.
- libraries-parent (packaging=pom) acts as Parent + BOM:
  - properties: java.version, spring-boot.version, and key dependency/plugin versions
  - dependencyManagement: import Spring Boot BOM and pin extras if needed
  - pluginManagement: standardize compiler, surefire, boot plugin, etc.
  - Attach sources and javadocs for better IDE/repo consumption
- Library modules (packaging=jar): common-utilities, security-utilities, oms-db-libraries, others as needed.

Structure
```
common-libraries/
├─ pom.xml                  # root aggregator
├─ libraries-parent/
│  └─ pom.xml               # parent + BOM
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
- Do not set versions for managed dependencies/plugins in child modules.
- Enforce rules via Maven Enforcer (Java/Maven versions, dependency convergence).
- Inter-module dependencies are allowed (e.g., security-utilities -> common-utilities); avoid cycles.

---

## 3) Parent/BOM (libraries-parent) – Required capabilities
- Properties
  - spring-boot.version (single source of truth)
  - versions for key libraries (e.g., mapstruct, lombok) and for plugins
- dependencyManagement
  - Import Spring Boot BOM: org.springframework.boot:spring-boot-dependencies:${spring-boot.version}
  - Declare internal modules for consumer convenience at ${project.version}
- pluginManagement
  - Pin maven-compiler-plugin, maven-surefire-plugin, and spring-boot-maven-plugin
- Build plugins
  - Attach -sources.jar and -javadoc.jar for all modules

Illustrative snippets (actual values in libraries-parent/pom.xml)
```xml
<properties>
  <java.version>21</java.version>
  <spring-boot.version>3.5.7</spring-boot.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring-boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    <!-- Internal modules -->
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>common-utilities</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>security-utilities</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>com.shdev</groupId>
      <artifactId>oms-db-libraries</artifactId>
      <version>${project.version}</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

## 4) Modules – Minimal rules
- Each module inherits from libraries-parent.
- Declare only artifactId/name and direct dependencies (no versions if managed).
- For Boot configuration metadata in a module, add optional spring-boot-configuration-processor.
- If a module uses MapStruct or Lombok, add those dependencies and, if needed, configure annotation processors per-module.

Inter-module dependency example (no version needed)
```xml
<dependency>
  <groupId>com.shdev</groupId>
  <artifactId>common-utilities</artifactId>
</dependency>
```

---

## 5) Using libraries in Spring Boot services (no Spring Boot parent)
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

<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
  </plugins>
</build>
```
Why this works: libraries-parent imports the Spring Boot BOM and pins plugin versions, so services don’t need spring-boot-starter-parent.

---

## 6) Versioning and Releases
- Semantic Versioning for the monorepo: MAJOR.MINOR.PATCH applies to all modules via project.version.
- Bump project version once; children inherit automatically.
- Recommended: release all modules together for each version to keep the BOM consistent.
- Advanced: selective deploy is possible but ensure the BOM doesn’t reference non-published modules at that version.

Windows-friendly workflows
```bat
:: Bump repo-wide version
mvnw.cmd -B versions:set -DnewVersion=1.2.0 -DprocessAllModules -DgenerateBackupPoms=false

:: Build and deploy all modules
mvnw.cmd -B -DskipTests deploy

:: Deploy a single module (build required deps too)
mvnw.cmd -B -pl security-utilities -am -DskipTests deploy

:: Update Spring Boot version centrally
mvnw.cmd -B versions:set-property -Dproperty=spring-boot.version -DnewVersion=3.5.8 -DgenerateBackupPoms=false
```
Notes
- Reactor builds compute order automatically; avoid cyclic dependencies.
- SNAPSHOTs are more tolerant for partial deploys; for releases, ensure all artifacts referenced by the BOM exist.

---

## 7) Publishing (own repository)
Recommended: Sonatype Nexus Repository OSS
- Pros: easy to host, proxy Maven Central, supports anonymous reads, widely used.
- Configure distributionManagement in libraries-parent and credentials in CI settings.xml.

distributionManagement (placeholder using shdev domain)
```xml
<distributionManagement>
  <repository>
    <id>internal-releases</id>
    <url>https://nexus.shdev.local/repository/maven-releases/</url>
  </repository>
  <snapshotRepository>
    <id>internal-snapshots</id>
    <url>https://nexus.shdev.local/repository/maven-snapshots/</url>
  </snapshotRepository>
</distributionManagement>
```

CI settings.xml (written by the pipeline)
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

---

## 8) CI/CD (minimal)
- Build on PRs/branches: ./mvnw -B verify
- Release on tag vX.Y.Z: ./mvnw -B deploy (with settings.xml and distributionManagement configured)

---

## 9) Acceptance Criteria
- [x] Root aggregator builds at least two modules (common-utilities, security-utilities, oms-db-libraries).
- [x] libraries-parent provides centralized properties, dependencyManagement (imports Spring Boot BOM), and pluginManagement.
- [x] Modules inherit from libraries-parent and do not declare versions for managed deps/plugins.
- [x] Host Spring Boot services inherit from libraries-parent (not Boot parent) and consume modules without versions.
- [x] Inter-module dependencies work without specifying versions.
- [x] CI can deploy snapshots/releases to the chosen repository via distributionManagement + settings.xml.
