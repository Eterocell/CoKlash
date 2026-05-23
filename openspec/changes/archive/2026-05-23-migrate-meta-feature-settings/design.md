## Context

MetaFeatureSettingsActivity is a preference-list screen for Clash Meta kernel override settings. It manages `ConfigurationOverride` state with tri-state nullable Booleans (null = "Don't modify", true = "Enabled", false = "Disabled"), sniffer sub-settings with dependency logic, and geo file import actions.

## Goals / Non-Goals

**Goals:**
- Migrate MetaFeatureSettingsActivity to Compose + M3 using BaseComposeActivity
- Implement tri-state selectable list preferences (null/true/false)
- Implement editable text list dialogs for ports/domains
- Implement sniffer dependency logic (disable sub-prefs when sniffer is off)
- Implement geo file import with extension validation
- Implement reset override confirmation dialog
- Remove old MetaFeatureSettingsDesign.kt and XML layout

**Non-Goals:**
- Changing ConfigurationOverride model
- Adding ViewModel (direct mutableStateOf in Activity, same as other migrated screens)
- Migrating the commented-out GeoX URL section

## Decisions

1. **State management**: Activity holds `ConfigurationOverride` as mutable state. Each preference reads/writes directly to the configuration fields via lambda callbacks. On back/finish, configuration is patched to Clash core.
2. **Tri-state pattern**: Reusable `TriStateListItem` composable that shows current value text and opens a selection dialog on click.
3. **Editable text list**: Reusable `EditableTextListItem` composable that shows current list summary and opens an edit dialog with add/remove functionality.
4. **Sniffer dependencies**: Use `enabled` parameter on composables, derived from `snifferEnabled != false`.
5. **File import**: Activity registers `ActivityResultContracts.GetContent()` launchers, composable triggers them via callbacks. Validation and file copy logic stays in Activity.
6. **Reset action**: TopAppBar action button triggers confirmation dialog, then calls `clearOverride` and finishes.

## Risks / Trade-offs

- **Complexity**: This screen has more interactive elements than previous migrations (dialogs, file pickers, dependent state). Keeping logic in Activity rather than ViewModel is acceptable for now but may need refactoring when ViewModel migration happens.
- **Reusable composables**: TriStateListItem and EditableTextListItem will be useful for NetworkSettingsActivity and OverrideSettingsActivity migrations too.
