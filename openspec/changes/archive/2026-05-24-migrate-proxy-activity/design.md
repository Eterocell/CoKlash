## Decisions
1. Replace ViewPager2 + TabLayout with HorizontalPager + ScrollableTabRow
2. Replace ProxyPageAdapter with Map<Int, List<Proxy>> state
3. Mode switching via MaterialAlertDialogBuilder (replaces ProxyMenu PopupMenu)
4. URL test state tracked via Set<Int> of testing group indices
