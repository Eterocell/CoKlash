## Why

ApkBrokenActivity is Priority 3 in the migration order (Trivial). It displays a static error message when the APK is incomplete, with a single link to GitHub releases for reinstallation. Migrating it continues validating the Compose+M3 pattern for simple preference-style screens with clickable items.

## What Changes

- Replace `ApkBrokenDesign.kt` (View-based preference screen using `DesignSettingsCommonBinding`) with an `ApkBrokenScreen` Composable
- Rewrite `ApkBrokenActivity` from `BaseActivity<ApkBrokenDesign>` to `ComponentActivity` with `setContent`
- Remove the shared XML layout dependency for this screen

## Capabilities

### New Capabilities

- `apk-broken-screen-compose`: ApkBrokenActivity reimplemented as a Compose screen with error message and reinstall link

### Modified Capabilities

## Impact

- `app/.../ApkBrokenActivity.kt`: Rewrite to ComponentActivity + setContent
- `design/.../ApkBrokenDesign.kt`: Replace with `ApkBrokenScreen.kt` Composable
- No new dependencies needed — Compose infrastructure already established
