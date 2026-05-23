## Why

All migrated Compose activities directly extend `ComponentActivity` and independently access `Remote.broadcasts.clashRunning`. This duplicates logic and loses features from `BaseActivity` like broadcast observation, event channel, hideFromRecents enforcement, and lifecycle tracking. A `BaseComposeActivity` provides these features in a Compose-friendly way, matching `BaseActivity`'s role for the View-based screens.

## What Changes

- Create `BaseComposeActivity` extending `ComponentActivity` with ported features from `BaseActivity`
- Update all 4 already-migrated activities (HelpActivity, AppCrashedActivity, ApkBrokenActivity, AppSettingsActivity) to extend `BaseComposeActivity`
- Replace direct `Remote.broadcasts.clashRunning` references with inherited `clashRunning` property

## Capabilities

### New Capabilities

- `base-compose-activity`: Shared base class for all Compose-migrated activities

### Modified Capabilities

- `help-screen-compose`: HelpActivity extends BaseComposeActivity
- `app-crashed-screen-compose`: AppCrashedActivity extends BaseComposeActivity
- `apk-broken-screen-compose`: ApkBrokenActivity extends BaseComposeActivity
- `app-settings-screen-compose`: AppSettingsActivity extends BaseComposeActivity

## Impact

- `app/.../BaseComposeActivity.kt`: New file
- `app/.../HelpActivity.kt`: Change parent class
- `app/.../AppCrashedActivity.kt`: Change parent class
- `app/.../ApkBrokenActivity.kt`: Change parent class
- `app/.../AppSettingsActivity.kt`: Change parent class, remove Remote import
