## ADDED Requirements

### Requirement: App settings screen displays preference categories
The app settings screen SHALL display preferences organized into categories: Behavior, Interface, and Service.

#### Scenario: Categories displayed
- **WHEN** the user opens the App Settings screen
- **THEN** the screen displays "Behavior", "Interface", and "Service" category headers

### Requirement: Auto-restart switch toggles BroadcastReceiver
The app settings screen SHALL provide a switch to enable/disable auto-restart, which toggles the RestartReceiver component via PackageManager.

#### Scenario: Toggle auto-restart on
- **WHEN** the user enables the auto-restart switch
- **THEN** the RestartReceiver component is set to ENABLED state

### Requirement: Dark mode selection changes app theme
The app settings screen SHALL provide a selectable list for dark mode (Follow System, Always Light, Always Dark) that recreates all activities on change.

#### Scenario: User selects dark mode option
- **WHEN** the user selects "Always Dark" from the dark mode dialog
- **THEN** all activities are recreated to apply the new theme

### Requirement: Dynamic notification switch disabled when running
The show traffic notification switch SHALL be disabled when Clash VPN is currently running.

#### Scenario: Switch disabled during VPN
- **WHEN** Clash VPN is running
- **THEN** the "Show Traffic" switch is disabled and cannot be toggled

### Requirement: Hide app icon switch modifies launcher alias
The hide app icon switch SHALL toggle the MainActivityAlias component enabled state via PackageManager.

#### Scenario: User hides app icon
- **WHEN** the user enables the hide app icon switch
- **THEN** the MainActivityAlias component is set to DISABLED state

### Requirement: App settings screen has top app bar with back navigation
The app settings screen SHALL display a Material 3 TopAppBar with the screen title and a back navigation button.

#### Scenario: Back navigation
- **WHEN** the user taps the back button
- **THEN** the activity finishes and returns to the previous screen
