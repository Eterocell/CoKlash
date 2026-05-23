## 1. Create LogcatScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/LogcatScreen.kt` with Scaffold, TopAppBar (mode-dependent actions), and LazyColumn for log message list

## 2. Rewrite Activity

- [x] 2.1 Rewrite `LogcatActivity` to extend `BaseComposeActivity` with `setContent`, support both streaming and file modes

## 3. Cleanup & Verify

- [x] 3.1 Delete `LogcatDesign.kt`, `LogMessageAdapter.kt`, and XML layouts
- [x] 3.2 Verify build passes
