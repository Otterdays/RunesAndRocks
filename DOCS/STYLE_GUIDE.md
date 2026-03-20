<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Style Guide

Project-specific conventions for Runes & Rocks. Aligns with user rules where applicable.

---

## Naming

| Context | Convention | Example |
|---------|------------|---------|
| Kotlin (classes, functions, vars) | camelCase | `playerPosition`, `getChunkAt()` |
| Kotlin (types, objects) | PascalCase | `PlayerMovePacket`, `MovementSystem` |
| Constants | UPPER_SNAKE_CASE | `TICKS_PER_SECOND`, `DEFAULT_PORT` |
| Packages | lowercase | `com.runesandrocks.shared.network` |
| Files | Match primary type | `PlayerMovePacket.kt`, `MovementSystem.kt` |
| CSS (if any) | kebab-case | `.player-health-bar` |

---

## Code Limits

- **Line length:** 100 characters
- **Function length:** 50 lines
- **File length:** 400 lines

Split or extract when exceeded.

---

## Trace Tags

Link code to docs with:

```kotlin
// [TRACE: ARCHITECTURE.md]
```

Use for non-obvious design decisions, not every function.

---

## Comments

- **WHY only**, never WHAT. Types and names document behavior.
- Prefixes: `TODO:`, `FIXME:`, `NOTE:`
- No commented-out code in commits.

---

## Types Over Comments

Prefer explicit types and sealed interfaces over long comments. Let Kotlin document intent.

---

## Secrets

Never in code. Use `.env` (gitignored). Document required env vars in README or DOCS.
