## ADDED Requirements

### Requirement: Apk broken screen displays error message
The apk broken screen SHALL display an error tip explaining that the APK lacks necessary runtime components due to an incomplete download.

#### Scenario: Error tip displayed on screen open
- **WHEN** the app launches with broken APK state
- **THEN** the screen displays the `application_broken_tips` message with HTML bold formatting preserved

### Requirement: Apk broken screen provides reinstall link
The apk broken screen SHALL display a clickable "Github Releases" link under a "Reinstall" category that opens the GitHub releases page in the default browser.

#### Scenario: User taps Github Releases link
- **WHEN** the user taps the "Github Releases" item
- **THEN** the system opens the meta GitHub URL in the default browser via ACTION_VIEW intent

### Requirement: Apk broken screen has top app bar with title
The apk broken screen SHALL display a Material 3 TopAppBar with the "Application Broken" title.

#### Scenario: Title displayed
- **WHEN** the user views the apk broken screen
- **THEN** the top app bar shows "Application Broken" as the title

### Requirement: Apk broken screen supports light and dark theme
The apk broken screen SHALL render correctly in both light and dark themes using CoKlashTheme.

#### Scenario: Dark mode rendering
- **WHEN** the device is in dark mode
- **THEN** the apk broken screen renders with dark theme colors from CoKlashTheme
