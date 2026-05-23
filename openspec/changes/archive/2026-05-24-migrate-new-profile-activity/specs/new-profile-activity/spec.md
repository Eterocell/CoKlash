## ADDED Requirements

### Requirement: NewProfileScreen composable
Compose screen replacing NewProfileDesign with identical functionality.

#### Scenario: Screen displays profile providers
- **WHEN** NewProfileScreen is rendered with providers
- **THEN** it shows a LazyColumn with each provider's icon, name, and summary

#### Scenario: Click creates profile
- **WHEN** user taps a provider
- **THEN** onProviderClick is called

#### Scenario: Long-press opens app details
- **WHEN** user long-presses an External provider
- **THEN** onProviderLongClick is called
