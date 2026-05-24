## Context

All 19 Activities have been migrated to Jetpack Compose + Material 3. The old View-based infrastructure remains in the codebase as dead code: base classes, adapters, XML layouts, custom views, menu resources, and utilities that only served the deleted `*Design.kt` classes.

## Goals / Non-Goals

**Goals:**
- Remove all dead code left over from the View-based architecture
- Remove orphaned XML resources (layouts, menus)
- Remove `viewBinding` global config (no longer used)
- Reduce build time by eliminating unnecessary generated binding classes
- Keep the build green at every step

**Non-Goals:**
- Migrating remaining legacy infrastructure that IS still used (preference system, dialog helpers, editable text adapters)
- Removing Material 2 / AppCompat dependencies (still needed by dialogs and preferences)
- Removing DataBinding (still needed by preference XML layouts)
- Refactoring the preference system to Compose (separate future change)

## Decisions

1. **Delete in dependency order**: Remove leaf files first (adapters, XML), then components, then base classes — verify build after each batch.
2. **Keep preference infrastructure**: `EditableTextListAdapter`, `EditableTextMapAdapter`, `PopupListAdapter` and their XML layouts are still used by the preference system. Do NOT delete.
3. **Keep dialog infrastructure**: `requestModelTextInput`, `withModelProgressBar`, `Input.kt`, `Progress.kt` are still used by migrated Activities. Do NOT delete.
4. **Remove `viewBinding` but keep `dataBinding`**: viewBinding is unused; dataBinding is still needed for preference/dialog XML layouts.
5. **Batch deletions by category**: adapters → XML layouts → menus → custom views → utilities → base classes. Verify build between batches.

## Risks / Trade-offs

- **Risk**: Accidentally deleting a file still referenced by preference/dialog code → mitigated by build verification after each batch.
- **Trade-off**: Keeping Material 2 / AppCompat for now means the dependency tree isn't fully cleaned. This is acceptable because the preference system and dialogs still depend on them.
- **Trade-off**: Not removing DataBinding means annotation processing overhead remains. Acceptable until the preference system is migrated.
