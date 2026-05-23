## Why

LogcatActivity is next in migration order (Priority 10). It's a streaming log view with two modes: live streaming (from LogcatService) and viewing archived log files. Each mode has different actions: live mode supports close/stop service, file mode supports delete/export.

## What Changes

- Create `LogcatScreen.kt` composable in `design/.../compose/`
- Rewrite `LogcatActivity` to extend `BaseComposeActivity` with `setContent`
- Remove `LogcatDesign.kt`, `LogMessageAdapter.kt`, and associated XML layouts

## Scope

- Files created: `design/.../compose/LogcatScreen.kt`
- Files modified: `app/.../LogcatActivity.kt`
- Files deleted: `design/.../LogcatDesign.kt`, `design/.../adapter/LogMessageAdapter.kt`, associated XML layouts
