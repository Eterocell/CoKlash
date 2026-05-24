## ADDED Requirements

### Requirement: ProfilesScreen composable
Compose screen replacing ProfilesDesign with identical functionality.

#### Scenario: Profile list with active indicator
- **WHEN** ProfilesScreen is rendered with profiles
- **THEN** it shows a LazyColumn with colored dot indicating active profile

#### Scenario: Profile CRUD via long-press menu
- **WHEN** user long-presses a profile
- **THEN** a menu shows available operations (update, edit, duplicate, delete)

#### Scenario: Update All with spinning animation
- **WHEN** user taps Update All
- **THEN** sync icon spins until all updates complete
