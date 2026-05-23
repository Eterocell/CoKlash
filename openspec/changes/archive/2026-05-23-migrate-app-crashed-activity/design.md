## Context

AppCrashedActivity displays crash logs when the app encounters an unrecoverable error. It uses `AppCrashedDesign` which inflates `design_app_crashed.xml` — a simple layout with an app bar and a scrollable text view showing log output. The screen receives log data asynchronously via `SystemLogcat.dumpCrash()` and has zero user interaction beyond scrolling.

Compose infrastructure (BOM, M3, CoKlashTheme) is already established from the HelpActivity migration.

## Goals / Non-Goals

**Goals:**
- Migrate AppCrashedActivity to Compose using the established pattern
- Display crash logs in a scrollable, monospace text view
- Maintain the `singleTask` launch mode behavior

**Non-Goals:**
- Adding new features (copy-to-clipboard, share, etc.)
- Changing the crash log collection mechanism (`SystemLogcat.dumpCrash()`)
- Introducing ViewModel (screen is simple enough without one — just pass logs as state)

## Decisions

### 1. Pass logs as state parameter, not ViewModel

**Choice:** Load logs in the Activity's `onCreate` via coroutine, pass result to `AppCrashedScreen(logs)`.
**Rationale:** The screen has exactly one piece of state (the log string) loaded once. A ViewModel adds unnecessary complexity for a fire-and-forget display.
**Alternative considered:** ViewModel with StateFlow — rejected as over-engineering for a single immutable string.

### 2. Monospace font for log display

**Choice:** Use `FontFamily.Monospace` for the log text to preserve alignment.
**Rationale:** Crash logs contain stack traces where alignment matters for readability.

### 3. Keep singleTask launch mode

**Choice:** Preserve `android:launchMode="singleTask"` in manifest.
**Rationale:** Prevents multiple crash screens stacking. Existing behavior must be maintained.

## Risks / Trade-offs

- **Risk:** Large log text may cause performance issues in Compose Text → Mitigation: Use `SelectionContainer` for copy support but avoid `BasicTextField`. Compose handles large text well with lazy measurement.
- **Trade-off:** No ViewModel means configuration change (rotation) re-loads logs — acceptable since crash screen is rarely rotated and logs load fast from logcat buffer.
