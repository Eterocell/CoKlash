## 1. Build System Setup (Compose + M3 Dependencies)

- [x] 1.1 Add Compose BOM, M3, and activity-compose versions to `gradle/libs.versions.toml`
- [x] 1.2 Add Compose BOM, material3, ui, ui-tooling-preview, and activity-compose library entries to `gradle/libs.versions.toml`
- [x] 1.3 Add Compose compiler plugin to `build-logic` convention or project-level build config
- [x] 1.4 Add Compose dependencies to `design/build.gradle.kts` (material3, ui, foundation, ui-tooling-preview)
- [x] 1.5 Add activity-compose dependency to `app/build.gradle.kts`
- [x] 1.6 Enable `buildFeatures.compose = true` in design and app modules
- [x] 1.7 Verify Gradle sync succeeds without version conflicts

## 2. Theme Foundation (CoKlashTheme)

- [x] 2.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/theme/Color.kt` with light/dark color schemes extracted from existing `AppThemeLight`/`AppThemeDark`
- [x] 2.2 Create `design/src/main/java/com/github/kr328/clash/design/compose/theme/Theme.kt` with `CoKlashTheme` composable wrapping MaterialTheme (light/dark/dynamic color support)
- [x] 2.3 Create `design/src/main/java/com/github/kr328/clash/design/compose/theme/Type.kt` with M3 Typography if custom fonts are used

## 3. HelpScreen Composable

- [x] 3.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/HelpScreen.kt` with M3 Scaffold + TopAppBar + categorized link list
- [x] 3.2 Implement link items as clickable ListItem composables with title and URL summary
- [x] 3.3 Implement category headers matching "Document" and "Sources" sections
- [x] 3.4 Add tips/description text at the top matching existing `tips_help` content

## 4. Activity Migration

- [x] 4.1 Rewrite `app/.../HelpActivity.kt` to extend `ComponentActivity` with `setContent { CoKlashTheme { HelpScreen(...) } }`
- [x] 4.2 Pass `onLinkClick` lambda that launches ACTION_VIEW intent
- [x] 4.3 Pass `onBackClick` lambda that calls `finish()`
- [x] 4.4 Verify HelpActivity still declared correctly in AndroidManifest.xml

## 5. Verification

- [x] 5.1 Build project successfully (`./gradlew assembleDebug`)
- [x] 5.2 Verify no Kotlin compiler errors in changed files
- [x] 5.3 Verify HelpScreen renders correctly in light and dark themes (Preview)
- [x] 5.4 Remove old `HelpDesign.kt` after Compose screen is verified working
