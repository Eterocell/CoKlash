## 1. Remove Orphaned Adapter Classes

- [x] 1.1 `design/src/main/java/.../adapter/ProviderAdapter.kt`: Delete file, no Kotlin references remain
- [x] 1.2 `design/src/main/java/.../adapter/LogFileAdapter.kt`: Delete file, no Kotlin references remain
- [x] 1.3 `design/src/main/java/.../adapter/ProfileAdapter.kt`: Delete file, no Kotlin references remain
- [x] 1.4 `design/src/main/java/.../adapter/FileAdapter.kt`: Delete file, no Kotlin references remain
- [x] 1.5 `design/src/main/java/.../adapter/AppAdapter.kt`: Delete file, no Kotlin references remain
- [x] 1.6 Run `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 2. Remove Orphaned XML Layouts

- [x] 2.1 `design/src/main/res/layout/design_settings_common.xml`: Delete, no Kotlin references
- [x] 2.2 `design/src/main/res/layout/dialog_search.xml`: Delete, no Kotlin references
- [x] 2.3 `design/src/main/res/layout/common_activity_bar.xml`: Delete, only included by deleted design_settings_common.xml
- [x] 2.4 `design/src/main/res/layout/common_recycler_list.xml`: Delete, no Kotlin references
- [x] 2.5 `design/src/main/res/layout/adapter_sideload_provider.xml`: Delete, no Kotlin references
- [x] 2.6 Delete adapter XML layouts whose only referencing Kotlin file was deleted in step 1: `adapter_file.xml`, `adapter_profile.xml`, `adapter_provider.xml`, `adapter_app.xml`
- [x] 2.7 Run `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 3. Remove Orphaned Menu Resources

- [x] 3.1 `design/src/main/res/menu/menu_proxy.xml`: Delete, no Kotlin references (ProxyMenu.kt already deleted)
- [x] 3.2 `design/src/main/res/menu/menu_access_control.xml`: Delete, no Kotlin references (AccessControlMenu.kt already deleted)
- [x] 3.3 Run `./gradlew :design:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 4. Remove Orphaned Custom Views and Utilities

- [x] 4.1 Delete `design/src/main/java/.../design/util/Elevation.kt`: no external call sites
- [x] 4.2 Delete `design/src/main/java/.../design/util/Interval.kt`: no external call sites
- [x] 4.3 Delete `design/src/main/java/.../design/util/ActivityBar.kt`: no external call sites
- [x] 4.4 Delete `design/src/main/java/.../design/view/VerticalScrollableHost.kt`: no references
- [x] 4.5 Delete `design/src/main/java/.../design/view/LargeActionLabel.kt`: no references
- [x] 4.6 Delete `design/src/main/java/.../design/view/LargeActionCard.kt`: no references
- [x] 4.7 Delete `design/src/main/java/.../design/view/ActionTextField.kt`: no references
- [x] 4.8 Remove `AppBottomSheetDialog` class from `Dialogs.kt` (keep `FullScreenDialog` — still used by Overlay.kt)
- [x] 4.9 Run `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 5. Remove BaseActivity and Design Base Class

- [x] 5.1 Delete `app/src/main/java/.../BaseActivity.kt` — no subclasses remain
- [x] 5.2 Delete `design/src/main/java/.../design/Design.kt` — no subclasses remain
- [x] 5.3 Fix any remaining references to Design in utility files (e.g., Toast.kt) — rewrite to not depend on Design
- [x] 5.4 Run `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 6. Remove viewBinding Global Config

- [x] 6.1 In root `build.gradle.kts`: remove `viewBinding = name != "hideapi"` line
- [x] 6.2 Run `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 7. Remove Orphaned styles.xml

- [x] 7.1 Verify `design/src/main/res/values/styles.xml` has no remaining references
- [x] 7.2 If confirmed orphaned, delete it
- [x] 7.3 Run `./gradlew :design:compileAlphaDebugKotlin :app:compileAlphaDebugKotlin` — expect BUILD SUCCESSFUL

## 8. Final Verification

- [x] 8.1 Run full project build: `./gradlew assembleAlphaDebug` — expect BUILD SUCCESSFUL
- [x] 8.2 Commit all cleanup changes
