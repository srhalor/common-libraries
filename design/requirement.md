# common-libraries monorepo – Requirements (Concise)

Owner: Shailesh
Last updated: 2025-10-24

## 0) Purpose
- Single Maven monorepo to develop, test, and publish multiple custom libraries for Spring Boot applications.
- Centralize ALL versioning (project, Spring Boot, dependencies, plugins) in one parent/BOM to simplify upgrades and keep services consistent.
- Allow inter-module dependencies and provide a simple consumer experience in host services (without using Spring Boot parent).

---

## 1) Architecture and Layout
- Root aggregator POM (packaging=pom) orchestrates the build.
- libraries-parent (packaging=pom) acts as Parent + BOM:
  - properties: java.version, spring-boot.version, and key dependency/plugin versions
  - dependencyManagement: import Spring Boot BOM and pin any extras
  - pluginManagement: standardize compiler, surefire, boot plugin, etc.
- Library modules (packaging=jar): common-utilities, security-utilities, (optional) logging-utilities, test-utilities.
- Optional Spring Boot “starter” modules to expose auto-configuration.

Proposed structure
```
common-libraries/
├─ pom.xml                  # root aggregator
├─ libraries-parent/
│  └─ pom.xml               # parent + BOM
├─ common-utilities/
│  └─ pom.xml
├─ security-utilities/
│  └─ pom.xml
└─ (optional) starters/
   ├─ common-utilities-spring-boot-starter/
   └─ security-utilities-spring-boot-starter/
```

---

## 2) Conventions and Constraints
- Java 17+; build with Maven Wrapper (mvnw/mvnw.cmd).
- No module defines <version>; all inherit project.version from libraries-parent.
- Do not set versions for managed dependencies/plugins in child modules.
- Enforce rules via Maven Enforcer (Java/Maven versions, dependency convergence).
- Inter-module dependencies are allowed (e.g., security-utilities -> common-utilities); avoid cycles.

---

## 3) Parent/BOM (libraries-parent) – Required capabilities
- Properties
  - spring-boot.version (single source of truth)
  - versions for common libs (e.g., slf4j, jackson, junit) and for plugins
- dependencyManagement
  - Import Spring Boot BOM: org.springframework.boot:spring-boot-dependencies:${spring-boot.version}
  - Optionally import additional BOMs (e.g., jackson-bom)
  - Declare internal modules for consumer convenience at ${project.version}
- pluginManagement
  - Pin maven-compiler-plugin, maven-surefire-plugin, and spring-boot-maven-plugin

Key snippets (illustrative)
```xml
<!-- In libraries-parent/pom.xml -->
<properties>
  <java.version>17</java.version>
  <spring-boot.version>3.3.4</spring-boot.version>
  <!-- others: slf4j/jackson/junit + plugin versions -->
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
    <!-- Internal modules so consumers don’t specify versions -->
    <dependency>
      <groupId>com.yourorg.libraries</groupId>
      <artifactId>common-utilities</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>com.yourorg.libraries</groupId>
      <artifactId>security-utilities</artifactId>
      <version>${project.version}</version>
    </dependency>
  </dependencies>
</dependencyManagement>

<build>
  <pluginManagement>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>${maven.compiler.plugin.version}</version>
        <configuration><release>${java.version}</release></configuration>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>${maven.surefire.plugin.version}</version>
      </plugin>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring-boot.version}</version>
      </plugin>
    </plugins>
  </pluginManagement>
</build>
```

---

## 4) Modules – Minimal rules
- Each module inherits from libraries-parent.
- Declare only groupId/artifactId/name and direct dependencies (no versions if managed).
- For Boot-specific configuration properties, add optional spring-boot-configuration-processor.
- Optional starters should provide @AutoConfiguration and register in:
  - META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports

Inter-module dependency example (no version needed)
```xml
<dependency>
  <groupId>com.yourorg.libraries</groupId>
  <artifactId>common-utilities</artifactId>
</dependency>
```

---

## 5) Using libraries in Spring Boot services (no Spring Boot parent)
Preferred: host service POM inherits from libraries-parent and declares Boot starters normally.
```xml
<parent>
  <groupId>com.yourorg.libraries</groupId>
  <artifactId>libraries-parent</artifactId>
  <version>1.0.0</version>
  <relativePath/>
</parent>

<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>com.yourorg.libraries</groupId>
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
Why this works: libraries-parent imports the Spring Boot BOM and pins plugin versions, so services don’t need the Boot parent.

---

## 6) Versioning and Releases
- Semantic Versioning for the monorepo: MAJOR.MINOR.PATCH applies to all modules via project.version.
- Bump project version once; children inherit automatically.
- Recommended: release all modules together for each version to keep BOM consistent.
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
mvnw.cmd -B versions:set-property -Dproperty=spring-boot.version -DnewVersion=3.3.5 -DgenerateBackupPoms=false
```
Notes
- Reactor builds compute order automatically; avoid cyclic dependencies.
- SNAPSHOTs are more tolerant for partial deploys; for releases, ensure all artifacts referenced by the BOM exist.

---

## 7) Publishing (choose one)
Recommended for “own repository”: Sonatype Nexus Repository OSS
- Pros: easy to host, proxy Maven Central, supports anonymous reads, widely used.
- Configure distributionManagement in libraries-parent and credentials in CI settings.xml.

distributionManagement placeholder
```xml
<distributionManagement>
  <repository>
    <id>internal-releases</id>
    <url>https://nexus.example.com/repository/maven-releases/</url>
  </repository>
  <snapshotRepository>
    <id>internal-snapshots</id>
    <url>https://nexus.example.com/repository/maven-snapshots/</url>
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

Note: If you prefer zero-ops, GitHub Packages integrates well with GitHub Actions but usually requires auth for reads.

---

## 8) CI/CD (minimal)
- Build on PRs/branches: ./mvnw -B verify
- Release on tag vX.Y.Z: ./mvnw -B deploy (with settings.xml and distributionManagement configured)

GitHub Actions (Nexus publish, minimal)
```yaml
name: release
on:
  push:
    tags: ['v*.*.*']
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
          cache: maven
      - name: Write Maven settings.xml
        run: |
          mkdir -p ~/.m2
          cat > ~/.m2/settings.xml << 'EOF'
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
          EOF
      - name: Build and deploy
        env:
          NEXUS_USERNAME: ${{ secrets.NEXUS_USERNAME }}
          NEXUS_PASSWORD: ${{ secrets.NEXUS_PASSWORD }}
        run: ./mvnw -B -DskipTests=false deploy
```

---

## 9) Acceptance Criteria
- [ ] Root aggregator builds at least two modules (common-utilities, security-utilities).
- [ ] libraries-parent provides centralized properties, dependencyManagement (imports Spring Boot BOM), and pluginManagement.
- [ ] Modules inherit from libraries-parent and do not declare versions for managed deps/plugins.
- [ ] Host Spring Boot services inherit from libraries-parent (not Boot parent) and consume modules without versions.
- [ ] Inter-module dependencies work without specifying versions.
- [ ] CI can deploy snapshots/releases to the chosen repository via distributionManagement + settings.xml.
