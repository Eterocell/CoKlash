## ADDED Requirements

### Requirement: About dialog displays app icon, name, and version
The about dialog SHALL display the app icon, application name, and version string in a horizontal layout.

#### Scenario: About dialog shown with version info
- **WHEN** the user taps "About" in the main screen menu
- **THEN** a dialog appears showing the app icon, "Clash Meta for Android" name, and current version string

### Requirement: About dialog uses Compose for content rendering
The about dialog content SHALL be rendered using Jetpack Compose via ComposeView inside the existing AlertDialog.

#### Scenario: Compose content in AlertDialog
- **WHEN** `MainDesign.showAbout(versionName)` is called
- **THEN** the dialog content is rendered by a Compose composable hosted in ComposeView

### Requirement: About dialog supports light and dark theme
The about dialog content SHALL render correctly in both light and dark themes using CoKlashTheme.

#### Scenario: Dark mode rendering
- **WHEN** the device is in dark mode and the About dialog is shown
- **THEN** the dialog content renders with dark theme colors from CoKlashTheme
