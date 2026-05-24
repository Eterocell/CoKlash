## Decisions
1. Search mode replaces TopAppBar content with TextField (Compose SearchBar pattern)
2. Overflow menu uses DropdownMenu (replaces PopupMenu from AccessControlMenu)
3. Save selections on onDestroy, restart Clash service if changed
4. Filtering done in composable via derivedStateOf or inline filter
