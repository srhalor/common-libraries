# common-libraries (Maven monorepo)

Multi-module monorepo for shared libraries used by Spring Boot services. Centralized versioning and Spring Boot alignment live in `libraries-parent`, which inherits `spring-boot-starter-parent`.

- Java: 21
- Parent: `com.shdev:libraries-parent` (inherits `org.springframework.boot:spring-boot-starter-parent`)
- Modules: `common-utilities`, `security-utilities`, `oms-db-libraries`
- Artifacts publish with sources and Javadocs attached

---

## 1) Quick start (Windows)

```bat
:: Build and test everything (always run from repo root so ${revision} resolves)
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
├─ pom.xml                 # root aggregator (no parent logic here)
├─ libraries-parent/       # shared parent (inherits Spring Boot parent; centralizes versions)
├─ common-utilities/       # library module (jar)
├─ security-utilities/     # library module (jar)
└─ oms-db-libraries/       # library module (jar)
```

Key ideas
- Root POM only aggregates modules.
- All modules inherit from `libraries-parent` and do not declare explicit versions for dependencies/plugins covered by Spring Boot parent.
- Inter-module deps (e.g., `security-utilities` -> `common-utilities`) require no version tags; reactor resolves order automatically.

---

## 3) Use in a Spring Boot host (no Spring Boot parent in service)
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
Why this works: `libraries-parent` already inherits Spring Boot’s parent, so your service doesn’t need `spring-boot-starter-parent`.

---

## 4) Version management (single source of truth in libraries-parent)
All internal library versions are tied to the parent `libraries-parent` via the `revision` property and `dependencyManagement`.
- Children declare their parent as:
  ```xml
  <parent>
    <groupId>com.shdev</groupId>
    <artifactId>libraries-parent</artifactId>
    <version>${revision}</version>
    <relativePath>../libraries-parent/pom.xml</relativePath>
  </parent>
  ```
- Always build from the repository root (aggregator) so `${revision}` resolves properly.

Common tasks (Windows CMD)
```bat
:: Show current parent revision (from repo root)
mvnw.cmd -q -pl libraries-parent -DforceStdout help:evaluate -Dexpression=revision

:: Bump the parent revision ONLY (children inherit automatically; no edits in child POMs)
mvnw.cmd -B -pl libraries-parent -am versions:set-property -Dproperty=revision -DnewVersion=0.1.1 -DgenerateBackupPoms=false

:: (Optional) Verify effective managed version for internal libs
mvnw.cmd -q -pl libraries-parent -DforceStdout help:evaluate -Dexpression=shdev.libraries.version
```
Notes
- Do NOT edit child POMs when bumping versions; only change `revision` in `libraries-parent/pom.xml`.
- Because you build from the root, `${revision}` in child `<parent>` sections resolves correctly.
- For Spring Boot upgrades, update the literal `<parent><version>` in `libraries-parent` and keep other changes property-driven.

---

## 5) Selective builds and deploys (advanced)
Build or deploy only one module (reactor builds its prerequisites too):
```bat
:: Build only security-utilities and its local deps
mvnw.cmd -B -pl security-utilities -am clean install

:: Deploy only common-utilities (ensure parent expectations before publishing it)
mvnw.cmd -B -pl common-utilities -am -DskipTests deploy
```
Caution
- If you publish the parent at version X.Y.Z, consumers may expect all listed modules to exist at X.Y.Z. Either deploy all modules, or delay publishing until all artifacts are uploaded.

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
- Q: Do I need to update each module’s POM on version bumps?
  - A: No. Update `revision` in `libraries-parent/pom.xml`; all children inherit through their `<parent>`.
- Q: Can a module use another (e.g., `security-utilities` use `common-utilities`)?
  - A: Yes. Declare the dependency without a version; the parent/BOM manages it and the reactor computes build order.
- Q: Do I have to release all modules when only one changes?
  - A: Recommended yes for consistency, especially if you publish the BOM. Selective deploy is possible but manage BOM expectations carefully.

---

## 8) Useful references
- Parent POM: `libraries-parent/pom.xml` (central properties and dependencyManagement)
- Design doc: `design/requirement.md` (concise repo requirements and CI notes)
