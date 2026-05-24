## ADDED Requirements

### Requirement: ProxyScreen composable
Compose screen replacing ProxyDesign with identical functionality.

#### Scenario: Tabbed proxy groups
- **WHEN** ProxyScreen is rendered with group names
- **THEN** it shows ScrollableTabRow + HorizontalPager with one tab per group

#### Scenario: Proxy selection in Selector groups
- **WHEN** user taps a proxy in a Selector group
- **THEN** onProxySelect is called and RadioButton updates

#### Scenario: URL testing with loading indicator
- **WHEN** user taps the URL test FAB
- **THEN** FAB shows CircularProgressIndicator until test completes

#### Scenario: Color-coded delay indicators
- **WHEN** proxy has delay > 0
- **THEN** delay is shown colored (green ≤200ms, yellow ≤500ms, red >500ms)
