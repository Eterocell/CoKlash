## ADDED Requirements

### Requirement: FilesScreen composable
Compose screen replacing FilesDesign with identical functionality.

#### Scenario: File browser with directory navigation
- **WHEN** user taps a directory
- **THEN** it pushes to the stack and shows directory contents
- **WHEN** user presses back
- **THEN** it pops the stack or finishes if at root

#### Scenario: File operations via long-press menu
- **WHEN** user long-presses a file
- **THEN** a menu shows available operations (rename, import, export, delete)

#### Scenario: Conditional FAB for new file import
- **WHEN** user is in a subdirectory AND configuration is editable
- **THEN** FAB is shown for importing new files
