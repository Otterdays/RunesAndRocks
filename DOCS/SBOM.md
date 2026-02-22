# Software Bill of Materials (SBOM)

Security tracking for all packages. Update on every install/remove.

---

## Installed

| Package | Version | Module | Purpose |
|---------|---------|--------|---------|
| kotlin-stdlib | 2.3.0 | all | Kotlin runtime (JDK 25–compatible) |
| junit-jupiter | 5.10.2 | server (test) | Unit tests |
| junit-platform-launcher | (resolved with Jupiter) | server (test) | JUnit 5 test execution |
| kryo | 5.6.0 | shared | Binary serialization |
| gdx | 1.12.1 | shared, client | LibGDX core |
| ktor-network | 3.4.0 | server, client | TCP sockets |
| ktor-io | 3.4.0 | shared | ByteReadChannel/ByteWriteChannel for packet codec |
| ktor-server-cio | 3.4.0 | server | Admin HTTP engine |
| ktor-server-content-negotiation | 3.4.0 | server | JSON responses |
| ktor-serialization-jackson | 3.4.0 | server | Jackson for admin API |
| ktor-server-websockets | 3.4.0 | server | Live dashboard push |
| slf4j-simple | 2.0.12 | server, client | Logging |
| gdx-backend-lwjgl3 | 1.12.1 | client | Desktop backend |
| gdx-platform | 1.12.1 | client | LWJGL natives |

---

## Planned (not yet added)

| Package | Version | Module | Purpose |
|---------|---------|--------|---------|
| gdx-backend-headless | 1.12.1 | server | Headless LibGDX for collision math |

Add rows when packages are actually added to `build.gradle.kts`.
