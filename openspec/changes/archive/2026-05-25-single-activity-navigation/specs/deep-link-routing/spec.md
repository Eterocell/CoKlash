## ADDED Requirements

### Requirement: Deep-link URI scheme for internal navigation
The app SHALL define an internal URI scheme (`coklash://`) for NavDeepLink routing that is separate from external schemes (`clash://`, `clashmeta://`).

#### Scenario: Notification navigates to Properties screen
- **WHEN** a profile update notification is tapped
- **THEN** the PendingIntent uses URI `coklash://properties/{uuid}`
- **THEN** MainActivity's NavHost resolves the deep-link to the Properties route with the correct UUID

#### Scenario: Notification navigates to Main screen
- **WHEN** the VPN service notification's configure action is tapped
- **THEN** the PendingIntent uses URI `coklash://main`
- **THEN** MainActivity's NavHost resolves to the Main route

### Requirement: External intent forwarding to NavDeepLink
`ExternalControlActivity` SHALL forward profile-import intents to MainActivity via NavDeepLink after creating the profile.

#### Scenario: Deep-link profile import
- **WHEN** an external intent with `clash://install-config?url=...` is received
- **THEN** `ExternalControlActivity` creates the profile and obtains a UUID
- **THEN** it launches MainActivity with URI `coklash://properties/{uuid}`
- **THEN** it finishes itself

#### Scenario: External start/stop/toggle actions
- **WHEN** an external intent with action `START_CLASH`, `STOP_CLASH`, or `TOGGLE_CLASH` is received
- **THEN** `ExternalControlActivity` performs the action directly
- **THEN** it finishes without navigating to any screen

### Requirement: External schemes remain on ExternalControlActivity
The `clash://` and `clashmeta://` URI schemes SHALL remain as intent-filters on `ExternalControlActivity`, not on `MainActivity`.

#### Scenario: External deep-link does not open MainActivity directly
- **WHEN** a `clash://install-config` URI is opened from a browser
- **THEN** Android resolves it to `ExternalControlActivity`
- **THEN** `ExternalControlActivity` processes the intent before optionally forwarding to MainActivity
