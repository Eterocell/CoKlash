## Why

NetworkSettingsActivity is next in migration order (Priority 7). It's a preference-list screen for VPN and network options (route system traffic toggle, bypass private network, DNS hijacking, allow bypass, allow IPv6, system proxy, access control mode, and access control packages navigation). Simpler than MetaFeatureSettings — just switches with VPN dependency logic.

## What Changes

- Create `NetworkSettingsScreen.kt` composable in `design/src/main/java/com/github/kr328/clash/design/compose/`
- Rewrite `NetworkSettingsActivity` to extend `BaseComposeActivity` and use `setContent { }`
- Remove `NetworkSettingsDesign.kt` and its XML layout
- Implement VPN dependency logic (disable sub-preferences when VPN is off or clash is running)
- Implement access control mode selector (AcceptAll/AcceptSelected/DenySelected)
- Implement navigation to AccessControlActivity

## Scope

- Files created: `design/.../compose/NetworkSettingsScreen.kt`
- Files modified: `app/.../NetworkSettingsActivity.kt`
- Files deleted: `design/.../NetworkSettingsDesign.kt`, associated XML layout
