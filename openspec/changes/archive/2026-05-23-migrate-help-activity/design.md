## Context

CoKlash currently uses a View-based architecture where each screen is an `Activity` + `Design` class pair. `HelpDesign` inflates `design_settings_common.xml` and builds a preference screen with clickable links organized in categories (Document, Sources). The screen has zero mutable state — it only opens URLs in a browser.

This is the first Compose migration. It must establish the foundation (dependencies, theme, patterns) for all subsequent migrations.

## Goals / Non-Goals

**Goals:**
- Migrate HelpActivity to Jetpack Compose with Material 3 Expressive
- Establish Compose BOM + M3 dependencies in the build system
- Create a reusable `CoKlashTheme` (light/dark, dynamic color support)
- Prove the migration pattern works end-to-end before tackling stateful screens

**Non-Goals:**
- Migrating any other Activity in this change
- Creating a shared navigation graph (premature — only one screen is Compose)
- Replacing `BaseActivity` or the channel/select pattern (later migrations)
- Supporting hybrid Compose-in-View or View-in-Compose interop

## Decisions

### 1. ComponentActivity over AppCompatActivity

**Choice:** `ComponentActivity` with `setContent { }` directly.
**Rationale:** HelpActivity has no AppCompat dependencies (no ActionBar, no Fragments). Compose screens don't need AppCompat. Lighter base class.
**Alternative considered:** Keep AppCompatActivity for consistency — rejected because it adds unnecessary weight and the migration goal is to move away from it.

### 2. Compose BOM for dependency management

**Choice:** Use `androidx.compose:compose-bom` to align all Compose artifact versions.
**Rationale:** Single version source, avoids version conflicts between compose-ui, material3, etc.

### 3. Material 3 (androidx.compose.material3) — not Material 2

**Choice:** `androidx.compose.material3:material3` with expressive extensions.
**Rationale:** Migration goal explicitly targets M3 Expressive. M3 provides `TopAppBar`, `Scaffold`, dynamic theming out of the box.

### 4. Screen as a top-level @Composable in design module

**Choice:** `HelpScreen` composable lives in `design/` module, Activity in `app/` just calls `setContent { CoKlashTheme { HelpScreen(...) } }`.
**Rationale:** Matches existing separation (Design = UI, Activity = controller). Keeps design module as the UI layer.

### 5. No ViewModel for HelpScreen

**Choice:** No ViewModel — pass a lambda `onLinkClick: (Uri) -> Unit` to the composable.
**Rationale:** Zero state. A ViewModel would be over-engineering. The Activity provides the Intent-launching lambda.

## Risks / Trade-offs

- **Risk:** Compose BOM version may conflict with existing AndroidX versions → Mitigation: Use compatible BOM version, verify build compiles.
- **Risk:** Theme doesn't match existing app appearance → Mitigation: Extract current color scheme from existing `AppThemeLight`/`AppThemeDark` styles into M3 ColorScheme.
- **Trade-off:** First migration adds ~2MB to APK (Compose runtime) — acceptable for the long-term migration benefit.
- **Trade-off:** Two UI frameworks coexist during migration period — expected and documented in AGENTS.md migration rules.
