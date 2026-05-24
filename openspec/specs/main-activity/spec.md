## ADDED Requirements

### Requirement: MainScreen composable
Compose screen replacing MainDesign — hub with VPN toggle, traffic stats, navigation.

#### Scenario: VPN toggle
- **WHEN** user taps toggle button
- **THEN** VPN starts or stops

#### Scenario: Traffic polling
- **WHEN** VPN is running
- **THEN** traffic stats update every second

#### Scenario: Navigation items
- **WHEN** user taps a navigation item
- **THEN** corresponding activity is launched
