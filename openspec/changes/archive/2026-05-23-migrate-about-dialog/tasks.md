## 1. AboutDialogContent Composable

- [x] 1.1 Create `design/src/main/java/com/github/kr328/clash/design/compose/AboutDialogContent.kt` with app icon + name + version in horizontal layout
- [x] 1.2 Use `painterResource` for app icon and `stringResource` for app name

## 2. MainDesign Integration

- [x] 2.1 Replace `showAbout()` in `MainDesign.kt` to use `ComposeView` with `AboutDialogContent` inside AlertDialog
- [x] 2.2 Remove `DesignAboutBinding` import from `MainDesign.kt`

## 3. Verification

- [x] 3.1 Build project successfully (`./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin`)
- [x] 3.2 Remove old `design_about.xml` after Compose dialog is verified working
