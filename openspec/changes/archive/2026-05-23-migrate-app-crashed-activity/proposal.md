## Why

AppCrashedActivity is the second-simplest screen (Priority 2). It displays crash logs with zero user interaction beyond scrolling. Migrating it reinforces the Compose+M3 patterns established in HelpActivity and validates the pattern for screens that receive data asynchronously (logs loaded via coroutine).

## What Changes

- Replace `AppCrashedDesign.kt` (View-based, uses `DesignAppCrashedBinding`) with an `AppCrashedScreen` Composable
- Rewrite `AppCrashedActivity` from `BaseActivity<AppCrashedDesign>` to `ComponentActivity` with `setContent`
- Remove the XML layout dependency (`design_app_crashed.xml`) for this screen

## Capabilities

### New Capabilities

- `app-crashed-screen-compose`: AppCrashedActivity reimplemented as a Compose screen displaying crash logs

### Modified Capabilities

## Impact

- `app/.../AppCrashedActivity.kt`: Rewrite to ComponentActivity + setContent + ViewModel
- `design/.../AppCrashedDesign.kt`: Replace with `AppCrashedScreen.kt` Composable
- No new dependencies needed — Compose infrastructure already established
