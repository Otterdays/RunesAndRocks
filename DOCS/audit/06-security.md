<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Audit: Security Posture

**System:** Runes & Rocks + OtterEngine V1  
**Scope:** CVEs, attack surface, hardening status, secrets management  
**Last Updated:** 2026-02-23

---

## Dependency Security Audit

### Known CVE Mitigations

| CVE | Component | Status | Mitigation |
|-----|-----------|--------|------------|
| CVE-2025-68161 | Apache Log4j | **RESOLVED** | Forced to 2.25.3+ in root `build.gradle.kts` |

### Dependency Versions (Current)

| Library | Version | License | Security Notes |
|---------|---------|---------|----------------|
| Kotlin | 2.3.10 | Apache 2.0 | Current stable |
| Ktor | 3.4.0 | Apache 2.0 | Regular updates |
| LibGDX | 1.14.0 | Apache 2.0 | Stable, widely used |
| Kryo | 5.6.2 | BSD-3 | Binary serialization |
| PostgreSQL JDBC | 42.7.10 | BSD-2 | Current stable |
| Redis (Jedis) | 7.3.0 | MIT | Pool-based |
| Exposed ORM | 1.0.0 | Apache 2.0 | JetBrains maintained |
| HikariCP | 7.0.2 | Apache 2.0 | Industry standard pool |
| Jackson | 2.18.2 | Apache 2.0 | Admin JSON only |

## Attack Surface Assessment

### Network Surface

| Endpoint | Port | Exposure | Risk |
|----------|------|----------|------|
| Game TCP | 25565 | Public (intended) | **Medium** — No rate limiting yet |
| Admin HTTP/WebSocket | 8080 | Localhost only (127.0.0.1) | **Low** — Not externally bound |
| PostgreSQL | 5432 | Docker network only | **Low** — Not exposed to host |
| Redis | 6379 | Docker network only | **Low** — Not exposed to host |

### Input Validation Gaps

| Input | Validation | Risk |
|-------|-----------|------|
| Username (LoginRequest) | No length/sanitization/trim | **Medium** — Empty string, whitespace accepted; DB truncates at 50 |
| MoveRequest velocity | No bounds checking | **Medium** — Client could send huge values |
| Packet payload | Kryo class registration | Low — Only registered classes deserialize |
| Packet payload length | No max cap in PacketCodec.read() | **Medium** — Client sends huge `length` → OOM |

## Secrets Management

| Secret | Location | Status |
|--------|----------|--------|
| Database password | Environment variable (`DB_PASS`) | **CORRECT** |
| Redis password | Not used (default config) | Low risk — internal network only |
| JDBC URL | Environment variable (`JDBC_URL`) | **CORRECT** |

**No secrets in code:** Verified no hardcoded credentials in source.

## Server-Side Authority Validation

| Action | Authority | Implementation Status |
|--------|-----------|----------------------|
| Player movement | Server-only | **VALIDATED** — Velocity applied by server, position authoritative |
| Position update | Server-only | **VALIDATED** — No client-side position packets |
| Player spawn | Server-only | **VALIDATED** — DB-driven spawn position |
| Player disconnect | Server-only | **VALIDATED** — Socket state drives cleanup |

## Current Vulnerabilities

| Issue | Severity | Notes | Timeline |
|-------|----------|-------|----------|
| No packet rate limiting | **Medium** | Client could spam MoveRequest; floods task queue | Phase 10 |
| No connection limit | Low | Memory exhaustion possible | Phase 10 |
| MoveRequest velocity unbounded | **Medium** | Could set extreme velocity | Phase 9 |
| PacketCodec payload length unbounded | **Medium** | `PacketCodec.read()` allocates `ByteArray(length)`; malicious length → OOM | Phase 9 |
| Duplicate LoginRequest | Low | Client can send LoginRequest twice before spawn; second overwrites entityId, first entity leaked | Phase 9 |
| Username empty/whitespace | Low | `loginPlayer("")` creates player; no trim | Phase 9 |
| Admin endpoints no auth | Low | Kick, GC, status — anyone on admin port has full control | Phase 10 |
| Admin dashboard accessible if port forwarded | Low | Bound to 127.0.0.1 | Document only |
| Docker socket exposed to server container | Low | Read-only `docker ps` | Acceptable risk |

## Hardening Recommendations

### Immediate (Pre-Production)

1. **Rate limiting:** Per-client packet throttle (e.g., 60 MoveRequest/sec max)
2. **Velocity clamping:** Max speed check in MovementSystem
3. **Connection limit:** Configurable max concurrent players
4. **PacketCodec max payload:** Cap `length` in `PacketCodec.read()` (e.g. 64KB) before `ByteArray` allocation
5. **Username validation:** Reject empty/whitespace; trim; enforce length before DB

### Near-Term (Phase 10)

1. **Authentication tokens:** JWT or session tokens beyond username-only
2. **TLS for TCP:** Optional TLS wrapper for game socket
3. **Admin authentication:** Password/login for admin dashboard
4. **Audit logging:** Structured logging of admin actions

### Long-Term

1. **Anti-cheat validation:** Server-side movement sanity checks (distance/time)
2. **DDoS protection:** Connection rate limiting, IP ban list
3. **Secrets rotation:** Support for rotating DB credentials without restart

## Security Audit Log

| Date | Event | Status |
|------|-------|--------|
| 2026-02-23 | Log4j CVE-2025-68161 mitigation verified | ✅ Active |
| 2026-02-23 | No hardcoded secrets found | ✅ Verified |
| 2026-02-23 | Server-authoritative design confirmed | ✅ Validated |
| 2026-02-23 | Rate limiting gap identified | ⚠️ Phase 10 |
| 2026-02-23 | Code audit: PacketCodec OOM, username validation, duplicate login, admin auth | ⚠️ Documented |

---

## Audit Trail

| Date | Entry | Author |
|------|-------|--------|
| 2026-02-23 | Initial security audit, Phase 8 baseline | Claude |
| 2026-02-23 | Follow-up audit: PacketCodec, PlayerRepository, GameServer, AdminRoutes reviewed | Claude |
