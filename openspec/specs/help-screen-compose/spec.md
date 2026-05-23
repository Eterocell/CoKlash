## ADDED Requirements

### Requirement: Help screen displays categorized link list
The help screen SHALL display a list of clickable links organized into categories: "Document" and "Sources". Each link SHALL show a title and URL summary.

#### Scenario: Document category links displayed
- **WHEN** the user opens the Help screen
- **THEN** the screen displays a "Document" category with links to "Clash Wiki" and "Clash Meta Wiki"

#### Scenario: Sources category links displayed
- **WHEN** the user opens the Help screen
- **THEN** the screen displays a "Sources" category with links to "Clash Meta Core" and "Clash Meta for Android"

### Requirement: Clicking a link opens external browser
The help screen SHALL open the associated URL in the device's default browser when a link item is tapped.

#### Scenario: User taps a wiki link
- **WHEN** the user taps "Clash Wiki"
- **THEN** the system opens the Clash Wiki URL in the default browser via ACTION_VIEW intent

#### Scenario: User taps a source link
- **WHEN** the user taps "Clash Meta for Android"
- **THEN** the system opens the GitHub repository URL in the default browser

### Requirement: Help screen has top app bar with back navigation
The help screen SHALL display a Material 3 TopAppBar with the screen title and a back navigation button.

#### Scenario: Back navigation
- **WHEN** the user taps the back button in the top app bar
- **THEN** the activity finishes and returns to the previous screen

### Requirement: Help screen supports light and dark theme
The help screen SHALL render correctly in both light and dark themes using CoKlashTheme.

#### Scenario: Dark mode rendering
- **WHEN** the device is in dark mode
- **THEN** the help screen renders with dark theme colors from CoKlashTheme
