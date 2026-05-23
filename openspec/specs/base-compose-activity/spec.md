## ADDED Requirements

### Requirement: BaseComposeActivity provides clashRunning property
BaseComposeActivity SHALL expose a `clashRunning` property that encapsulates access to `Remote.broadcasts.clashRunning`.

#### Scenario: Activity reads clash running state
- **WHEN** a Compose activity accesses `clashRunning`
- **THEN** it returns the current VPN running state without directly referencing `Remote`

### Requirement: BaseComposeActivity provides uiStore property
BaseComposeActivity SHALL expose a lazy-initialized `uiStore` property for accessing UI preferences.

#### Scenario: Activity reads UI preferences
- **WHEN** a Compose activity accesses `uiStore`
- **THEN** it returns a `UiStore` instance for the current context

### Requirement: BaseComposeActivity applies edge-to-edge and hideFromRecents
BaseComposeActivity SHALL call `enableEdgeToEdge()` and enforce `hideFromRecents` setting in `onCreate`.

#### Scenario: Activity created
- **WHEN** a BaseComposeActivity subclass is created
- **THEN** edge-to-edge is enabled and hideFromRecents is applied to app tasks

### Requirement: BaseComposeActivity implements Broadcasts.Observer
BaseComposeActivity SHALL implement `Broadcasts.Observer` and provide an event channel for system events (ClashStart, ClashStop, ProfileChanged, etc.).

#### Scenario: VPN state changes
- **WHEN** the Clash VPN starts or stops
- **THEN** the corresponding event is sent to the activity's event channel

### Requirement: All migrated activities extend BaseComposeActivity
All previously migrated Compose activities (HelpActivity, AppCrashedActivity, ApkBrokenActivity, AppSettingsActivity) SHALL extend BaseComposeActivity instead of ComponentActivity.

#### Scenario: Migrated activity uses base class features
- **WHEN** AppSettingsActivity needs `clashRunning`
- **THEN** it accesses the inherited property instead of directly referencing `Remote`
