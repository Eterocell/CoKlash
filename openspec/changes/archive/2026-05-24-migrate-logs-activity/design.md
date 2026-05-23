## Context
LogsActivity shows a list of log files with dates, supports opening individual files, deleting all logs, and navigating to live logcat.

## Goals
- Migrate LogsActivity to Compose + M3 using BaseComposeActivity
- Use LazyColumn for potentially long list
- Implement delete-all confirmation dialog
- Remove old LogsDesign.kt and XML layout

## Decisions
1. Use LazyColumn (not Column with verticalScroll) since log list can be long
2. Activity loads files on onStart and refreshes after delete
3. Date formatting done in composable via SimpleDateFormat
