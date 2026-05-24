## ADDED Requirements

### Requirement: ProvidersScreen composable
Compose screen replacing ProvidersDesign with identical functionality.

#### Scenario: Provider list with update indicators
- **WHEN** ProvidersScreen is rendered with providers
- **THEN** it shows a LazyColumn with each provider's name, type, vehicle type, and relative update time

#### Scenario: Per-provider update with loading state
- **WHEN** user taps update on a provider
- **THEN** a CircularProgressIndicator shows until update completes

#### Scenario: Update All
- **WHEN** user taps Update All
- **THEN** all non-Inline providers are updated concurrently
