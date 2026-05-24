## Decisions
1. Track updating state via Set<Int> of indices currently being updated
2. Handle ProfileLoaded event to restart activity on config change
3. Update All fires concurrent coroutines for each non-Inline provider
