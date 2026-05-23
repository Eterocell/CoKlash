## Context

NetworkSettingsActivity is a preference-list screen for VPN and network options. It has a main VPN toggle that controls dependent sub-preferences, switches for network options, an access control mode selector, and a navigation action to AccessControlActivity. All preferences are disabled when clash is running.

## Goals / Non-Goals

**Goals:**
- Migrate NetworkSettingsActivity to Compose + M3 using BaseComposeActivity
- Implement VPN dependency logic (disable sub-prefs when VPN is off)
- Implement "all disabled when running" logic with toast
- Implement access control mode selector (AcceptAll/AcceptSelected/DenySelected)
- Implement navigation to AccessControlActivity
- Remove old NetworkSettingsDesign.kt and XML layout

**Non-Goals:**
- Adding ViewModel
- Changing ServiceStore or UiStore

## Decisions

1. **State management**: Activity holds mutable state from UiStore/ServiceStore. Changes write through immediately.
2. **Running state**: When clashRunning is true, all preferences disabled + Snackbar/Toast shown.
3. **VPN dependency**: Sub-preferences enabled only when enableVpn is true AND not running.
4. **Access control mode**: Reuse SelectionDialog pattern from MetaFeatureSettingsScreen.
5. **Navigation**: Activity starts AccessControlActivity via Intent.
6. **System proxy**: Only shown on API 29+, checked at composition time via Build.VERSION.SDK_INT.

## Risks / Trade-offs

- Simple screen, low risk. Reuses patterns established in MetaFeatureSettingsScreen.
