## 1. Create NetworkSettingsScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/NetworkSettingsScreen.kt` with Scaffold, TopAppBar, and scrollable Column
- [x] 1.2 Implement VPN toggle switch with dependency logic
- [x] 1.3 Implement VPN sub-preferences (bypass private network, DNS hijacking, allow bypass, allow IPv6, system proxy)
- [x] 1.4 Implement access control mode selector and packages navigation item

## 2. Rewrite Activity

- [x] 2.1 Rewrite `NetworkSettingsActivity` to extend `BaseComposeActivity` with `setContent`
- [x] 2.2 Implement clash state change handling (recreate on start/stop)

## 3. Cleanup & Verify

- [x] 3.1 Delete `NetworkSettingsDesign.kt` and its XML layout
- [x] 3.2 Verify build passes
