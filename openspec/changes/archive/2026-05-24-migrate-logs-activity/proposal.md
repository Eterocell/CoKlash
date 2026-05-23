## Why

LogsActivity is next in migration order (Priority 9). It's a medium-complexity screen that shows a list of log files with their dates, supports opening individual files (navigates to LogcatActivity), deleting all logs with confirmation, and navigating to live logcat view.

## What Changes

- Create `LogsScreen.kt` composable in `design/.../compose/`
- Rewrite `LogsActivity` to extend `BaseComposeActivity` with `setContent`
- Remove `LogsDesign.kt`, `LogFileAdapter.kt`, and associated XML layout

## Scope

- Files created: `design/.../compose/LogsScreen.kt`
- Files modified: `app/.../LogsActivity.kt`
- Files deleted: `design/.../LogsDesign.kt`, `design/.../adapter/LogFileAdapter.kt`, associated XML layout
