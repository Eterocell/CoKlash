## ADDED Requirements

### Requirement: PropertiesScreen composable
Compose screen replacing PropertiesDesign with identical functionality.

#### Scenario: Dynamic form based on profile type
- **WHEN** profileType is File
- **THEN** only Name and Browse Files are shown
- **WHEN** profileType is Url or External
- **THEN** Name, URL, Auto-update interval, and Browse Files are shown

#### Scenario: Commit with validation
- **WHEN** user clicks commit with blank name
- **THEN** error toast is shown
- **WHEN** user clicks commit with valid data
- **THEN** progress bar shows during fetch/commit

#### Scenario: Exit without saving
- **WHEN** user presses back with unsaved changes
- **THEN** confirmation dialog appears
