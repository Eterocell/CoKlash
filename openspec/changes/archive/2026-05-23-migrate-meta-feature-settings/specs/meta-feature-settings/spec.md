## ADDED Requirements

### Requirement: MetaFeatureSettingsScreen composable
Compose screen replacing MetaFeatureSettingsDesign with identical functionality.

#### Scenario: Screen displays all settings categories
- **WHEN** MetaFeatureSettingsScreen is rendered
- **THEN** it shows "Settings" category with unified delay, geodata mode, TCP concurrent, find process mode
- **THEN** it shows "Sniffer" category with enable toggle and sub-settings
- **THEN** it shows "GeoX Files" category with import buttons

#### Scenario: Tri-state preference selection
- **WHEN** user taps a tri-state preference (e.g. unified delay)
- **THEN** a dialog shows options: "Don't modify", "Enabled", "Disabled"
- **WHEN** user selects an option
- **THEN** the configuration field updates and dialog dismisses

#### Scenario: Find process mode selection
- **WHEN** user taps find process mode preference
- **THEN** a dialog shows: "Don't modify", "Off", "Strict", "Always"

#### Scenario: Sniffer dependency logic
- **WHEN** sniffer enable is set to "Disabled" (false)
- **THEN** all sniffer sub-preferences are disabled (greyed out)
- **WHEN** sniffer enable is set to "Don't modify" or "Enabled"
- **THEN** all sniffer sub-preferences are enabled

#### Scenario: Editable text list preferences
- **WHEN** user taps a text list preference (e.g. sniff HTTP ports)
- **THEN** a dialog shows current list items with add/remove capability
- **WHEN** user confirms the dialog
- **THEN** the configuration list field updates

#### Scenario: Geo file import
- **WHEN** user taps an import button (GeoIP/GeoSite/Country/ASN)
- **THEN** a file picker opens for "*/*" content
- **WHEN** file is selected with valid extension (.metadb/.db/.dat/.mmdb)
- **THEN** file is copied to clash dir with correct name and toast shown
- **WHEN** file has invalid extension
- **THEN** error dialog is shown

#### Scenario: Reset override
- **WHEN** user taps reset action in top bar
- **THEN** confirmation dialog appears
- **WHEN** user confirms reset
- **THEN** override is cleared and activity finishes

### Requirement: Activity extends BaseComposeActivity
MetaFeatureSettingsActivity extends BaseComposeActivity and uses setContent.

#### Scenario: Activity lifecycle
- **WHEN** activity starts
- **THEN** it queries ConfigurationOverride from Clash core
- **WHEN** activity finishes (back press or reset)
- **THEN** it patches the override back to Clash core (unless reset)
