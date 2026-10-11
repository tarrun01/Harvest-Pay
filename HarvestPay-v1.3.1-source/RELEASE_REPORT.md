# Harvest Pay v1.4.2 delivery report

## Implementation

Native English/Hindi UI is implemented with 771 paired string resources, native boundary-point plurals and localized offline history arrays. More → Language / भाषा uses AppCompat/native per-app locale APIs. The locale changes immediately and persists; Android 13+ shares the framework preference. English is the default if no prior app preference exists. User-entered data and stored identifiers stay unchanged.

More → About → What's New shows v1.4.2, v1.4.1, v1.4.0 and documented v1.2.2 in descending order. One release expands at a time. The current-version badge reads installed package metadata. v1.4.0's missing notes are disclosed; v1.2.2 notes are sourced from the earlier repository README. No release dates are invented.

GPS/satellite, review, completion and saved-boundary measured area displays use two decimals with HALF_UP. Add Field converts original square meters directly to Bigha and rounds its editable input only once. Full-precision area/factor/boundary metadata remains unchanged. A rounded zero-sized measurement is warned about and rejected by existing save validation. PDF language selection and financial rounding remain independent.

## APK and diagnostics

| Property | Actual result |
| --- | --- |
| Filename | `HarvestPay-v1.4.2-optimized.apk` |
| Absolute path | `/home/tarun/Code-PlayGround/Harvest-Pay/HarvestPay-v1.3.1-source/app/build/outputs/apk/preview/HarvestPay-v1.4.2-optimized.apk` |
| Size | 4,881,459 bytes / approximately 4.66 MiB |
| Package | `com.harvestpay.app` |
| Version | 1.4.2 / versionCode 14 |
| Variant | `preview`, inheriting release optimization |
| SDK | min 23 / target 36 / compile 36 |
| R8 | Code shrinking, optimization, obfuscation and optimized resource shrinking enabled |
| Signing | Existing Android development certificate; expressly authorized by owner; no production keystore supplied |
| Signature verification | APK v1 and v2 verified |
| Certificate SHA-256 | `611b7528e2e558021aeebf2a8cb82477f239fcb53c8ebe05e743acafe9c3d95d` |
| APK SHA-256 | `9c037964d91c3584aadc4d54efa38de2b178d07ae7d5a7bac7690ec70c5e208a` |

Mapping preserved at `app/build/outputs/release-diagnostics/v1.4.2/mapping.txt`; exact artifact metadata and checksum are alongside it. Generated APKs/mapping/build outputs are excluded from the source commit.

## Verification actually completed

- Final `testDebugUnitTest` focused suite: **12 passed**, zero failures/errors: MeasurementFormattingTest (4), LocalizationResourcesTest (2), DashboardInsightDataTest (4), DashboardGreetingTest (1), ShareUtilsTest (1).
- Covers HALF_UP ties, two decimal formatting, full-precision conversion, very small positive measurement handling, resource parity/placeholders, critical Hindi labels and release arrays, preserved dashboard summaries and payment-share content.
- Final `lintDebug`: **zero errors**, 35 warnings and 3 hints. Remaining findings concern available dependency/plugin upgrades, deprecated/style APIs and unused resources/attributes; they are not presented as zero warnings.
- `assemblePreview` and preview vital lint succeeded. The final work-type correction build completed in 3 minutes 14 seconds; R8 minification/resource shrinking tasks executed.
- Samsung **SM-A346E / Android 16**, wireless identifier `adb-RZCWA2HEL3T-zWLkus._adb-tls-connect._tcp`: `adb install -r` succeeded without uninstalling or clearing data. Signature compatibility was enforced by Android's update installation.
- Cold launch succeeded (696 ms reported by Activity Manager). Native Hindi selection succeeded; framework app locales reported `[hi]`. Hindi persisted after force-stop/cold restart (300 ms launch).
- Dashboard customer/work counts and monetary totals matched the pre-update snapshot. User-entered owner/customer values were retained. The Hindi dashboard was visually inspected; text wrapped without observed overlap.

## Additional device checks completed

- The final corrected APK installed successfully as an update and cold-launched in 292 ms. Hindi remained selected across the update.
- Hindi satellite action buttons were visually checked on the final APK; long labels and Search fit without narrow clipping.
- New Work showed the owner-provided Hindi names in both its selection and dropdown: हैरो, धान सफाई मशीन, रोटावेटर, पानी में रोटावेटर and टिलर. The existing rate remained unchanged. Android's system app-locale switch to English immediately showed Harrow on the same form. Canonical work-type identifiers and billing calculations are unchanged.
- Satellite imagery loaded, a temporary three-point boundary produced an area, and review displayed Acres/Bigha with two decimals. Add Field displayed an editable Bigha value calculated from raw square meters, confirming that rounded Acres were not used for conversion. No field was saved.
- GPS entry and Start succeeded, a 2.50-meter-accuracy fix was accepted, and the recording draft was cancelled and discarded. No saved field was created.
- Hindi history order/current badge, one-section expansion, collapse and the missing v1.4.0 notes were checked. Hindi customer, profile, payments and settings screens were inspected.
- Profile PDF report selection excluded the duplicate Outstanding option; English/Hindi/Both output choices remained available while the UI was Hindi. The chooser was cancelled; PDF pages were not newly rendered for visual QA in this phase.
- Export opened Android's Create Document picker and Import opened its Open Document picker without a crash. Both were cancelled without exporting or restoring ledger data.
- The final app process had no AndroidRuntime or Maps authorization errors in the inspected log. The original English preference was restored after the language checks.
- Customer/work/field counts and current-account totals remained unchanged through the checks. User-entered names and settings were preserved. Prior v1.4.1 device/crash verification remains separately recorded in README.

## Practical limitations

- This optimized APK is development-signed, not production-signed.
- Actual outdoor GPS walking accuracy has not been verified. The geodesic calculation and rounding are covered by focused tests; an indoor GPS fix and Start/cancellation were verified on this release.
- Android 6–12 AppCompat locale persistence has not been physically tested on an older device.
- Offline history is implemented solely from packaged arrays; the phone's network was not disabled because wireless ADB needs it.
- No full device test run was used on the populated owner phone because test tooling can uninstall the real app. No customer, field, work entry or payment was saved for these UI checks.

## Git and publication

The local branch is `main`. The source directory was previously entirely untracked; this release commit brings the complete current source into Git, including earlier requested functionality. The repository root README now points to the current project and detailed documentation. Existing deletions under the older v1.2.2 directory and the unrelated parent APK are preserved and excluded from staging. The working tree therefore retains those pre-existing changes.

The owner separately authorized pushing source and publishing the Maps-enabled APK, including its embedded Android Maps key. Source commit `ba662074c0e7198be2733acb06065e6398ef18ff` was successfully pushed to `main` in `tarrun01/Harvest-Pay`. Automatic approval review nevertheless rejected the APK upload because the embedded key is publicly extractable; no APK was uploaded. A draft v1.4.2 release was prepared against that source commit, with release notes and no assets: https://github.com/tarrun01/Harvest-Pay/releases/tag/untagged-111b9400f32e028cbf7c. Publishing the APK remains blocked pending evidence that would establish a lower-risk upload. Existing unrelated changes remain unstaged.

## Relevant new/updated v1.4.2 files

Created: `localization/AppText.kt`, `localization/StableLabels.kt`, `measurement/MeasurementFormatting.kt`, `ui/screens/LanguageHistoryScreens.kt`, `res/values-hi/strings.xml`, `res/xml/locales_config.xml`, localization JVM fixtures/tests, measurement-format tests, PROJECT_GUIDE.md, CHANGELOG.md and this report.

Updated: app build/version/dependency configuration, manifest/theme, MainActivity, HarvestPayApplication, HarvestViewModel, measurement screen/map/store/service presentation, ReminderWorker, navigation, UI screens/components, ShareUtils, PDF share chooser only, UI date formatters, README and measurement workflow test expectations. PDF templates, geometry calculations, financial calculations and historical persistence semantics remain unchanged for v1.4.2.

### Complete source/documentation commit inventory

The following files are newly tracked in the current source directory or updated at repository root. The inventory includes existing source necessary to build the project, not just v1.4.2 edits:

- `HarvestPay-v1.3.1-source/.gitignore`
- `HarvestPay-v1.3.1-source/CHANGELOG.md`
- `HarvestPay-v1.3.1-source/PROJECT_GUIDE.md`
- `HarvestPay-v1.3.1-source/README.md`
- `HarvestPay-v1.3.1-source/app/build.gradle.kts`
- `HarvestPay-v1.3.1-source/app/proguard-rules.pro`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/1.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/2.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/3.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/4.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/5.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/6.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/7.json`
- `HarvestPay-v1.3.1-source/app/schemas/com.harvestpay.app.data.AppDatabase/8.json`
- `HarvestPay-v1.3.1-source/app/src/androidTest/java/com/harvestpay/app/MainActivityResultIntegrationTest.kt`
- `HarvestPay-v1.3.1-source/app/src/androidTest/java/com/harvestpay/app/data/AppDatabaseMigrationTest.kt`
- `HarvestPay-v1.3.1-source/app/src/androidTest/java/com/harvestpay/app/measurement/GpsServiceInstrumentedTest.kt`
- `HarvestPay-v1.3.1-source/app/src/androidTest/java/com/harvestpay/app/measurement/MeasurementRecoveryTest.kt`
- `HarvestPay-v1.3.1-source/app/src/androidTest/java/com/harvestpay/app/measurement/MeasurementWorkflowTest.kt`
- `HarvestPay-v1.3.1-source/app/src/androidTest/java/com/harvestpay/app/security/LocalAuthInstrumentedTest.kt`
- `HarvestPay-v1.3.1-source/app/src/main/AndroidManifest.xml`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/HarvestPayApplication.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/HarvestViewModel.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/MainActivity.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/data/AppDatabase.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/data/Entities.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/data/HarvestDao.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/data/HarvestRepository.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/data/SettingsRepository.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/AccountArchiveCalculator.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/BusinessCalculator.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/BusinessModels.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/CustomerReport.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/DashboardInsight.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/ServiceCalculator.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/domain/SuccessFeedback.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/localization/AppText.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/localization/StableLabels.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/measurement/BoundaryMap.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/measurement/FieldGeometry.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/measurement/GpsMeasurementService.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/measurement/MeasurementFormatting.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/measurement/MeasurementScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/measurement/MeasurementStore.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/notification/ReminderWorker.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/security/LocalAuth.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/components/CommonComponents.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/components/DashboardComponents.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/components/SuccessFeedbackHost.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/navigation/HarvestPayApp.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/ArchiveScreens.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/ChangePasswordScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/CustomerScreens.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/DashboardInsightData.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/DashboardInsightScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/DashboardScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/DieselScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/GlobalSearchScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/LanguageHistoryScreens.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/LoginScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/MoreScreens.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/PaymentsScreens.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/ReportsScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/ServiceReminderScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/WorkScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/screens/WorkTypeManagementScreen.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/ui/theme/Theme.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/util/BackupCodec.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/util/Formatters.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/util/PdfLogo.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/util/ReceiptGenerator.kt`
- `HarvestPay-v1.3.1-source/app/src/main/java/com/harvestpay/app/util/ShareUtils.kt`
- `HarvestPay-v1.3.1-source/app/src/main/res/drawable-nodpi/ic_launcher_foreground.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/drawable-nodpi/login_background.jpg`
- `HarvestPay-v1.3.1-source/app/src/main/res/drawable/ic_launcher_monochrome.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/drawable/ic_notification.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/font/noto_sans_devanagari_regular.ttf`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-hdpi/ic_launcher.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-hdpi/ic_launcher_round.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-mdpi/ic_launcher.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-mdpi/ic_launcher_round.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-xhdpi/ic_launcher.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-xhdpi/ic_launcher_round.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-xxhdpi/ic_launcher.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png`
- `HarvestPay-v1.3.1-source/app/src/main/res/values-hi/strings.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/values/ic_launcher_background.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/values/strings.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/values/themes.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/xml/data_extraction_rules.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/xml/file_paths.xml`
- `HarvestPay-v1.3.1-source/app/src/main/res/xml/locales_config.xml`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/domain/AccountArchiveCalculatorTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/domain/BusinessCalculatorTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/domain/CustomerReportFactoryTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/domain/DashboardRecordsTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/domain/ServiceCalculatorTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/domain/SuccessFeedbackTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/localization/EnglishResources.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/localization/LocalizationResourcesTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/measurement/FieldGeometryTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/measurement/MeasurementFormattingTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/security/LocalAuthTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/ui/screens/DashboardGreetingTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/ui/screens/DashboardInsightDataTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/util/ReceiptGeneratorTest.kt`
- `HarvestPay-v1.3.1-source/app/src/test/java/com/harvestpay/app/util/ShareUtilsTest.kt`
- `HarvestPay-v1.3.1-source/build.gradle.kts`
- `HarvestPay-v1.3.1-source/gradle.properties`
- `HarvestPay-v1.3.1-source/gradle/gradle-daemon-jvm.properties`
- `HarvestPay-v1.3.1-source/gradle/wrapper/gradle-wrapper.jar`
- `HarvestPay-v1.3.1-source/gradle/wrapper/gradle-wrapper.properties`
- `HarvestPay-v1.3.1-source/gradlew`
- `HarvestPay-v1.3.1-source/gradlew.bat`
- `HarvestPay-v1.3.1-source/settings.gradle.kts`
- `README.md`
- `HarvestPay-v1.3.1-source/RELEASE_REPORT.md`
