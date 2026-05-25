## 1. Infrastructure Setup

- [x] 1.1 Add `androidx.navigation:navigation-compose` and `androidx.lifecycle:lifecycle-viewmodel-compose` dependencies to `build.gradle.kts`
- [x] 1.2 Create `app/src/main/java/com/github/kr328/clash/navigation/Routes.kt` with all `@Serializable` route definitions
- [x] 1.3 Add `kotlinx-serialization` plugin to app module if not already present

## 2. AppViewModel

- [x] 2.1 Create `app/src/main/java/com/github/kr328/clash/AppViewModel.kt` with `clashRunning` StateFlow and broadcast event SharedFlow
- [x] 2.2 Implement `Broadcasts.Observer` in AppViewModel, register/unregister in init/onCleared
- [x] 2.3 Expose `AppEvent` sealed class for profile changes, service lifecycle events

## 3. NavHost Shell in MainActivity

- [x] 3.1 Rewrite `MainActivity` to use `NavHost` with all route destinations, keeping existing screen composables as-is
- [x] 3.2 Wire `AppViewModel` as Activity-scoped ViewModel accessible from all destinations
- [x] 3.3 Add Material 3 shared-axis transition animations to `NavHost` enter/exit
- [x] 3.4 Register `coklash://` deep-link URIs for Properties and Main routes in NavHost

## 4. Migrate Simple Screens (no ViewModel needed)

- [x] 4.1 Wire HelpScreen as NavHost destination, remove HelpActivity
- [x] 4.2 Wire SettingsScreen as NavHost destination, remove SettingsActivity
- [x] 4.3 Wire AppSettingsScreen as NavHost destination, remove AppSettingsActivity
- [x] 4.4 Wire NewProfileScreen as NavHost destination, remove NewProfileActivity
- [x] 4.5 Wire FilesScreen as NavHost destination (uuid route arg), remove FilesActivity

## 5. Migrate Medium Screens (per-screen ViewModel)

- [x] 5.1 Create ProfilesViewModel, wire ProfilesScreen as destination, remove ProfilesActivity
- [x] 5.2 Create PropertiesViewModel, wire PropertiesScreen as destination (uuid route arg), remove PropertiesActivity
- [x] 5.3 Create LogsViewModel, wire LogsScreen as destination, remove LogsActivity
- [x] 5.4 Create LogcatViewModel, wire LogcatScreen as destination (fileName? route arg), remove LogcatActivity
- [x] 5.5 Create ProvidersViewModel, wire ProvidersScreen as destination, remove ProvidersActivity

## 6. Migrate Complex Screens (per-screen ViewModel)

- [x] 6.1 Create ProxyViewModel, wire ProxyScreen as destination, remove ProxyActivity
- [x] 6.2 Create AccessControlViewModel, wire AccessControlScreen as destination, remove AccessControlActivity
- [x] 6.3 Create NetworkSettingsViewModel, wire NetworkSettingsScreen as destination, remove NetworkSettingsActivity
- [x] 6.4 Create OverrideSettingsViewModel, wire OverrideSettingsScreen as destination, remove OverrideSettingsActivity
- [x] 6.5 Create MetaFeatureSettingsViewModel, wire MetaFeatureSettingsScreen as destination, remove MetaFeatureSettingsActivity

## 7. Screen Composable Signature Updates

- [x] 7.1 Remove `onBackClick` parameter from all screen composables in design module
- [x] 7.2 Replace `onBackClick` with `NavController.popBackStack()` calls via `LocalNavController` CompositionLocal or passed navController
- [x] 7.3 Replace inter-screen navigation callbacks (onProfilesClick, onSettingsClick, etc.) with NavController.navigate() calls

## 8. External Integration

- [x] 8.1 Update ExternalControlActivity to forward profile-import results via `coklash://properties/{uuid}` deep-link to MainActivity
- [x] 8.2 Update TunService notification PendingIntent to use `coklash://main` deep-link URI
- [x] 8.3 Update ProfileWorker notification PendingIntent to use `coklash://properties/{uuid}` deep-link URI

## 9. Cleanup

- [x] 9.1 Delete BaseComposeActivity.kt
- [x] 9.2 Remove all deleted Activity declarations from AndroidManifest.xml
- [x] 9.3 Add NavDeepLink intent-filters for `coklash://` scheme to MainActivity in AndroidManifest.xml
- [x] 9.4 Verify build passes with `./gradlew assembleAlphaDebug`
- [x] 9.5 Verify navigation works end-to-end for all routes
