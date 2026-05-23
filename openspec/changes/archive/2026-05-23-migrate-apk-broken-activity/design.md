## Context

ApkBrokenActivity displays an error when the APK is incomplete/corrupted. It uses `ApkBrokenDesign` which inflates `DesignSettingsCommonBinding` (shared preference-style layout) and builds a preference screen with a tip message and a single clickable "GitHub Releases" link. The screen has minimal state — it only sends a URL request when the link is clicked.

Compose infrastructure (BOM, M3, CoKlashTheme) is already established. This follows the same pattern as HelpActivity and AppCrashedActivity migrations.

## Goals / Non-Goals

**Goals:**
- Migrate ApkBrokenActivity to Compose using the established pattern
- Display error tip and a single reinstall link (GitHub Releases)
- Maintain the same user experience (tip + clickable link opens browser)

**Non-Goals:**
- Adding new features (retry, diagnostics, etc.)
- Changing the error detection mechanism that launches this activity
- Introducing ViewModel (zero mutable state)

## Decisions

### 1. No ViewModel — pass lambda directly

**Choice:** Activity provides `onLinkClick` lambda to the composable.
**Rationale:** Zero state. The screen only opens a URL on click. Same pattern as HelpActivity.

### 2. Use AnnotatedString.fromHtml for tips text

**Choice:** Parse `R.string.application_broken_tips` with `AnnotatedString.fromHtml()` since it contains `<strong>` tags (consistent with HelpScreen pattern).
**Rationale:** Established pattern from HelpActivity migration. HTML formatting must be preserved.

## Risks / Trade-offs

- **Risk:** None significant — this is the simplest possible screen migration.
- **Trade-off:** Removing the shared `DesignSettingsCommonBinding` usage means one fewer consumer of that layout, but other screens still use it.
