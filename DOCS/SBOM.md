# Software Bill of Materials (SBOM)

Security tracking for all packages. Update on every install/remove.

**CVE mitigation:** Root `build.gradle.kts` forces `org.apache.logging.log4j:log4j-core:2.25.3` if pulled in transitively (e.g. by HikariCP) to address CVE-2025-68161.

---

## Installed

| Package | Version | Module | Purpose |
|---------|---------|--------|---------|
| kotlin-stdlib | 2.3.10 | all | Kotlin runtime (JDK 25–compatible) |
| junit-jupiter | 5.12.2 | server (test) | Unit tests |
| junit-platform-launcher | (resolved with Jupiter) | server (test) | JUnit 5 test execution |
| kryo | 5.6.2 | shared | Binary serialization |
| gdx | 1.14.0 | shared, client | LibGDX core |
| ktor-network | 3.4.0 | server, client | TCP sockets |
| ktor-io | 3.4.0 | shared | ByteReadChannel/ByteWriteChannel for packet codec |
| ktor-server-cio | 3.4.0 | server | Admin HTTP engine |
| ktor-server-content-negotiation | 3.4.0 | server | JSON responses |
| ktor-serialization-jackson | 3.4.0 | server | Jackson for admin API |
| ktor-server-websockets | 3.4.0 | server | Live dashboard push |
| ktor-server-call-logging | 3.4.0 | server | Admin HTTP request logging |
| logback-classic | 1.5.6 | server | SLF4J binding + custom RingBufferAppender for admin log tail |
| slf4j-simple | 2.0.17 | client | Logging (server uses Logback) |
| gdx-backend-lwjgl3 | 1.14.0 | client | Desktop backend |
| gdx-platform | 1.14.0 | client | LWJGL natives |
| exposed-core (dao, jdbc) | 1.0.0 | server | JetBrains ORM framework |
| postgresql | 42.7.10 | server | Base relational database driver |
| HikariCP | 7.0.2 | server | SQL connection pooling |
| jedis | 7.3.0 | server | Fast synchronous Redis cache pooling |

---

## Planned (not yet added)

| Package | Version | Module | Purpose |
|---------|---------|--------|---------|
| gdx-backend-headless | 1.14.0 | server | Headless LibGDX for collision math |

Add rows when packages are actually added to `build.gradle.kts`.
