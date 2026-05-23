## 1. AppCrashedScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/AppCrashedScreen.kt` with M3 Scaffold + TopAppBar + scrollable monospace log text
- [x] 1.2 Accept `logs: String` parameter and display in `SelectionContainer` with `FontFamily.Monospace`
- [x] 1.3 Show loading indicator when logs are empty/null

## 2. Activity Migration

- [x] 2.1 Rewrite `app/.../AppCrashedActivity.kt` to extend `ComponentActivity` with `setContent`
- [x] 2.2 Load crash logs via coroutine in `onCreate`, pass to `AppCrashedScreen` as state
- [x] 2.3 Verify `singleTask` launch mode preserved in AndroidManifest.xml

## 3. Verification

- [x] 3.1 Build project successfully (`./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin`)
- [x] 3.2 Remove old `AppCrashedDesign.kt` after Compose screen is verified working
