## Context
LogcatActivity has two modes:
1. Streaming: live logs from LogcatService (foreground service)
2. File: archived logs from LogcatReader

## Goals
- Migrate to Compose + M3 using BaseComposeActivity
- Use LazyColumn with rememberLazyListState
- Auto-scroll preserving user position
- Use registerForActivityResult for export (replaces startActivityForResult)
- Remove LogcatDesign.kt, LogMessageAdapter.kt, XML layouts

## Decisions
1. LazyColumn instead of reverseLayout RecyclerView — auto-scroll is more idiomatic in Compose
2. Streaming polls service every 500ms via lifecycleScope coroutine
3. Service binding kept identical (queryLocalInterface for cross-process binding)
4. File export uses registerForActivityResult (modern API), replacing the suspend startActivityForResult
