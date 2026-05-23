## Why

AppSettingsActivity is Priority 5 in the migration order (Low complexity). It's a preference list screen with switches and a selectable list for dark mode. This is the first migration involving mutable state (preference stores) and event-driven behavior (recreate activities on theme change). It validates the Compose pattern for settings screens.

## What Changes

- Create `AppSettingsScreen` composable with preference-style UI (switches, selectable list)
- Rewrite `AppSettingsActivity` from `BaseActivity<AppSettingsDesign>` to `ComponentActivity` with `setContent`
- Remove `AppSettingsDesign.kt` and its shared XML layout dependency

## Capabilities

### New Capabilities

- `app-settings-screen-compose`: AppSettingsActivity reimplemented as a Compose settings screen

### Modified Capabilities

## Impact

- `app/.../AppSettingsActivity.kt`: Rewrite to ComponentActivity + setContent
- `design/.../AppSettingsDesign.kt`: Replace with `AppSettingsScreen.kt` Composable
- No new dependencies needed
