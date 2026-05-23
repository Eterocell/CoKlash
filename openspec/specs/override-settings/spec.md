## ADDED Requirements

### Requirement: OverrideSettingsScreen composable
Compose screen replacing OverrideSettingsDesign with identical functionality.

#### Scenario: Screen displays General and DNS categories
- **WHEN** OverrideSettingsScreen is rendered
- **THEN** it shows "General" category with ports, allow LAN, IPv6, bind address, external controller, CORS, secret, mode, log level, hosts
- **THEN** it shows "DNS" category with enable toggle and sub-settings

#### Scenario: Editable port preferences
- **WHEN** user taps a port preference (HTTP, SOCKS, redirect, tproxy, mixed)
- **THEN** a dialog shows current value with text input for port number
- **WHEN** user enters empty value
- **THEN** the field is set to null (don't modify)

#### Scenario: DNS dependency logic
- **WHEN** DNS enable is set to "Use built-in" (false)
- **THEN** all DNS sub-preferences are disabled
- **WHEN** DNS enable is "Don't modify" or "Force enable"
- **THEN** all DNS sub-preferences are enabled

#### Scenario: Editable text map (hosts, nameserver policy)
- **WHEN** user taps a text map preference
- **THEN** a dialog shows current key-value pairs with add/remove capability

#### Scenario: Reset override
- **WHEN** user taps reset action in top bar
- **THEN** confirmation dialog appears and clears override on confirm

### Requirement: Activity extends BaseComposeActivity
OverrideSettingsActivity extends BaseComposeActivity and uses setContent.

#### Scenario: Activity lifecycle
- **WHEN** activity starts
- **THEN** it queries ConfigurationOverride from Clash core
- **WHEN** activity finishes
- **THEN** it patches the override back to Clash core
