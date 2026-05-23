## 1. Create BaseComposeActivity

- [x] 1.1 Create `app/src/main/java/com/github/kr328/clash/BaseComposeActivity.kt` extending ComponentActivity with Broadcasts.Observer
- [x] 1.2 Add `clashRunning` property, `uiStore` lazy property, `events` channel
- [x] 1.3 Implement `onCreate` with enableEdgeToEdge + hideFromRecents enforcement
- [x] 1.4 Implement `onStart`/`onStop` for broadcast observer registration
- [x] 1.5 Implement Broadcasts.Observer callbacks sending events to channel

## 2. Update Migrated Activities

- [x] 2.1 Update `HelpActivity` to extend `BaseComposeActivity` and remove `enableEdgeToEdge()` call
- [x] 2.2 Update `AppCrashedActivity` to extend `BaseComposeActivity` and remove `enableEdgeToEdge()` call
- [x] 2.3 Update `ApkBrokenActivity` to extend `BaseComposeActivity` and remove `enableEdgeToEdge()` call
- [x] 2.4 Update `AppSettingsActivity` to extend `BaseComposeActivity`, use inherited `clashRunning` and `uiStore`, remove `Remote` import

## 3. Verification

- [x] 3.1 Build project successfully (`./gradlew :app:compileAlphaDebugKotlin`)
