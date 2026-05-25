## Why

The app currently has 19 separate Activities, each hosting a single Compose screen. This is a legacy of the View-based architecture. With all screens now in Compose, maintaining separate Activities adds unnecessary overhead: each navigation is an Intent launch with process-level transitions, state cannot be shared via composition, and deep-link handling requires a separate dispatcher Activity. Consolidating into a single Activity with Compose Navigation + ViewModel gives us type-safe routes, shared state scoping, animated transitions, and a modern Android architecture.

## What Changes

- **BREAKING**: Replace 17 internal Activities with a single `MainActivity` hosting a `NavHost` navigation graph
- Introduce `androidx.navigation.compose` for all in-app navigation (type-safe route arguments)
- Introduce `androidx.lifecycle.ViewModel` per-screen for state management (replacing Activity-scoped coroutines + Channel events)
- Keep `ExternalControlActivity` as a separate exported entry point that deep-links into the NavHost
- Keep `AppCrashedActivity` and `ApkBrokenActivity` as standalone Activities (launched from `Remote` before normal app initialization)
- Remove `BaseComposeActivity` after migration (its responsibilities move to the single MainActivity + shared ViewModel)
- Update `AndroidManifest.xml` to remove deleted Activity declarations
- Update service notification PendingIntents to use NavDeepLink URIs instead of explicit Activity intents

## Capabilities

### New Capabilities
- `compose-navigation`: Single-activity NavHost with type-safe routes, argument passing (uuid, fileName), and animated transitions
- `viewmodel-state`: ViewModel-based state management per screen, replacing Activity-scoped Channel/event patterns
- `deep-link-routing`: NavDeepLink integration for external intents (clash://, clashmeta://) and notification PendingIntents

### Modified Capabilities
- `base-compose-activity`: Replaced by single MainActivity with NavHost; broadcast observation and clashRunning state move to a shared ViewModel

## Impact

- **app module**: Delete 15 Activity files, rewrite MainActivity, update ExternalControlActivity to forward to NavDeepLinks
- **design module**: Screen composables gain `navController` or route-based callbacks instead of Activity-level `onBackClick`
- **service module**: Update PendingIntent targets in TunService and ProfileWorker to use deep-link URIs
- **common module**: Navigation route constants and argument definitions added
- **Dependencies**: Add `androidx.navigation:navigation-compose`, `androidx.lifecycle:lifecycle-viewmodel-compose`, `androidx.hilt:hilt-navigation-compose` (optional, if DI needed)
- **Manifest**: Remove 15 activity declarations, add nav-graph deep-link intent-filters to MainActivity
