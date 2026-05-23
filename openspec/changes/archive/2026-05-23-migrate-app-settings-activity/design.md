## Context

AppSettingsActivity is a preference list screen using the custom preference DSL (`preferenceScreen`, `category`, `switch`, `selectableList`). It reads/writes to `UiStore` (SharedPreferences) and `ServiceStore`. The Activity implements `Behavior` interface for `autoRestart` (toggles a BroadcastReceiver via PackageManager). It handles events to recreate all activities when theme changes.

Note: The existing `AppSettingsDesign.kt` has duplicated preference sections (service category appears 3x). The Compose version will deduplicate to the correct unique set.

## Goals / Non-Goals

**Goals:**
- Migrate AppSettingsActivity to Compose with proper state management
- Implement preference-style UI (switches, selectable list dialog)
- Handle dark mode changes by recreating activities
- Handle hide icon changes via PackageManager

**Non-Goals:**
- Introducing ViewModel (stores are simple SharedPreferences delegates, read directly)
- Changing the store mechanism (UiStore/ServiceStore stay as-is)
- Adding new settings

## Decisions

### 1. Read stores directly in Activity, pass state to composable

**Choice:** Activity creates stores, reads initial values into `mutableStateOf`, passes them + callbacks to the composable.
**Rationale:** Stores use property delegates on SharedPreferences — they're synchronous reads. No need for Flow/StateFlow wrapper. Compose state triggers recomposition on change.

### 2. Use AlertDialog for dark mode selection

**Choice:** Show a Compose `AlertDialog` with radio buttons for dark mode selection.
**Rationale:** Matches the existing `selectableList` behavior — a dialog with options.

### 3. Deduplicate the service category

**Choice:** Keep only unique preferences: autoRestart, darkMode, dynamicNotification, hideAppIcon, hideFromRecents.
**Rationale:** The original has duplicated sections (likely a merge artifact). The Compose version corrects this.

## Risks / Trade-offs

- **Risk:** Recreating all activities on dark mode change may flash — same behavior as before, acceptable.
- **Trade-off:** Direct store access without ViewModel means rotation re-reads from SharedPreferences — fast and acceptable for a settings screen.
