## Why

HelpActivity is the simplest screen in CoKlash (static links, zero state). Migrating it first establishes the Compose + Material 3 Expressive foundation (theme, dependencies, base patterns) that all subsequent migrations will reuse. It validates the migration approach with minimal risk.

## What Changes

- Replace `HelpDesign.kt` (View-based preference screen) with a `HelpScreen` Composable using M3 Expressive components
- Replace `HelpActivity` extending `BaseActivity<HelpDesign>` with a standalone `ComponentActivity` using `setContent`
- Add Jetpack Compose + Material 3 dependencies to the build system
- Create the app-wide M3 Expressive theme (CoKlashTheme) reusable by all future migrations
- Remove `design_settings_common.xml` dependency for this screen (shared layout stays for other screens)

## Capabilities

### New Capabilities

- `compose-foundation`: Compose BOM, M3 Expressive dependencies, CoKlashTheme, and base infrastructure for all future Compose screens
- `help-screen-compose`: HelpActivity reimplemented as a Compose screen with clickable link list

### Modified Capabilities

## Impact

- `design/build.gradle.kts`: Add Compose + M3 dependencies
- `app/build.gradle.kts`: Add Compose + M3 dependencies
- `gradle/libs.versions.toml`: Add Compose BOM, M3, activity-compose version entries
- `app/.../HelpActivity.kt`: Rewrite to ComponentActivity + setContent
- `design/.../HelpDesign.kt`: Replace with `HelpScreen.kt` Composable
- No impact on other screens — old BaseActivity pattern remains for unmigrated screens
