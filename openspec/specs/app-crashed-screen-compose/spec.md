## ADDED Requirements

### Requirement: App crashed screen displays crash logs
The app crashed screen SHALL display the application crash logs in a scrollable, monospace text view.

#### Scenario: Crash logs displayed on screen open
- **WHEN** the app crashes and AppCrashedActivity launches
- **THEN** the screen loads and displays the crash log text from system logcat

#### Scenario: Logs are scrollable
- **WHEN** the crash log content exceeds the visible area
- **THEN** the user can scroll vertically to view the full log

### Requirement: App crashed screen has top app bar with title
The app crashed screen SHALL display a Material 3 TopAppBar with the "Application Crashed" title.

#### Scenario: Title displayed
- **WHEN** the user views the app crashed screen
- **THEN** the top app bar shows "Application Crashed" as the title

### Requirement: App crashed screen supports light and dark theme
The app crashed screen SHALL render correctly in both light and dark themes using CoKlashTheme.

#### Scenario: Dark mode rendering
- **WHEN** the device is in dark mode
- **THEN** the app crashed screen renders with dark theme colors from CoKlashTheme
