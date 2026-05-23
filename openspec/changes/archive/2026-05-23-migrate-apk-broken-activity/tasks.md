## 1. ApkBrokenScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/ApkBrokenScreen.kt` with M3 Scaffold + TopAppBar + error tip + reinstall link
- [x] 1.2 Display `application_broken_tips` with `AnnotatedString.fromHtml` for bold formatting
- [x] 1.3 Display "Github Releases" as a clickable ListItem under "Reinstall" category header

## 2. Activity Migration

- [x] 2.1 Rewrite `app/.../ApkBrokenActivity.kt` to extend `ComponentActivity` with `setContent`
- [x] 2.2 Pass `onLinkClick` lambda that launches ACTION_VIEW intent with meta_github_url

## 3. Verification

- [x] 3.1 Build project successfully (`./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin`)
- [x] 3.2 Remove old `ApkBrokenDesign.kt` after Compose screen is verified working
