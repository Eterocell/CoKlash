## ADDED Requirements

### Requirement: NetworkSettingsScreen composable
Compose screen replacing NetworkSettingsDesign with identical functionality.

#### Scenario: Screen displays VPN toggle and network options
- **WHEN** NetworkSettingsScreen is rendered
- **THEN** it shows VPN toggle (route system traffic) at the top
- **THEN** it shows "VPN Service Options" category with sub-preferences

#### Scenario: VPN dependency logic
- **WHEN** enableVpn is false
- **THEN** all VPN sub-preferences are disabled
- **WHEN** enableVpn is true and not running
- **THEN** all VPN sub-preferences are enabled

#### Scenario: Running state disables all
- **WHEN** clashRunning is true
- **THEN** all preferences including VPN toggle are disabled
- **THEN** a Snackbar/Toast indicates options are unavailable

#### Scenario: Access control mode selection
- **WHEN** user taps access control mode
- **THEN** dialog shows: "Allow all apps", "Allow selected apps", "Deny selected apps"

#### Scenario: Navigate to access control packages
- **WHEN** user taps access control packages
- **THEN** AccessControlActivity is started

#### Scenario: System proxy (API 29+)
- **WHEN** device is API 29+
- **THEN** system proxy switch is shown
- **WHEN** device is below API 29
- **THEN** system proxy switch is hidden

### Requirement: Activity extends BaseComposeActivity
NetworkSettingsActivity extends BaseComposeActivity and uses setContent.

#### Scenario: Activity recreates on clash state change
- **WHEN** ClashStart, ClashStop, or ServiceRecreated event occurs
- **THEN** activity recreates itself
