## ADDED Requirements

### Requirement: Remove orphaned adapter classes
All adapter classes that were only used by deleted Design classes must be removed.

#### Scenario: Orphaned adapters deleted
- **WHEN** build is run after removing ProviderAdapter, LogFileAdapter, ProfileAdapter, FileAdapter, AppAdapter
- **THEN** build succeeds with no unresolved references

### Requirement: Remove orphaned XML layouts
All XML layout files that are no longer referenced by any Kotlin code must be removed.

#### Scenario: Orphaned layouts deleted
- **WHEN** build is run after removing design_settings_common.xml, dialog_search.xml, common_activity_bar.xml, common_recycler_list.xml, adapter_sideload_provider.xml
- **THEN** build succeeds with no unresolved references

### Requirement: Remove orphaned menu resources
All menu XML files that are no longer referenced must be removed.

#### Scenario: Orphaned menus deleted
- **WHEN** build is run after removing menu_proxy.xml, menu_access_control.xml
- **THEN** build succeeds with no unresolved references

### Requirement: Remove orphaned custom views and utilities
Custom View classes and utility functions that only served deleted Design classes must be removed.

#### Scenario: Orphaned views and utilities deleted
- **WHEN** build is run after removing orphaned view/utility files
- **THEN** build succeeds with no unresolved references

### Requirement: Remove BaseActivity and Design base class
The old View-based base classes that have no remaining subclasses must be removed.

#### Scenario: Base classes deleted
- **WHEN** build is run after removing BaseActivity.kt and Design.kt
- **THEN** build succeeds with no unresolved references

### Requirement: Remove viewBinding global config
The viewBinding enablement in root build.gradle.kts must be removed since no code uses it.

#### Scenario: viewBinding disabled
- **WHEN** build is run after removing viewBinding config
- **THEN** build succeeds and no ViewBinding-generated classes are expected
