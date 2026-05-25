## ADDED Requirements

### Requirement: Single NavHost with all in-app routes
The app SHALL host a single `NavHost` in `MainActivity` containing composable destinations for all in-app screens.

#### Scenario: App launches to main screen
- **WHEN** the app is launched from the launcher
- **THEN** `MainActivity` displays the `NavHost` starting at the Main route

#### Scenario: Navigation to any screen
- **WHEN** a user action triggers navigation (e.g., tapping Profiles)
- **THEN** the `NavController` navigates to the corresponding route without launching a new Activity

### Requirement: Type-safe route definitions with Kotlin Serialization
All navigation routes SHALL be defined as `@Serializable` data objects or data classes with typed arguments.

#### Scenario: Route with no arguments
- **WHEN** navigating to a screen with no parameters (e.g., Settings)
- **THEN** the route is a `@Serializable data object` and requires no argument passing

#### Scenario: Route with UUID argument
- **WHEN** navigating to Properties or Files screen
- **THEN** the route is a `@Serializable data class` with a `uuid: String` parameter
- **THEN** the UUID is passed type-safely and available in the destination composable

#### Scenario: Route with optional argument
- **WHEN** navigating to Logcat screen
- **THEN** the route accepts an optional `fileName: String? = null` parameter
- **THEN** null fileName triggers streaming mode, non-null opens a specific log file

### Requirement: Back navigation handled by NavController
All screens SHALL use `NavController.popBackStack()` for back navigation instead of Activity `finish()`.

#### Scenario: User presses system back
- **WHEN** user presses the system back button on any screen except Main
- **THEN** `NavController` pops the current destination and returns to the previous screen

#### Scenario: User presses back on Main screen
- **WHEN** user presses back on the Main route (start destination)
- **THEN** the Activity finishes (app exits)

### Requirement: Animated transitions between destinations
Navigation transitions SHALL use Material 3 shared-axis motion patterns.

#### Scenario: Peer navigation (e.g., Main → Profiles)
- **WHEN** navigating between peer screens
- **THEN** a horizontal shared-axis enter/exit animation plays

#### Scenario: Parent-child navigation (e.g., Profiles → Properties)
- **WHEN** navigating from a list to a detail screen
- **THEN** a forward/backward shared-axis animation plays
