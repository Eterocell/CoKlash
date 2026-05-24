## ADDED Requirements

### Requirement: AccessControlScreen composable
Compose screen replacing AccessControlDesign with identical functionality.

#### Scenario: App list with checkboxes
- **WHEN** AccessControlScreen is rendered with apps
- **THEN** it shows a LazyColumn with each app's icon, label, package name, and checkbox

#### Scenario: Search/filter apps
- **WHEN** user activates search and types a query
- **THEN** the list is filtered by label or packageName (case-insensitive)

#### Scenario: Selection operations via overflow menu
- **WHEN** user opens overflow menu
- **THEN** options for Select All, Select None, Invert, Import, Export are shown

#### Scenario: Save on destroy
- **WHEN** activity is destroyed
- **THEN** selections are saved and Clash service is restarted if running and changed
