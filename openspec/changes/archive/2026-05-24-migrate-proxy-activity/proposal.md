## Why
ProxyActivity is Priority 17 in migration order. Tabs + real-time proxy data.

## What Changes
- Create ProxyScreen.kt composable with HorizontalPager + ScrollableTabRow
- Rewrite ProxyActivity to extend BaseComposeActivity
- Remove ProxyDesign.kt, ProxyMenu, ProxyView*, ProxyAdapter*, design_proxy.xml
