# Harvest Pay v1.4.2

Harvest Pay is an offline-first Android ledger for a tractor owner who charges farmers by field size or by operating time. It is built with Kotlin, Jetpack Compose, Material 3, Room, MVVM, Navigation Compose and DataStore.

## What's New — v1.4.2

- Work-type selection in Hindi uses the owner-provided Hindi name, with the English name as fallback; stored identifiers and billing remain unchanged.

- Added native English and Hindi resources throughout screens, dialogs, validation, accessibility labels, sharing messages and notification text.
- Added **More → Language / भाषा** with immediate Android per-app locale switching and persistent selection. English remains the default when no preference exists; Android 13+ system app-language settings use the same preference.
- Added **More → About → What's New**, an offline, expandable English/Hindi version history. One release expands at a time; the current-version badge reads installed package metadata. Undocumented v1.4.0 notes are explicitly marked unavailable.
- GPS, satellite, boundary review, completion and saved boundary area displays use two decimal places with HALF_UP rounding.
- Add Field receives an editable two-decimal Bigha value converted from original full-precision square meters. Positive measurements that round to zero show a warning; the existing positive-size validation still blocks saving zero.
- Preserved full-precision measurement metadata, configured Acre-to-Bigha conversion, historical field sizes, billing calculations and independent English/Hindi/Both PDF selection.
- Retained R8 code optimization, obfuscation and resource shrinking. Version name **1.4.2**, version code **14**, package **com.harvestpay.app**.

See [PROJECT_GUIDE.md](PROJECT_GUIDE.md) for the architecture, workflows, setup and maintenance details, [CHANGELOG.md](CHANGELOG.md) for release history, and [RELEASE_REPORT.md](RELEASE_REPORT.md) for this build's measured verification and delivery details.

### Build and install

Open this directory in Android Studio and let Gradle sync. Install Android SDK platform 36/build-tools 36.0.0 and use a compatible Java 21 Gradle runtime. The checked-in wrapper uses Gradle 9.6; AGP supplies R8 automatically.

```bash
./gradlew :app:assemblePreview
adb install -r app/build/outputs/apk/preview/app-preview.apk
```

The `preview` variant inherits release optimization and uses the existing development certificate. This is the owner's authorized signing choice for this delivery, **not a production release certificate**. It updates only installations with that certificate. The `release` variant has no production signing credentials configured; provide the owner's established key locally for future production distribution. Never resolve a signature mismatch by uninstalling a ledger containing customer data.

Keep `app/build/outputs/mapping/preview/mapping.txt` with the exact APK for crash decoding. Generated APKs, build directories and machine-specific `local.properties` are ignored by Git. GitHub Releases hosts the distributable APK rather than committing it to source.

## What's New — v1.4.1

- Added Measure Field to Quick Add, with GPS and Satellite methods and resume of an unfinished draft.
- Added GPS walking measurement, reported accuracy and signal quality, configurable accuracy filtering, duplicate/jump/speed filtering, a foreground location service, pause/resume and interrupted-track recovery.
- Added satellite polygon drawing, zoom/pan, undo, clear, optional current-location access, and place/coordinate search.
- Added shared boundary review, draggable map markers, coordinate editing/removal, edit undo and invalid-polygon validation.
- Added one geodesic calculation engine for square meters, Acres, hectares and configured Bigha conversion.
- Added Land Measurement Settings with an owner-provided positive Acre-to-Bigha factor. No regional factor is assumed.
- Added Assign to Customer using the existing searchable customer picker and IDs, followed by editable Add Field prefill. Finishing a measurement does not save a field.
- Added measurement metadata, saved boundaries, original GPS tracks and a read-only View Field Boundary action in the field editor.
- Preserved customer records, historical physical field sizes and billing formulas. Success feedback appears only after the existing field save succeeds.
- Updated versionName to 1.4.1 and versionCode to 13.

### Maps setup and privacy

The map renderer uses **Google Maps SDK for Android 20.0.0**, compatible with this project's minSdk 23 and compileSdk 36. Native Android GNSS supplies GPS fixes, including without internet or Google Play location services. Satellite imagery requires Google Play services, internet and a working Maps key.

1. Enable **Maps SDK for Android** in a Google Cloud project and enable billing.
2. Add `MAPS_API_KEY=your_key` to the ignored root `local.properties`, or provide a `MAPS_API_KEY` environment variable to Gradle. Do not commit the key. The owner supplied a key, configured locally on 11 October 2026; the key is not committed with this source.
3. Restrict the key to the Maps SDK for Android API, package `com.harvestpay.app`, and the SHA-1 fingerprint of each allowed signing certificate (`./gradlew signingReport`). Android API keys are shipped in the APK; certificate/API restrictions are essential.
4. Rebuild the APK after configuring the key. A key-free optimized APK uses the coordinate fallback and cannot load satellite imagery.
5. Verify satellite tiles and interactions on the phone. Configuration alone does not prove imagery works.

Without configuration or after an imagery timeout, the app explains the problem, supports retry, and keeps boundary points. GPS tracking/calculation and the zoomable coordinate review work without map tiles. Satellite browsing does not request location; only My location does. GPS requires precise foreground location and enabled device GPS. Active walks use a visible location service notification and can continue with the screen locked; process interruption restores a paused draft for explicit resume, never an automatic background restart. Long signal gaps pause tracking and require the owner to inspect the boundary before saving.

Maps and place search send viewport/location queries to Google. There is no custom boundary server or customer-data upload. Google map attribution remains visible. Smartphone GPS and imagery are estimates, not a legal survey. The geometry engine uses signed spherical triangle excess and the IUGG mean Earth radius; it validates great-circle segment intersections through a gnomonic projection. Very large boundaries spanning a hemisphere are rejected. Conversion uses exactly **1 Acre = 4046.8564224 m²**, with no intermediate rounding.

Provider references: [SDK setup](https://developers.google.com/maps/documentation/android-sdk/config), [billing and pricing](https://developers.google.com/maps/documentation/android-sdk/usage-and-billing), [policies and attribution](https://developers.google.com/maps/documentation/android-sdk/policies), [Google privacy policy](https://policies.google.com/privacy). Maps usage is subject to Google's current terms and SKU pricing; billing must be configured even where an eligible SDK SKU has no usage charge.

### Database, backup and optimization

Room schema **8** adds only nullable measurement values and defaulted MANUAL/empty-boundary columns to `fields` via migration 7→8. `sizeBigha` remains the final accepted physical size. Metadata records method, original square meters/Acres, factor used, date, boundary and original GPS track. Existing records are untouched; no destructive migration is enabled. Changing conversion settings does not recalculate historical fields or bills. JSON backup version 7 preserves new metadata and settings and still reads older backup versions. An unfinished draft is stored separately in private app storage.

R8 is bundled with AGP; it needs no separate installation. Release/preview builds enable code minification, the optimized ProGuard configuration and resource shrinking, including optimized resource shrinking. Preview is signed with the development certificate so it can be installed for testing. Production distribution should use the owner's original release signing key. Keep the generated `app/build/outputs/mapping/preview/mapping.txt` for crash decoding.

### v1.4.1 implementation files

Created: `measurement/FieldGeometry.kt`, `MeasurementStore.kt`, `GpsMeasurementService.kt`, `BoundaryMap.kt`, `MeasurementScreen.kt`, `res/drawable/ic_notification.xml`, geometry/filter unit tests, GPS foreground-service and measurement workflow/recovery device tests, and Room schema 8.

Modified: app build/version configuration, R8 properties, manifest, application, Room entities/database/migrations, DataStore settings, backup codec, field form, settings screen, navigation/Quick Add/customer picker, field validation, migration tests and README. Existing PDF, payments, archives, biometric and billing calculation code are unchanged by this update.

### Verification

Crash follow-up: the real MainActivity inherited Fragment 1.2.5 through Biometric 1.1.0. Its legacy 16-bit request-code validation crashed modern Activity Result permission requests and backup document pickers. Fragment is explicitly aligned to 1.8.9 ([AndroidX releases](https://developer.android.com/jetpack/androidx/releases/fragment#1.8.9)). The earlier device tests hosted screens in a separate ComponentActivity and did not cover this integration.

Verified on 11 October 2026:

- `testDebugUnitTest`: **49 passed**, zero failures/errors/skips, including geometry references, conversions, self-intersections, date-line handling, stationary fixes, jumps, resume distance and existing Round billing.
- Earlier full `connectedDebugAndroidTest` run: **12 passed** on Samsung SM-A346E (Android 16), zero failures/errors/skips. Covers every published Room migration to schema 8, preserved old field sizes, review/validation/undo, missing-conversion prompt, correct customer-ID prefill, metadata persistence, backup round trip, draft recovery and GPS foreground tracking/pause/review/cancel confirmation. GPS initialization uses an injected accurate fix in the service test; this is not an outdoor accuracy test.
- Crash regression device run: **2 passed**, zero failures/errors, in `MainActivityResultIntegrationTest`. Uses the actual MainActivity to launch a JSON document picker (cancelled without writing) and a permission request, covering the legacy request-code crash missed by the screen-only tests.
- `lintDebug` and preview vital lint: **zero errors**. There are 26 warnings and three hints, mainly available dependency/SDK upgrades and deprecated APIs.
- `assemblePreview`: successful with R8 code optimization and optimized resource shrinking. APK package `com.harvestpay.app`, version 1.4.1, versionCode 13, minSdk 23 and targetSdk 36.
- Maps-enabled optimized APK v1/v2 signature verification passed. Installed as an update with `adb install -r`, preserving the signed-in session and ledger. Cold launch succeeded in 659 ms. Satellite imagery and GPS hybrid imagery/current-location marker rendered on the phone. Satellite boundary taps produced three points and a nonzero area; Undo removed the third point. All test drafts were discarded without saving a field. GPS entry and Start did not crash; poor-accuracy fixes were filtered. No AndroidRuntime or Maps authorization errors appeared for the current app process.
- On the optimized crash-fixed APK, Export complete backup and Import backup opened their Android document pickers without crashing. Both pickers were cancelled without writing or restoring a backup.
- Deliverable: `app/build/outputs/apk/preview/HarvestPay-v1.4.1-optimized.apk` (4,453,186 bytes, approximately 4.25 MiB), signed with the development certificate. SHA-256: `cff34c4d606d7b6e6ecc084c6a8e2956a01a71cb3712d8b11efb3c27efc62a72`.

Reports: `app/build/reports/tests/testDebugUnitTest/index.html`, `app/build/reports/androidTests/connected/debug/index.html`, and `app/build/reports/lint-results-debug.html`. R8 mapping: `app/build/outputs/mapping/preview/mapping.txt`.

**Remaining manual checks:** satellite marker dragging, real network retry, a real outdoor GPS boundary walk, phone-lock continuation over a complete walk, and permission-denial/approximate-only cases still need verification. The phone received low-accuracy GPS fixes during this check, which the app correctly filtered; no outdoor accuracy claim is made. Automated tests do not establish survey accuracy.

## What is included

- Full-screen tractor login artwork with fast salted SHA-256 password verification, automatic legacy credential migration, user-changeable password, show/hide password, Remember Me, auto-lock (including Never) and logout
- Dashboard totals for customers, fields, Bigha, earnings, collections, pending balances, advance credit, diesel litres and diesel spending
- Customer CRUD, optional opening/previous due, duplicate-mobile protection, search, sorting, call/WhatsApp actions and full profiles
- Multiple fields per customer with decimal Bigha sizes
- Work entries with managed English/Hindi work types, per-Bigha or per-minute billing, manual rate overrides, fixed 0.5/1/1.5/2 Round selection for area work, discounts, extras and live charge calculation
- Paid, partially paid, pending and advance states with customer-account ledgers; credit clears the oldest work first and carries forward automatically
- Pending-payment queue, unrestricted customer payments, advance-credit tracking and WhatsApp reminder drafts
- Local scheduled notifications using WorkManager
- Filtered reports by period, customer, village and payment state
- Individual work receipts plus chronological, multi-page customer account PDFs in English, Hindi or both, with an embedded Devanagari font
- Tractor service reminders based on running hours, weekly or monthly hour-update prompts and service history
- Admin-password verification before deleting a customer and clear SETTLED/UNPAID account status
- Diesel entry management with quantity, amount, date, Paid/Unpaid status and purchaser name
- Complete JSON backup/restore plus customer and payment CSV exports through Android's document picker
- Light, dark and system themes; owner/business and default-rate settings
- Near-instant navigation animations, eager background ledger loading, WAL database access and allocation-light calculations off the main UI thread
- No Firebase, custom server, cloud database or analytics SDK. Google Maps imagery and optional place search use internet; the ledger and GPS calculation remain local.

## Open and run

1. Install current Android Studio with JDK 21 and Android SDK Platform 36.
2. Open this `HarvestPay` folder in Android Studio.
3. Allow Gradle sync to finish.
4. Run the `app` configuration on an Android 6.0 (API 23) or newer device/emulator.

Command-line verification (set `JAVA_HOME` to a JDK 21 installation if Java is not on PATH; this computer uses `/home/tarun/.jdks/jbr-21.0.11`):

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug assemblePreview
```

The generated debug APK is at `app/build/outputs/apk/debug/app-debug.apk`. The installable, release-optimized preview APK is at `app/build/outputs/apk/preview/app-preview.apk`.

## Download and install the APK

1. Download `HarvestPay-v1.4.1-optimized.apk` for normal daily use. Use the debug APK only for development or troubleshooting.
2. On the Android phone, open the APK and allow installation from that file manager/browser if Android asks.
3. Tap **Install**, then open Harvest Pay.
4. Updates must use the same signing key as the installed app. If Android reports a signing conflict, obtain a matching signed build; do not uninstall an installation containing customer data.

Both APKs have the same features and local data model. The optimized APK is smaller and uses R8 code/resource optimization; the debug APK keeps debugging information and is easier to inspect in Android Studio.

| APK | Best for | Characteristics |
|---|---|---|
| Optimized | Normal phone use | Smaller, faster startup, R8 optimized, installable test signature |
| Debug | Development/testing | Larger, debuggable, more diagnostic information |

## Initial access

Use the mobile number and initial password from the supplied Harvest Pay product brief. The password is never rendered or stored in readable form. Fresh installs verify the initial password with fast salted SHA-256 immediately; existing PBKDF2 credentials created by earlier versions are accepted once and automatically migrated after successful login.

## Architecture

- `data/`: Room entities, DAO, database, repository and DataStore settings
- `domain/`: monetary calculations and joined dashboard/ledger models
- `security/`: local credential verification and Indian mobile normalization
- `notification/`: WorkManager-based local reminders
- `util/`: JSON/CSV backup, receipt PDF, formatting and external intents
- `ui/`: Material 3 Compose screens, components, theme and navigation

Room relationships preserve the requested model: one customer has many fields and work entries; a field has many work entries; and a work entry has many payments. Financial history is retained if a field is deleted. Deleting a customer cascades through that customer's complete ledger only after confirmation and successful admin-password verification.

## Modern Dashboard & UI/UX Improvements

- Refreshed the dashboard with compact rounded cards, clear typography, consistent spacing, Harvest Pay green accents and light/dark theme colors. Large font settings and narrow screens use a single column.
- **Total Customers remains first; Received Today is second.** The Work Types dashboard card is removed; service management remains under More. A Total Work entry count is included. Every statistic opens a searchable breakdown.
- Customer overview shows field counts, outstanding balances and Settled/Unpaid status, with links to existing customer profiles. Pending and advance views use the same current account balances as the dashboard.
- Field overview shows every saved field, the original physical area in Bigha and the number of owners. Field rows open the existing editor with both the customer and field IDs. Physical area is kept separate from work adjusted by Round.
- Work and earnings views show date-wise jobs, work types, customers and billed amounts. Bigha and minutes remain separate; the Bigha view includes only area work. Rows open the saved work entry.
- Payment views show current-account transactions, methods, dates and notes. Received Today filters the exact reporting day used by the dashboard. No payment transaction times are inferred from record creation timestamps.
- Outstanding details show existing opening dues as account context without adding them again. Archived transactions remain in customer archive history. Diesel breakdowns include litres, expenditure, date, person and Paid/Unpaid status, and open the selected saved entry.
- Added gentle dashboard entrance, card press feedback and value fades with native Compose animations, which follow Android's animation duration scale. Stable list keys and background detail filtering keep updates focused and scrolling lightweight.
- Added reusable animated success notifications for customer, field, work and payment saves. They appear only after successful persistence, survive navigation and dismiss after about two seconds (longer when accessibility settings request it). Failed saves show an error instead. Work saved with an initial payment produces one confirmation after the transaction completes.
- Added dashboard/detail loading states and a retryable account-loading error state. Room observation continues to drive updates after adds, edits and deletions. A local-midnight wake-up and foreground date check keep Received Today current without database polling.
- Improved Quick Add spacing and navigation cues while preserving all existing actions. Version 1.4.1 adds Measure Field alongside the existing actions.
- Kept database schemas, stored records, billing/round calculations, archive rules and app version unchanged. The existing compact Customers list and the preceding customer-profile/PDF changes are retained.

Earlier dashboard-only verification: source paths, card ordering, record identifiers and save/error ordering were reviewed. Kotlin syntax parsing covered 15 changed/new Kotlin files with zero syntax errors; this is not a compilation or runtime test. Added 11 focused tests in `DashboardRecordsTest`, `DashboardInsightDataTest` and `SuccessFeedbackTest` for archive filtering, mixed billing, correct record navigation and post-save notifications. The owner subsequently authorized builds and device tests; see v1.4.1 verification above for the completed results. Light/dark rendering, keyboard behavior, PDF rendering and animation performance still require the later device pass.

Files created (Kotlin paths are relative to `app/src/main/java/com/harvestpay/app/`):

- `domain/DashboardInsight.kt`, `domain/SuccessFeedback.kt`
- `ui/components/DashboardComponents.kt`, `ui/components/SuccessFeedbackHost.kt`
- `ui/screens/DashboardInsightData.kt`, `ui/screens/DashboardInsightScreen.kt`
- Tests under `app/src/test/java/com/harvestpay/app/`: `domain/DashboardRecordsTest.kt`, `domain/SuccessFeedbackTest.kt`, `ui/screens/DashboardInsightDataTest.kt`

Files modified for the dashboard update: `HarvestViewModel.kt`, `domain/BusinessModels.kt`, `domain/BusinessCalculator.kt` (reporting-day metadata only), `ui/screens/DashboardScreen.kt`, `ui/screens/DieselScreen.kt`, `ui/navigation/HarvestPayApp.kt`, and `README.md`.

## What's New in v1.3.1

- Added Yearly Customer Account Archive system.
- Added yearly account separation from 1 January to 31 December.
- Added safe previous-year archive workflow.
- Added outstanding balance carry-forward.
- Added advance balance carry-forward.
- Added Archived History in Customer Profile.
- Added year-wise historical customer account viewing.
- Added Current Account PDF.
- Added Outstanding Account PDF.
- Added Custom Date Range PDF.
- Added Full Current Year PDF.
- Added Archived Year PDF.
- Added Complete History PDF.
- Added report-type selection before PDF language selection.
- Archived outstanding balances now appear as previous-year opening balances instead of repeating old transaction history in normal outstanding PDFs.
- Preserved full archived transactions for detailed historical statements.
- Preserved date-wise chronological PDF history.
- Preserved English, Hindi and bilingual PDF support.
- Improved handling of long-running customer accounts.
- Database migration and archive stability improvements.

## What's New in v1.3.0

- Added **Per Bigha** and **Per Minute** billing modes to managed work types and work entries. Minute jobs store their duration and rate snapshot without changing historical area-based jobs.
- Added optional **Hindi work-type names**. Built-in work types migrate to कल्टीवेटर, रोटावेटर, जुताई and बुवाई automatically.
- Rebuilt customer account PDFs as one **date-wise chronological work table**, with English, Hindi and bilingual output choices.
- Embedded Noto Sans Devanagari so Hindi PDF text does not depend on fonts installed on the phone.
- Added a **tractor service reminder** page with current hours, service interval, last-service hours, weekly/monthly update prompts, due/overdue status and service history.
- Added **admin-password confirmation** before deleting a customer and their ledger.
- Added a clear **SETTLED / UNPAID** status to each customer account.
- Updated dashboard, reports, WhatsApp messages and receipts for mixed billing; time-based jobs contribute revenue but never inflate Bigha totals.
- Extended JSON backup/restore for billing mode, duration, Hindi names and service settings while retaining backward compatibility with older backups.
- Upgraded Room to schema 6 through a non-destructive 5→6 migration. Existing customers, fields, work, payments, diesel entries and settings are retained.
- Added regression coverage for mixed billing, service calculations, chronological PDF ordering, credential verification and database migration.
- Updated app version to **1.3.0** (version code 10).

## Data and security notes

- The ledger and GPS calculation work offline. Satellite imagery and place search need internet. WhatsApp, phone and sharesheet actions hand content to another installed app and never send automatically.
- JSON and CSV exports can contain personal and financial data and are intentionally not encrypted so they remain portable. Store exported files securely.
- This local login is an app access gate. For a higher-risk deployment, add device-backed key storage and SQLCipher database encryption before distributing outside the owner's device.
- Android's automatic cloud backup is disabled; use the explicit Export Backup action instead.

## Extending the app

The repository and shared ViewModel isolate persistence and calculations from UI. Add Room migrations whenever the schema version changes, keep money validation in the domain/ViewModel layer, and add regression tests for every new balance rule.

## Developer

Tarun Kumar
