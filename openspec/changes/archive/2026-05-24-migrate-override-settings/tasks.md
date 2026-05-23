## 1. Create OverrideSettingsScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/OverrideSettingsScreen.kt` with Scaffold, TopAppBar (back + reset action), and scrollable Column
- [x] 1.2 Implement EditablePortItem composable (nullable Int port with text input dialog)
- [x] 1.3 Implement EditableTextItem composable (nullable String with text input dialog)
- [x] 1.4 Implement EditableTextMapItem composable (Map<String, String>? with key-value dialog)
- [x] 1.5 Implement General category (ports, allow LAN, IPv6, bind address, external controller, CORS, secret, mode, log level, hosts)
- [x] 1.6 Implement DNS category with enable toggle and dependent sub-preferences

## 2. Rewrite Activity

- [x] 2.1 Rewrite `OverrideSettingsActivity` to extend `BaseComposeActivity` with `setContent`
- [x] 2.2 Implement reset override confirmation and patching logic

## 3. Cleanup & Verify

- [x] 3.1 Delete `OverrideSettingsDesign.kt` and its XML layout
- [x] 3.2 Verify build passes
