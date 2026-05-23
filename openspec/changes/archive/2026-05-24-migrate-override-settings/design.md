## Context

OverrideSettingsActivity is the largest preference-list screen. It manages ConfigurationOverride with two categories: General (ports, allow LAN, IPv6, bind address, external controller, CORS, secret, mode, log level, hosts map) and DNS (enable with dependency logic, prefer H3, listen, append system DNS, IPv6, use hosts, enhanced mode, nameservers, fallback, default server, fake-ip filter, fake-ip filter mode, fallback filter geoip/code/domain/ipcidr, nameserver policy map).

## Goals / Non-Goals

**Goals:**
- Migrate OverrideSettingsActivity to Compose + M3 using BaseComposeActivity
- Implement all preference types: editable text (nullable Int port, nullable String), tri-state Boolean, selectable list (enum), editable text list, editable text map (key-value)
- Implement DNS dependency logic (disable sub-prefs when DNS enable is false)
- Implement reset override confirmation dialog and patching logic
- Remove old OverrideSettingsDesign.kt and XML layout

**Non-Goals:**
- Adding ViewModel
- Changing ConfigurationOverride model

## Decisions

1. **Reuse composables**: TriStateListItem, SelectionDialog, EditableTextListItem patterns from MetaFeatureSettingsScreen
2. **New composables**: EditablePortItem (nullable Int port), EditableTextItem (nullable String), EditableTextMapItem (Map<String, String>?)
3. **DNS dependency**: Same pattern as sniffer — `enabled = dnsEnabled != false`
4. **State management**: Activity holds ConfigurationOverride, mutableStateOf for each field, patches on finish

## Risks / Trade-offs

- Large file (~400+ lines composable). Acceptable for a single-screen migration.
- EditableTextMapItem is new — needs key+value input dialog.
