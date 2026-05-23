## 1. AppSettingsScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/AppSettingsScreen.kt` with M3 Scaffold + TopAppBar + preference list
- [x] 1.2 Implement switch preference items (autoRestart, dynamicNotification, hideAppIcon, hideFromRecents)
- [x] 1.3 Implement dark mode selectable list with AlertDialog showing radio options
- [x] 1.4 Organize preferences into categories (Behavior, Interface, Service)

## 2. Activity Migration

- [x] 2.1 Rewrite `app/.../AppSettingsActivity.kt` to extend `ComponentActivity` with `setContent`
- [x] 2.2 Read stores (UiStore, ServiceStore) and pass state + callbacks to composable
- [x] 2.3 Handle event-driven recreation (dark mode change, hide from recents)
- [x] 2.4 Implement autoRestart via PackageManager and hideIcon via PackageManager

## 3. Verification

- [x] 3.1 Build project successfully (`./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin`)
- [x] 3.2 Remove old `AppSettingsDesign.kt` after Compose screen is verified working
