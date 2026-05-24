## Why

All 19 Activities have been migrated from View-based (`BaseActivity` + `Design` classes) to Jetpack Compose (`BaseComposeActivity` + `setContent`). The old View infrastructure (adapters, XML layouts, custom views, base classes, menu resources) is now dead code that inflates build times, confuses navigation, and blocks removal of Material 2 / AppCompat / DataBinding dependencies.

## What Changes

- **BREAKING**: Remove `BaseActivity<D : Design<*>>` and `Design<T>` base class (no subclasses remain)
- Remove orphaned adapter classes (`ProviderAdapter`, `LogFileAdapter`, `ProfileAdapter`, `FileAdapter`, `AppAdapter`)
- Remove orphaned XML layouts (`design_settings_common.xml`, `dialog_search.xml`, `common_activity_bar.xml`, `common_recycler_list.xml`, `adapter_sideload_provider.xml`)
- Remove orphaned menu resources (`menu_proxy.xml`, `menu_access_control.xml`)
- Remove orphaned custom view components and utilities that only served deleted Design classes
- Remove `viewBinding` global enablement from root `build.gradle.kts` (no longer used)
- Audit and remove `styles.xml` if confirmed orphaned

## Capabilities

### New Capabilities
- `dead-code-removal`: Systematic removal of orphaned View-based infrastructure after Compose migration

### Modified Capabilities

## Impact

- `design` module: bulk file deletions (adapters, views, layouts, menus, utilities)
- `app` module: `BaseActivity.kt` removal
- Root `build.gradle.kts`: `viewBinding` config removal
- Build time improvement from fewer generated binding classes and reduced annotation processing
- No runtime behavior change — all deleted code is unreachable
