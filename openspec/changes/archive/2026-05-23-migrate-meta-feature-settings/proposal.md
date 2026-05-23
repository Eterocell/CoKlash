## Why

MetaFeatureSettingsActivity is next in the migration order (Priority 6). It's a preference-list screen for Clash Meta kernel override settings (unified delay, geodata mode, TCP concurrent, find process mode, sniffer configuration, and geo file imports). Migrating it continues the systematic View→Compose transition.

## What Changes

- Create `MetaFeatureSettingsScreen.kt` composable in `design/src/main/java/com/github/kr328/clash/design/compose/`
- Rewrite `MetaFeatureSettingsActivity` to extend `BaseComposeActivity` and use `setContent { }`
- Remove `MetaFeatureSettingsDesign.kt` and its XML layout
- Implement tri-state selectable lists (null/true/false → "Don't modify"/"Enabled"/"Disabled")
- Implement editable text list dialogs for port/domain lists
- Implement sniffer dependency logic (disable sub-preferences when sniffer is off)
- Implement geo file import actions (file picker → validate extension → copy to clash dir)
- Implement reset override confirmation dialog

## Scope

- Files created: `design/.../compose/MetaFeatureSettingsScreen.kt`
- Files modified: `app/.../MetaFeatureSettingsActivity.kt`
- Files deleted: `design/.../MetaFeatureSettingsDesign.kt`, associated XML layout
- No changes to `ConfigurationOverride` model or core module
