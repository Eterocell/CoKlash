## Context

CoKlash currently has 19 Activities, each extending `BaseComposeActivity` and hosting a single Compose screen via `setContent`. Navigation between screens uses explicit `Intent` launches. State is managed via Activity-scoped coroutines, `Channel<Event>`, and `mutableStateOf` directly in Activities. There are no ViewModels in the project.

The previous migration (View → Compose) kept the multi-Activity architecture intact. This change consolidates into a single-Activity + NavHost pattern, which is the recommended modern Android architecture for Compose apps.

## Goals / Non-Goals

**Goals:**
- Single `MainActivity` with a `NavHost` containing all in-app screens as composable destinations
- Type-safe navigation routes with compile-time argument validation
- ViewModel per screen for state management (replacing Activity-scoped coroutines + Channel)
- Shared application-level state (clashRunning, broadcast events) via a shared ViewModel scoped to the Activity
- Deep-link support for external intents (clash://, clashmeta://) routed into NavHost
- Animated transitions between screens

**Non-Goals:**
- Dependency injection framework (Hilt/Koin) — manual ViewModel construction via factory is sufficient
- Multi-module navigation (all routes stay in app module)
- Modularizing screen composables into separate Gradle modules
- Changing the VPN service architecture or core module

## Decisions

### 1. Navigation Library: Compose Navigation with type-safe routes

**Choice:** `androidx.navigation:navigation-compose` with Kotlin Serialization route objects (Navigation 2.8+ type-safe API).

**Alternatives considered:**
- Voyager/Decompose: Third-party, adds dependency risk. Navigation Compose is first-party and well-maintained.
- Manual NavHost with sealed class: More boilerplate, no deep-link support out of the box.

**Rationale:** Type-safe routes via `@Serializable` data objects/classes give compile-time safety for arguments. Deep-link support is built-in. This is the official recommended approach for Compose apps.

### 2. Route Definition: Serializable objects in a shared Routes file

```kotlin
// app/src/main/java/com/github/kr328/clash/navigation/Routes.kt
@Serializable data object Main
@Serializable data object Profiles
@Serializable data class Properties(val uuid: String)
@Serializable data class Files(val uuid: String)
@Serializable data object Logs
@Serializable data class Logcat(val fileName: String? = null)
@Serializable data object Settings
@Serializable data object AppSettings
@Serializable data object NetworkSettings
@Serializable data object AccessControl
@Serializable data object OverrideSettings
@Serializable data object MetaFeatureSettings
@Serializable data object Proxy
@Serializable data object Providers
@Serializable data object NewProfile
@Serializable data object Help
```

### 3. ViewModel Strategy: Per-screen ViewModels, one shared AppViewModel

**AppViewModel** (Activity-scoped):
- Holds `clashRunning` state (replaces `BaseComposeActivity.clashRunning`)
- Observes `Remote.broadcasts` (replaces `Broadcasts.Observer` in Activity)
- Exposes `SharedFlow<AppEvent>` for service lifecycle events

**Per-screen ViewModels** (NavBackStackEntry-scoped):
- Only for screens with non-trivial state (Proxy, Profiles, AccessControl, Logs, Logcat, Settings screens with override state)
- Trivial screens (Help, ApkBroken, AppCrashed, Settings hub) remain stateless composables

### 4. ExternalControlActivity: Keep separate, forward via deep-link

**Choice:** Keep `ExternalControlActivity` as a separate exported Activity. After processing the intent, it launches `MainActivity` with a NavDeepLink URI and finishes itself.

**Rationale:** ExternalControlActivity handles actions (start/stop/toggle) that don't navigate to a screen. It also handles profile import which needs to create a profile before navigating. Keeping it separate avoids polluting the NavHost with non-screen logic.

### 5. AppCrashedActivity / ApkBrokenActivity: Keep standalone

**Rationale:** These are launched by `Remote` before normal app initialization completes. They cannot rely on the NavHost being available. They remain standalone single-screen Activities.

### 6. Back navigation: System back handled by NavHost

Screen composables will no longer receive `onBackClick`. Instead, `NavController.popBackStack()` handles back navigation. The top-level `MainActivity` composable wraps the NavHost and provides the `navController` to screens that need to navigate forward.

### 7. Transition animations: Shared axis motion

Use Material 3 shared-axis transitions (horizontal for peer navigation, vertical for parent-child). Implemented via `AnimatedNavHost` with `enterTransition`/`exitTransition` parameters.

## Risks / Trade-offs

- **[Risk] Large single PR** → Mitigate by migrating in phases: (1) infrastructure (Routes, AppViewModel, NavHost shell), (2) simple screens, (3) complex screens, (4) cleanup old Activities. Each phase is a separate commit.
- **[Risk] Deep-link URI conflicts** → Mitigate by using app-internal URI scheme (`coklash://`) for NavDeepLinks, separate from external `clash://`/`clashmeta://` schemes.
- **[Risk] State loss on process death** → Mitigate by using `SavedStateHandle` in ViewModels for critical state. Navigation Compose automatically saves/restores the back stack.
- **[Risk] Screen composable signature changes** → All screens currently take `onBackClick: () -> Unit`. This parameter will be removed. Breaking change is contained within the design module.
- **[Trade-off] No DI framework** → ViewModels use manual factory construction with `Application` context. This is simpler but means ViewModels can't easily receive constructor-injected dependencies. Acceptable given the app's current simplicity.

## Migration Plan

1. Add navigation + lifecycle-viewmodel dependencies to `build.gradle.kts`
2. Create `Routes.kt` with all route definitions
3. Create `AppViewModel` with broadcast observation + clashRunning state
4. Rewrite `MainActivity` with `NavHost` containing all routes
5. Migrate screens one-by-one: remove `onBackClick`, wire to NavController
6. Create per-screen ViewModels for complex screens (move state from Activity)
7. Update `ExternalControlActivity` to forward via deep-link
8. Update service PendingIntents to use deep-link URIs
9. Delete old Activity files + remove from AndroidManifest
10. Delete `BaseComposeActivity`

## Open Questions

- Should `ProxyActivity` and `ProvidersActivity` self-restart behavior (on profile load change) become a `LaunchedEffect` that resets ViewModel state, or a full navigation pop-and-push?
- Should the About dialog remain a dialog overlay on Main, or become a route?
