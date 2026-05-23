## Context

The About dialog is a method `showAbout(versionName)` in `MainDesign.kt` that creates an `AlertDialog` with `DesignAboutBinding` (inflated from `design_about.xml`). It shows app icon, app name, and version string. It's invoked from `MainActivity` when the user taps "About" in the main menu.

Since `MainActivity` and `MainDesign` are still View-based (Priority 19, last to migrate), the About dialog must remain callable from `MainDesign.showAbout()`. The migration replaces the XML-inflated content with a `ComposeView` hosting a Compose composable inside the existing `AlertDialog`.

## Goals / Non-Goals

**Goals:**
- Replace `DesignAboutBinding` + `design_about.xml` with a Compose composable
- Keep `MainDesign.showAbout(versionName)` API unchanged
- Display app icon, name, and version in the dialog

**Non-Goals:**
- Migrating the AlertDialog itself to Compose Dialog (would require Activity-level Compose host)
- Changing how About is triggered from MainActivity
- Adding new features to the About dialog

## Decisions

### 1. Use ComposeView inside AlertDialog

**Choice:** Keep `AlertDialog.Builder` but set its view to a `ComposeView` hosting the composable.
**Rationale:** `MainDesign` is still View-based. A pure Compose `Dialog` requires a Compose host (setContent). Using `ComposeView` bridges the gap cleanly.
**Alternative considered:** Pure Compose Dialog from Activity level — rejected because it would require modifying `MainActivity` which is not being migrated yet.

### 2. Composable as standalone reusable component

**Choice:** Create `AboutDialogContent` composable that renders the icon + name + version layout.
**Rationale:** When `MainActivity` is eventually migrated, this composable can be reused directly in a Compose `AlertDialog` without changes.

## Risks / Trade-offs

- **Trade-off:** Hybrid approach (ComposeView inside AlertDialog) is temporary — will be replaced when MainActivity migrates. Acceptable for incremental migration.
