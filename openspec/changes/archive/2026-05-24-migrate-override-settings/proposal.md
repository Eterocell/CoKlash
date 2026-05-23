## Why

OverrideSettingsActivity is next in migration order (Priority 8). It's the largest preference-list screen — manages ConfigurationOverride with General settings (ports, allow LAN, IPv6, bind address, external controller, mode, log level, hosts) and DNS settings (enable, prefer H3, listen, nameservers, fallback, fake-ip filter, fallback filter, nameserver policy). Has DNS dependency logic and reset override action.

## What Changes

- Create `OverrideSettingsScreen.kt` composable in `design/.../compose/`
- Rewrite `OverrideSettingsActivity` to extend `BaseComposeActivity` with `setContent`
- Remove `OverrideSettingsDesign.kt` and its XML layout
- Reuse TriStateListItem, EditableTextListItem, SelectionDialog from MetaFeatureSettingsScreen pattern
- Add new EditableTextItem (single nullable text field) and EditableTextMapItem (key-value map editor)

## Scope

- Files created: `design/.../compose/OverrideSettingsScreen.kt`
- Files modified: `app/.../OverrideSettingsActivity.kt`
- Files deleted: `design/.../OverrideSettingsDesign.kt`, associated XML layout
