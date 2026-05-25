## ADDED Requirements

### Requirement: AppViewModel for shared application state
A single `AppViewModel` scoped to `MainActivity` SHALL hold shared application state including VPN running status and broadcast events.

#### Scenario: clashRunning state observed
- **WHEN** the VPN service starts or stops
- **THEN** `AppViewModel.clashRunning` StateFlow emits the updated boolean value
- **THEN** all screens observing this state recompose with the new value

#### Scenario: Broadcast events forwarded
- **WHEN** a broadcast event occurs (profile changed, service recreated, etc.)
- **THEN** `AppViewModel` emits the event via a SharedFlow
- **THEN** screens that need to react can collect the flow

### Requirement: Per-screen ViewModels for complex screens
Screens with non-trivial state (data loading, user input, real-time updates) SHALL have dedicated ViewModels scoped to their NavBackStackEntry.

#### Scenario: ViewModel created on navigation
- **WHEN** user navigates to a screen with a dedicated ViewModel
- **THEN** the ViewModel is created and scoped to that NavBackStackEntry
- **THEN** the ViewModel survives configuration changes

#### Scenario: ViewModel cleared on navigation away
- **WHEN** user navigates away and the back stack entry is removed
- **THEN** the ViewModel is cleared and resources are released

### Requirement: Stateless screens remain composable-only
Trivial screens (Help, ApkBroken, AppCrashed, Settings hub, About dialog) SHALL NOT have ViewModels and SHALL remain pure composable functions.

#### Scenario: Help screen renders without ViewModel
- **WHEN** user navigates to Help
- **THEN** the screen renders with only parameters passed from the NavHost
- **THEN** no ViewModel is instantiated

### Requirement: SavedStateHandle for process death survival
ViewModels holding user-editable state SHALL use `SavedStateHandle` to survive process death.

#### Scenario: Process death during profile editing
- **WHEN** the system kills the process while user is on Properties screen
- **THEN** on restoration, the ViewModel restores state from SavedStateHandle
- **THEN** user sees their unsaved edits preserved
