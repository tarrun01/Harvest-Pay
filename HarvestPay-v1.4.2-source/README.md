# Harvest Pay v1.4.3 — What's New

**Version:** 1.4.3 · **Version code:** 15 · **Package:** `com.harvestpay.app` · **Android:** 6.0/API 23+

## Changes in v1.4.3

- Payments opens **History** first, followed by **Pending**.
- Pending customer cards are smaller and use the standard card color. They show only the customer name and pending amount.
- Tap anywhere on the card outside its action buttons to open customer Details.
- **Mark paid** and **Add partial payment** appear side by side, with **Schedule reminder** directly below.
- Mark paid still asks for confirmation before recording a full payment. Existing payment, reminder and balance logic is preserved.
- **More → About → What's New** includes English/Hindi v1.4.3 notes and the installed-version badge.
- Preview builds retain R8 optimization, obfuscation and resource shrinking with the existing compatible development certificate.

## Changes in v1.4.2

### English and Hindi interface

- Native English/Hindi resources cover screens, dialogs, validation, accessibility labels, sharing messages and notifications.
- **More → Language / भाषा** changes the app language immediately and persists after restart. English is the default when no preference exists.
- Android 13+ system app-language settings synchronize with the app; earlier supported versions use AppCompat locale storage.
- New Work shows your configured **Hindi work-type names** when Hindi is selected, and English names in English. It falls back to English if the Hindi name is empty. Stored identifiers, selected rates and billing calculations remain unchanged.
- User-entered names, addresses and notes remain as entered. PDF language stays independently selectable as English, Hindi or Both.

### Offline version history

- **More → About → What's New** displays localized, expandable release history without fetching it online.
- Releases are ordered newest first; only one section expands at a time.
- The current-version badge reads installed package metadata.
- Missing v1.4.0 notes are explicitly marked unavailable.

### Measurement precision and layout

- GPS, satellite, boundary review, completion and saved-boundary measured Acres/Bigha display exactly two decimals using HALF_UP rounding.
- Add Field receives an editable two-decimal Bigha value converted directly from original square meters using the configured factor.
- Full-precision area, boundary and GPS metadata remain preserved. Historical fields and bills are not recalculated.
- Very small measurements that round to zero show a warning; zero-sized fields cannot be saved.
- Hindi satellite controls wrap naturally so long labels and Search remain readable.

### Build and existing features

- R8 optimization, obfuscation and resource shrinking remain enabled.
- Includes the existing GPS/satellite workflows, map rendering and modern Activity Result fixes for location permissions and backup import/export.
- Retains compact customers, profile totals directly below Share PDF, pending balance colors, simplified work entry, independent PDF language selection and dashboard search focus.

## v1.4.2 verification

- 12 focused unit tests passed with zero failures.
- Debug lint: zero errors, 35 warnings; optimized variant vital lint passed.
- Optimized APK signatures verified and installed over the existing app on Samsung A34 / Android 16 without clearing data.
- Hindi work-type selection/options, English display, native locale switching and restart persistence were checked.
- Maps, GPS Start/cancel, measurement transfer, history and backup document pickers were checked. Temporary drafts/pickers were cancelled without saving test records or restoring data.
- Outdoor walking accuracy and Android 6–12 locale persistence were not physically tested.

## Build

Open this folder in Android Studio. Use Java 21, SDK platform 36/build-tools 36.0.0 and the checked-in Gradle wrapper. R8 is bundled with AGP.

```bash
./gradlew :app:assemblePreview
adb install -r app/build/outputs/apk/preview/app-preview.apk
```

The `preview` variant inherits release optimization and uses the owner's authorized existing development certificate. No production keystore was supplied. Install compatible updates over the existing app; do not uninstall a populated ledger to bypass a signing mismatch. Keep the exact R8 mapping with its APK.

Configure Maps through ignored `local.properties` or `MAPS_API_KEY` in the build environment. Enable Maps SDK for Android and restrict the key by API, package and signing certificate. Machine configuration, keys, APKs and build outputs are excluded from Git.

## Documentation

- [Full app details and workflows](../README.md)
- [Project guide and architecture](PROJECT_GUIDE.md)
- [Complete changelog](CHANGELOG.md)
- [Latest APK metadata and verification report](RELEASE_REPORT.md)
- [v1.4.2 verification report](RELEASE_REPORT_1.4.2.md)

The GitHub v1.4.2 release remains a draft without an APK asset; its upload was blocked by automatic approval review. The tested APK and diagnostics remain available in this local project's ignored build outputs.
