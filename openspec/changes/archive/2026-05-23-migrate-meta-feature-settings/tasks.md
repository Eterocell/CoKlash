## 1. Create MetaFeatureSettingsScreen Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/MetaFeatureSettingsScreen.kt` with Scaffold, TopAppBar (back + reset action), and scrollable Column
- [x] 1.2 Implement reusable `TriStateListItem` composable (nullable Boolean → dialog with 3 options)
- [x] 1.3 Implement reusable `SelectableListItem` composable (generic enum/nullable values → dialog)
- [x] 1.4 Implement reusable `EditableTextListItem` composable (List<String>? → dialog with add/remove)
- [x] 1.5 Implement Settings category (unified delay, geodata mode, TCP concurrent, find process mode)
- [x] 1.6 Implement Sniffer category with enable toggle and dependent sub-preferences
- [x] 1.7 Implement GeoX Files category with import clickable items

## 2. Rewrite Activity

- [x] 2.1 Rewrite `MetaFeatureSettingsActivity` to extend `BaseComposeActivity` with `setContent`
- [x] 2.2 Implement file picker launchers and import logic in Activity
- [x] 2.3 Implement reset override confirmation and patching logic

## 3. Cleanup & Verify

- [x] 3.1 Delete `MetaFeatureSettingsDesign.kt` and its XML layout
- [x] 3.2 Verify build passes with `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin`
