## 1. Create LogsScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/LogsScreen.kt` with Scaffold, TopAppBar (back + logcat + delete actions), and LazyColumn for log file list

## 2. Rewrite Activity

- [x] 2.1 Rewrite `LogsActivity` to extend `BaseComposeActivity` with `setContent`, load files on start, handle navigation

## 3. Cleanup & Verify

- [x] 3.1 Delete `LogsDesign.kt`, `LogFileAdapter.kt`, and XML layout
- [x] 3.2 Verify build passes
