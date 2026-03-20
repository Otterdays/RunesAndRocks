<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# SBOM modernization checklist

Every dependency: current version → updateable (latest known). Verify each, then check off as you update.

---

## Root / plugins

| Done | Package | Current | Updateable | Note |
|------|---------|---------|------------|------|
| [x] | kotlin (jvm + android) | 2.3.10 | 2.3.10 | Updated 2026-02 |
| [x] | com.android.application | 9.0.1 | 9.0.1 | AGP; at root, applied to android module |
| [x] | Gradle wrapper | 9.3.1 | 9.3.1 | Bumped from 9.2.1 (2026-02) |

---

## Runtime / libs

| Done | Package | Current | Updateable | Note |
|------|---------|---------|------------|------|
| [x] | kotlin-stdlib | 2.3.10 | 2.3.10 | Via Kotlin plugin |
| [x] | kryo | 5.6.2 | 5.6.2 | Current |
| [x] | gdx | 1.14.0 | 1.14.0 | Updated 2026-02 |
| [x] | gdx-backend-lwjgl3 | 1.14.0 | 1.14.0 | Bump with gdx |
| [x] | gdx-platform | 1.14.0 | 1.14.0 | Bump with gdx |
| [x] | ktor-network | 3.4.0 | 3.4.0 | Current |
| [x] | ktor-io | 3.4.0 | 3.4.0 | Current |
| [x] | ktor-server-cio | 3.4.0 | 3.4.0 | Current |
| [x] | ktor-server-content-negotiation | 3.4.0 | 3.4.0 | Current |
| [x] | ktor-serialization-jackson | 3.4.0 | 3.4.0 | Current |
| [x] | ktor-server-websockets | 3.4.0 | 3.4.0 | Current |
| [x] | ktor-server-call-logging | 3.4.0 | 3.4.0 | Added 2026-02 for admin HTTP request logging |
| [x] | slf4j-simple | 2.0.17 | 2.0.17 | Current |
| [x] | exposed-core | 1.0.0 | 1.0.0 | Migrated from 0.50.1 (2026-02); imports updated to v1.* |
| [x] | exposed-dao | 1.0.0 | 1.0.0 | Migrated with exposed-core |
| [x] | exposed-jdbc | 1.0.0 | 1.0.0 | Migrated with exposed-core |
| [x] | postgresql | 42.7.10 | 42.7.10 | Current |
| [x] | HikariCP | 7.0.2 | 7.0.2 | Upgraded from 5.1.0 |
| [x] | jedis | 7.3.0 | 7.3.0 | Upgraded from 5.1.5; migrated to JedisPooled |

---

## Test

| Done | Package | Current | Updateable | Note |
|------|---------|---------|------------|------|
| [x] | junit-jupiter | 5.12.2 | 5.12.2 | Current; JUnit 6.0.3 available (2026-02) — major migration |
| [ ] | junit-platform-launcher | (with Jupiter) | (with Jupiter) | Resolved by Jupiter |

---

## Android (client)

| Done | Package | Current | Updateable | Note |
|------|---------|---------|------------|------|
| [x] | gdx-backend-android | 1.14.0 | 1.14.0 | Bump with gdx |
| [x] | gdx-platform (natives-*) | 1.14.0 | 1.14.0 | Bump with gdx |

---

## Planned (not yet added)

| Done | Package | Current | Updateable | Note |
|------|---------|---------|------------|------|
| [x] | gdx-backend-headless | 1.14.0 | 1.14.0 | When added, use 1.14.0 |

---

## Exposed 1.0.0 migration notes (from 0.61.0 guide; we're on 0.50.1)

Migration guide: <https://www.jetbrains.com/help/exposed/migration-guide-1-0-0.html>

**Import changes:** All packages gain `org.jetbrains.exposed.v1.*` prefix. Examples:
- `org.jetbrains.exposed.sql.Table` → `org.jetbrains.exposed.v1.core.Table`
- `org.jetbrains.exposed.sql.Database` → `org.jetbrains.exposed.v1.jdbc.Database`
- `org.jetbrains.exposed.sql.transactions.transaction` → `org.jetbrains.exposed.v1.jdbc.transactions.transaction`
- `select`, `insert`, `update`, `selectAll`, `exists` → moved to `org.jetbrains.exposed.v1.jdbc.*`

**SqlExpressionBuilder:** Deprecated; use top-level functions (`eq`, `less`, `greaterEq`, etc.) from `org.jetbrains.exposed.v1.core.*` instead of `SqlExpressionBuilder.less`, etc. Add `import org.jetbrains.exposed.v1.core.eq` for `Column eq value` queries. No more `Expression.build { }` or `Op.build { }`.

**Transactions:** `Transaction` is now abstract; use `JdbcTransaction` for JDBC. `Transaction.id` → `Transaction.transactionId`. `transaction()` param order changed; `db` is first.

**Kotlin:** 1.0.0 requires Kotlin 2.2+ (we have 2.3.10 ✓). Datetime artifacts need kotlin-stdlib 2.1.20+.

**MigrationUtils:** If used, replace `exposed-migration` with `exposed-migration-core` + `exposed-migration-jdbc`.

---

*After updating a row: bump version in the right `build.gradle.kts`, then update `DOCS/SBOM.md`, then check the box here.*
