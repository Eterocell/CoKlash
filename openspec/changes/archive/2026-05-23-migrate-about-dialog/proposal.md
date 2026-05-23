## Why

AboutActivity (Priority 4) is actually a dialog shown from MainDesign, not a standalone Activity. It displays a simple AlertDialog with app icon, name, and version. Migrating it to a Compose dialog validates the pattern for replacing View-based dialogs with Compose within a still-View-based host (MainDesign).

## What Changes

- Create an `AboutDialog` composable in `design/compose/`
- Replace `MainDesign.showAbout()` to use ComposeView as AlertDialog content
- Remove the XML layout `design_about.xml`
- Remove the `DesignAboutBinding` import from `MainDesign.kt`

## Capabilities

### New Capabilities

- `about-dialog-compose`: About dialog reimplemented as a Compose composable

### Modified Capabilities

## Impact

- `design/.../MainDesign.kt`: Replace `showAbout()` implementation to use ComposeView + Compose dialog content
- `design/.../compose/AboutDialog.kt`: New composable for the About dialog content
- `design/src/main/res/layout/design_about.xml`: Remove
