# common-libraries (Maven monorepo)

Multi-module monorepo for shared libraries used by Spring Boot services. Centralized versioning, Spring Boot alignment via BOM, and uniform plugin configuration live in `libraries-parent`.

- Java: 21
- Parent/BOM: `com.shdev:libraries-parent`
- Modules: `common-utilities`, `security-utilities`, `oms-db-libraries`
- Artifacts publish with sources and Javadocs attached

---

## 1) Quick start (Windows)

```bat
:: Build and test everything
mvnw.cmd -B clean verify

:: Install to local ~/.m2 for trying in another project
mvnw.cmd -B -DskipTests install

:: Deploy to your repo (needs distributionManagement + settings.xml)
mvnw.cmd -B -DskipTests deploy
```

Prereqs
- JDK 21 on PATH
- Optional for deploy: `~/.m2/settings.xml` with servers matching the ids in `libraries-parent` (internal-releases/internal-snapshots)

---

## 2) Structure
```
common-libraries/
├─ pom.xml                 # root aggregator
├─ libraries-parent/       # parent + BOM (centralized versions)
├─ common-utilities/       # library module (jar)
├─ security-utilities/     # library module (jar)
└─ oms-db-libraries/       # library module (jar)
```

Key ideas
- Root POM only aggregates modules.
- All modules inherit from `libraries-parent` and do not declare explicit versions for managed dependencies/plugins.
- Inter-module deps (e.g., `security-utilities` -> `common-utilities`) require no version tags; reactor resolves order automatically.

---

## 3) Use in a Spring Boot host (no Spring Boot parent)
Add this to your service `pom.xml`:
```xml
<parent>
  <groupId>com.shdev</groupId>
  <artifactId>libraries-parent</artifactId>
  <version>X.Y.Z</version> <!-- use a released version from your repo -->
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
  <dependency>
    <groupId>com.shdev</groupId>
    <artifactId>security-utilities</artifactId>
  </dependency>
  <dependency>
    <groupId>com.shdev</groupId>
    <artifactId>oms-db-libraries</artifactId>
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
Why this works: the parent imports the Spring Boot BOM and pins plugin versions, so your service doesn’t need `spring-boot-starter-parent`.

---

## 4) Version management (centralized)
All modules share the repo version (`project.version`) from `libraries-parent`. Spring Boot and plugin versions are properties in the parent.

Common commands (Windows CMD)
```bat
:: Show current project version (from repo root)
mvnw.cmd -q -DforceStdout help:evaluate -Dexpression=project.version

:: Bump the repo-wide version (applies to ALL modules)
mvnw.cmd -B versions:set -DnewVersion=0.1.1 -DprocessAllModules -DgenerateBackupPoms=false

:: Update Spring Boot version (single source of truth in parent)
mvnw.cmd -B versions:set-property -Dproperty=spring-boot.version -DnewVersion=3.5.8 -DgenerateBackupPoms=false

:: Update other managed properties if added later
mvnw.cmd -B versions:set-property -Dproperty=lombok.version -DnewVersion=1.18.36 -DgenerateBackupPoms=false
mvnw.cmd -B versions:set-property -Dproperty=mapstruct.version -DnewVersion=1.6.4 -DgenerateBackupPoms=false
```
Notes
- After bumping versions, commit the changes and tag if releasing.
- The BOM lists internal modules at the same version. Prefer releasing all modules together for each new version to avoid missing artifacts for consumers.

---

## 5) Selective builds and deploys (advanced)
Build or deploy only one module (reactor builds its prerequisites too):
```bat
:: Build only security-utilities and its local deps
mvnw.cmd -B -pl security-utilities -am clean install

:: Deploy only common-utilities (ensure BOM consistency before publishing it)
mvnw.cmd -B -pl common-utilities -am -DskipTests deploy
```
Caution
- If you publish the parent/BOM at version X.Y.Z, consumers may expect all listed modules to exist at X.Y.Z. Either deploy all modules, or delay publishing the BOM until all artifacts are uploaded.

---

## 6) Publishing
`libraries-parent/pom.xml` contains placeholder distributionManagement pointing to ShDev Nexus:
- Releases id: `internal-releases` -> `https://nexus.shdev.local/repository/maven-releases/`
- Snapshots id: `internal-snapshots` -> `https://nexus.shdev.local/repository/maven-snapshots/`

Example settings.xml (CI or local)
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
Deploy
```bat
:: Snapshot or release (based on project version suffix)
mvnw.cmd -B -DskipTests deploy
```
Artifacts include `-sources.jar` and `-javadoc.jar` for better IDE/repo browsing.

---

## 7) FAQ
- Q: Can a module use another (e.g., `security-utilities` use `common-utilities`)?
  - A: Yes. Declare the dependency without a version; the parent/BOM manages it and the reactor computes build order.
- Q: Do I need to update each module’s POM on version bumps?
  - A: No. Bump once at the repo root; every child inherits the new version.
- Q: Do I have to release all modules when only one changes?
  - A: Recommended yes for consistency, especially if you publish the BOM. Selective deploy is possible but manage BOM expectations carefully.

---

## 8) Useful references
- Parent POM: `libraries-parent/pom.xml` (Spring Boot BOM import, pluginManagement, distributionManagement)
- Design doc: `design/requirement.md` (concise repo requirements and CI notes)

