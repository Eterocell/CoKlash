## ADDED Requirements

### Requirement: LogsScreen composable
Compose screen replacing LogsDesign with identical functionality.

#### Scenario: Screen displays log file list
- **WHEN** LogsScreen is rendered with log files
- **THEN** it shows a LazyColumn with each file's name and formatted date

#### Scenario: Empty state
- **WHEN** logs list is empty
- **THEN** centered empty message is shown

#### Scenario: Delete all with confirmation
- **WHEN** user taps delete action
- **THEN** confirmation dialog appears
- **WHEN** user confirms
- **THEN** onDeleteAll is called

#### Scenario: Navigate to logcat
- **WHEN** user taps logcat action
- **THEN** onStartLogcat is called

#### Scenario: Open log file
- **WHEN** user taps a log file item
- **THEN** onLogFileClick is called with that LogFile
