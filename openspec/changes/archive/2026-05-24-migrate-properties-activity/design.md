## Context
PropertiesActivity is a dynamic form for editing profile properties (name, URL, auto-update interval).

## Decisions
1. Reuse requestModelTextInput from existing dialog infrastructure (no re-implementation needed)
2. Reuse withModelProgressBar for commit progress
3. Use MaterialAlertDialogBuilder for exit-without-saving confirmation
4. Auto-save on onStop (existing behavior preserved)
