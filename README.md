# Harvest Pay — Agricultural Service Ledger

Harvest Pay is an Android app for tractor and agricultural service owners to manage customers, fields, work, payments and pending balances. **Current version: v1.4.2 (14)**. Developed by Tarun.

## Repository layout

```text
Harvest-Pay/
├── README.md
└── HarvestPay-v1.4.2-source/
```

Open **[HarvestPay-v1.4.2-source](HarvestPay-v1.4.2-source/)** in Android Studio. All application code, Gradle files, resources, schemas, tests and project documentation are inside that folder. Git's hidden metadata is retained locally.

- [What's new in v1.4.2](HarvestPay-v1.4.2-source/README.md)
- [Project guide](HarvestPay-v1.4.2-source/PROJECT_GUIDE.md)
- [Release history](HarvestPay-v1.4.2-source/CHANGELOG.md)
- [Build and verification report](HarvestPay-v1.4.2-source/RELEASE_REPORT.md)
- [GitHub Releases](https://github.com/tarrun01/Harvest-Pay/releases)

The v1.4.2 optimized APK was built and safely tested as an update. Its GitHub release is currently a draft without an APK asset because automated approval blocked the embedded Maps key upload. APKs belong in Releases, not the source tree.

## About the app

Harvest Pay is a local Android ledger for tractor and agricultural service owners. It manages customers, physical fields, Bigha- or minute-based work, receipts, payments, pending balances, historical accounts, diesel purchases and service reminders. Current version: **1.4.2 / code 14**, package `com.harvestpay.app`, Android 6.0/API 23 or newer; compile/target API 36.

The application works from local storage. Google map imagery and place search require internet and a configured Google Maps project. Financial records are not sent to a custom backend. Local account credentials protect the application's entry flow; the app does not provide cloud account synchronization.

## Main workflows

### Dashboard and customers

The dashboard presents account totals, pending amounts, work insights and Quick Add. Its search action opens customer search with focus for typing. Work type management is available in More; the dashboard does not include the removed work-type section.

Customer cards are compact: name, optional nickname, pending balance, edit and delete actions. A customer profile includes fields, work, payments and archived accounts. The removed profile Notes tab is absent. Total bill and pending balance appear immediately below Share PDF; pending is red when positive and green when zero. These UI changes retain the existing account calculation rules and customer IDs.

### Fields and measurement

A physical field belongs to an existing customer. Manual field creation uses the existing Save action. Quick Add → Measure Field offers GPS and Satellite:

1. **GPS:** walk around the boundary, watch reported accuracy, pause/resume and complete. Native GNSS supplies fixes. Accuracy, duplicate, jump and speed filters exclude unsuitable points. Active measurement has a visible foreground-service notification.
2. **Satellite:** pan/zoom the map and tap boundary points. Undo, clear, coordinate/place search and optional My location are available. Map browsing itself does not need location permission.
3. Mark Complete → review boundary → Finish → Assign to Customer → choose customer → editable Add Field → Save.

Review supports coordinate edits, removal and undo. Invalid/self-crossing/degenerate polygons are rejected. A paused unfinished draft is stored separately in private storage; recovery requires explicit resume. Measurement completion does not automatically create a field, work entry or payment.

The geodesic engine uses full-precision canonical square meters. Exactly 1 Acre = 4046.8564224 m². Land Measurement Settings stores the owner's positive **Bigha per Acre** factor; no regional value is assumed. Without a factor, measure in Acres and supply the factor before transferring to Add Field. Changing the factor does not recalculate existing records.

Measured Acres/Bigha are displayed with exactly two decimals. Add Field converts directly from original square meters, then rounds its editable numeric input to two decimals using HALF_UP. Original square meters, Acres, factor, boundary and GPS track remain full precision in metadata. Very small positive measurements that produce `0.00` Bigha display a warning and cannot be saved as a zero-size field. Existing manual/historical inputs are not rewritten.

### Work and payments

Work supports physical size × round multiplier × rate, or operating duration × rate according to the work type. Work entries preserve the saved type, rate and billing mode. New Work displays the configured Hindi work-type name in Hindi and the English name in English; missing Hindi names fall back to English. The simplified work form omits rate presets, discount and extra-charge controls and reduces the notes input. Existing stored values and calculation support remain intact.

Payments may be associated with an account/work entry. Account totals distinguish bill, paid, pending and advance. Existing monetary rounding and archive calculations remain unchanged in v1.4.2.

### PDFs and sharing

Customer reports and work receipts are generated locally, with the configured business logo. Customer report types retain current account and relevant history/date selections; the duplicate outstanding report option was removed from the selection dialog. PDF output can be **English, Hindi or Both** regardless of application language. PDF templates, monetary/date formatting, fonts and layout remain separate from UI localization. The Android share chooser and payment message use application resources.

### Backup and history

More → Backup & Export provides JSON export/import through Android document pickers and CSV export. Only confirm restore after reviewing the selected backup. Backup format version 7 includes measurement metadata and settings and reads earlier supported versions. Unfinished measurement drafts are separate from the complete ledger backup.

Year archives are historical account snapshots. Room schema 8 includes nullable measurement metadata and defaulted boundary/method fields, with explicit migrations. No destructive database migration is enabled.

### Language, appearance and security

More → Language / भाषा selects English or Hindi. AppCompat's native locale APIs recreate the activity immediately and persist the preference. Android 13+ system app-language settings synchronize through the framework; Android 6–12 uses AppCompat storage. English is selected when no prior preference exists. User-entered names, notes, field labels, villages and stored financial identifiers are never machine-translated.

Text resources live in `res/values/strings.xml` and `res/values-hi/strings.xml`. Background services and workers resolve the same app locale through `ContextCompat.getContextForLanguage`. Controlled legacy labels/enum IDs are mapped at presentation through `StableLabels`; identifiers in storage stay unchanged. Native plurals are used for boundary-point counts. UI date formatting uses the app locale; PDF formatting remains independent.

Settings retain owner/business values, rates and light/dark/system appearance. Biometric locking and password change retain their existing behavior. Password verification uses the local credential implementation in `security/LocalAuth.kt`; credentials and customer records must not be placed in Git or release notes.

More → About → What's New works offline and shows v1.4.2, v1.4.1, v1.4.0 and the documented v1.2.2 in descending order. One row expands at a time. The badge reads installed package metadata. v1.4.0's missing historical documentation is explicitly disclosed instead of inventing features or dates.

## Architecture and source map

| Area | Location and responsibility |
| --- | --- |
| App entry | `HarvestPayApplication.kt`, `MainActivity.kt`: repositories, app resources, authentication/biometric host, Compose entry |
| State/actions | `HarvestViewModel.kt`: state flows, validation and existing save/backup/report actions |
| Local persistence | `data/`: Room entities, DAO, migrations/repository and DataStore settings |
| Business logic | `domain/`: account totals, work billing, archives, reports, service calculations and feedback models |
| UI | `ui/screens/`, `ui/components/`, `ui/navigation/`, `ui/theme/`: Material 3 Compose screens and routing |
| Localization | `localization/`, `res/values[-hi]/strings.xml`, `res/xml/locales_config.xml` |
| Measurement | `measurement/`: geodesic engine, draft store, GPS service, map, workflow and display formatting |
| Export | `util/BackupCodec.kt`, `ReceiptGenerator.kt`, `PdfLogo.kt`, `ShareUtils.kt`, `Formatters.kt` |
| Notifications | `notification/ReminderWorker.kt` and GPS foreground service |
| Tests | `src/test/`, `src/androidTest/`, `app/schemas/` |

Core stack: Kotlin 2.2.21, Compose/Material 3, Navigation Compose, Lifecycle/ViewModel, Room 2.8.4, DataStore, WorkManager, AndroidX Biometric, AppCompat 1.7.1 and Fragment 1.8.9. Maps SDK for Android 20.0.0 provides imagery; native Android location APIs provide GNSS. Gradle dependency versions are pinned in the checked-in build files.

## Development setup

1. Open `HarvestPay-v1.4.2-source` in Android Studio; use a Java 21 Gradle runtime.
2. Install SDK platform 36 and build-tools 36.0.0. `local.properties` supplies the SDK path locally.
3. Configure Maps locally if needed: `MAPS_API_KEY=...` in ignored `local.properties`, or the `MAPS_API_KEY` environment variable. Enable Maps SDK for Android and billing in the key's Google Cloud project. Restrict the key by Android package/certificate and API. Do not commit the machine configuration.
4. Sync Gradle and build using the wrapper.

```bash
cd HarvestPay-v1.4.2-source
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
./gradlew :app:assemblePreview
adb devices -l
adb install -r app/build/outputs/apk/preview/app-preview.apk
```

The focused v1.4.2 unit suite selects `MeasurementFormattingTest`, `LocalizationResourcesTest`, `DashboardInsightDataTest`, `DashboardGreetingTest` and `ShareUtilsTest` with Gradle `--tests` immediately following the unit-test task. Full device tests cover migrations, measurement workflow and the real activity result host. **Do not run test tooling that uninstalls the real application against a phone containing the owner's ledger**. Use an isolated emulator/test profile or perform safe manual checks of the update.

## R8 and signing

AGP bundles R8; no separate R8 installation is needed. Release enables minification, optimization, obfuscation and resource shrinking with the optimized default ProGuard configuration. `preview` inherits those flags and uses the existing development certificate. Strict full-mode keep rules and optimized resource shrinking are enabled. App rules keep generic signatures and Kotlin metadata; integrations also supply their consumer rules. Avoid blanket package keep rules.

No production keystore was supplied. The owner explicitly authorized the development certificate for v1.4.2. This APK is optimized and signed, but it is not signed with a production key. Preserve the existing certificate for compatible updates. Keep the exact mapping file and APK together for diagnostics. Install with `adb install -r`; never uninstall or clear data to bypass signature incompatibility.

## Release maintenance

- Increment versionCode and update versionName in `app/build.gradle.kts`.
- Add paired English/Hindi resources and accurately sourced history notes.
- Verify changed modules, lint, optimized APK metadata/signature and safe device update.
- Keep machine configuration, APKs, keystores and build outputs out of source commits.
- Commit and push reviewed source changes to the repository.
- Publish authorized APK artifacts in GitHub Releases after verification.
- See [RELEASE_REPORT.md](HarvestPay-v1.4.2-source/RELEASE_REPORT.md) for exact build results and any remaining limitations.
