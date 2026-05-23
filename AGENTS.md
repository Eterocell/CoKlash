# AGENTS.md — CoKlash

## Project Overview

CoKlash is an Android VPN client (Clash Meta GUI). Multi-module Gradle project in Kotlin.

| Module | Role |
|--------|------|
| `app` | Activities (controllers), lifecycle, navigation |
| `design` | View-based UI layer (`*Design` classes bind XML layouts) |
| `core` | Clash core bridge (JNI/native) |
| `service` | VPN service, background processing |
| `common` | Shared utilities, constants, compat layers |
| `hideapi` | Hidden Android API stubs |

## Architecture Pattern (Current)

```
Activity <-> Design <-> XML Layout
   |
BaseActivity<D : Design<*>>
   |-- coroutine lifecycle (MainScope)
   |-- Channel<Event> for system events
   |-- select {} loop for event + UI request dispatch
```

Each screen = 1 Activity + 1 Design class. `Design.root` is the inflated View.

## Migration Goal

**View-based + Material 2 → Jetpack Compose + Material 3 Expressive**

Migrate ONE activity at a time. Never batch-migrate.

### Target Architecture (Post-Migration)

```
ComponentActivity (no AppCompat)
   |-- setContent { Theme { Screen() } }
   |-- ViewModel (state holder, replaces Channel<Event> + select{})
   |-- Navigation via NavHost (replaces Intent-based navigation)
```

### Migration Order (simplest → most complex)

| Priority | Activity | Complexity | Notes |
|----------|----------|-----------|-------|
| 1 | `HelpActivity` | Trivial | Static content, no state |
| 2 | `AppCrashedActivity` | Trivial | Error display only |
| 3 | `ApkBrokenActivity` | Trivial | Error display only |
| 4 | `AboutActivity` (dialog in Main) | Low | Single dialog |
| 5 | `AppSettingsActivity` | Low | Preference list |
| 6 | `MetaFeatureSettingsActivity` | Low | Preference list |
| 7 | `NetworkSettingsActivity` | Low | Preference list |
| 8 | `OverrideSettingsActivity` | Low | Preference list |
| 9 | `LogsActivity` | Medium | List + state |
| 10 | `LogcatActivity` | Medium | Streaming log view |
| 11 | `NewProfileActivity` | Medium | Form + validation |
| 12 | `PropertiesActivity` | Medium | Dynamic form |
| 13 | `FilesActivity` | Medium | File browser |
| 14 | `ProfilesActivity` | Medium | List + CRUD |
| 15 | `ProvidersActivity` | Medium | List + network |
| 16 | `AccessControlActivity` | High | Complex list + filtering |
| 17 | `ProxyActivity` | High | Tabs + real-time data |
| 18 | `SettingsActivity` | High | Hub screen |
| 19 | `MainActivity` | High | Hub + VPN state + traffic |

### Migration Rules

1. **One screen per spec.** Each Activity migration = one OpenSpec change.
2. **Keep both paths working.** Old Activity stays until new Compose screen is verified.
3. **Design module evolves.** New Compose UI goes into `design/` as `@Composable` functions.
4. **No XML for new screens.** Migrated screens use Compose only, no hybrid.
5. **Material 3 Expressive only.** Use `androidx.compose.material3` with expressive extensions.
6. **Delete old code after verification.** Remove `*Design.kt` + XML after Compose screen passes all tests.

---

## Development Workflow

### OpenSpec + Superpowers Integration

#### Before Any Implementation Work

1. Check if active `spec.md` exists under `openspec/changes/`
2. If not, create one with `/opsx-propose`
3. Review `openspec/changes/archive/` for similar past implementations

#### During Implementation

1. Follow Superpowers TDD workflow:
   - brainstorm → write-tests → implement → run-tests → code-review
2. Update spec with implementation decisions using `/opsx-apply`
3. Keep spec in sync with actual implementation

#### After Implementation

1. Run Superpowers code review (`/requesting-code-review`)
2. Archive completed spec with `/opsx-archive`
3. Document learnings for future reference

#### ⚠️ Do NOT

- Implement without a spec (use `/opsx-propose` first)
- Let spec drift from actual implementation
- Skip the archive step after completion
- Batch-migrate multiple Activities in one spec

---

### Superpowers TDD Configuration

#### Mandatory Workflow

For EVERY implementation task:

1. **Exploration First**
   - Run brainstorming skill before coding
   - Understand existing codebase patterns
   - Identify potential conflicts

2. **TDD Cycle**
   - Write failing test FIRST
   - Implement minimum code to pass
   - Refactor if needed
   - Target 80%+ coverage

3. **Code Review**
   - Run code-review skill after implementation
   - Address all CRITICAL and HIGH issues
   - Document any accepted MEDIUM issues

#### Quality Gates

- [ ] All tests passing
- [ ] No Kotlin compiler errors
- [ ] Code review completed
- [ ] OpenSpec tasks updated
