## MODIFIED Requirements

### Requirement: Base class for Compose activities
The `BaseComposeActivity` class SHALL be removed. Its responsibilities SHALL be absorbed by `MainActivity` and `AppViewModel`.

#### Scenario: Broadcast observation moves to AppViewModel
- **WHEN** the app starts
- **THEN** `AppViewModel` registers as a `Broadcasts.Observer`
- **THEN** broadcast events are emitted via `SharedFlow<AppEvent>`
- **THEN** individual screens collect events they care about

#### Scenario: clashRunning state moves to AppViewModel
- **WHEN** the VPN service starts or stops
- **THEN** `AppViewModel.clashRunning` StateFlow updates
- **THEN** screens that depend on this state recompose

#### Scenario: Edge-to-edge and recents handling stays in MainActivity
- **WHEN** `MainActivity.onCreate` is called
- **THEN** `enableEdgeToEdge()` is called
- **THEN** `hideFromRecents` preference is applied to app tasks

#### Scenario: Activity lifecycle events replaced by Compose lifecycle
- **WHEN** a screen needs to react to start/stop lifecycle
- **THEN** it uses `LifecycleEventEffect` or `DisposableEffect` in Compose
- **THEN** no `Channel<Event>` or `select {}` loop is needed

## REMOVED Requirements

### Requirement: BaseComposeActivity as base class for all screens
**Reason**: Replaced by single MainActivity + AppViewModel. With a single Activity hosting all screens via NavHost, there is no need for a base Activity class.
**Migration**: Move broadcast observation to AppViewModel. Move edge-to-edge/recents to MainActivity directly. Remove Channel<Event> pattern entirely — use Compose lifecycle effects and ViewModel flows instead.
