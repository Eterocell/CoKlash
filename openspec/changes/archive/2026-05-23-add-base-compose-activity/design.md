## Context

`BaseActivity<D : Design<*>>` extends `AppCompatActivity` and provides: broadcast observation (`Broadcasts.Observer`), event channel (`Channel<Event>`), `clashRunning` property, `uiStore`, `hideFromRecents` enforcement, edge-to-edge, and lifecycle tracking. Migrated Compose activities currently extend `ComponentActivity` directly and access `Remote.broadcasts.clashRunning` independently.

## Goals / Non-Goals

**Goals:**
- Create `BaseComposeActivity` extending `ComponentActivity` with relevant features from `BaseActivity`
- Provide `clashRunning` property (encapsulate `Remote` access)
- Provide `uiStore` lazy property
- Apply `hideFromRecents` enforcement in `onCreate`
- Call `enableEdgeToEdge()` in `onCreate`
- Implement `Broadcasts.Observer` with event channel for activities that need it
- Update all 4 migrated activities to extend `BaseComposeActivity`

**Non-Goals:**
- Porting the View-based `Design<*>` system (Compose replaces it)
- Porting `setContentDesign()` (replaced by `setContent {}`)
- Porting `defer` mechanism (can be added later if needed)
- Porting `startActivityForResult` wrapper (use Compose activity result APIs instead)
- Porting `applyDayNight` theme logic (Compose uses `CoKlashTheme` which handles this)

## Decisions

### 1. Open class, not abstract

**Choice:** `open class BaseComposeActivity : ComponentActivity(), Broadcasts.Observer`
**Rationale:** Simple activities (Help, ApkBroken, AppCrashed) don't need to override anything. More complex ones (AppSettings) can override event handlers.

### 2. Event channel as opt-in

**Choice:** Provide `events` channel and `Broadcasts.Observer` implementation in base class. Activities that need events override the relevant callbacks.
**Rationale:** Matches `BaseActivity` pattern. Simple screens ignore events; complex screens (AppSettings) use them.

### 3. Keep enableEdgeToEdge in base onCreate

**Choice:** Call `enableEdgeToEdge()` in `BaseComposeActivity.onCreate()`.
**Rationale:** All Compose activities need it. Removes duplication from every subclass.

## Risks / Trade-offs

- **Risk:** Adding broadcast observer to simple screens (Help, ApkBroken) adds minor overhead — acceptable since it's just a listener registration.
- **Trade-off:** `BaseComposeActivity` couples to `Remote` singleton — same coupling as `BaseActivity`, acceptable for this project.
