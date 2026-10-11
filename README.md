# Harvest Pay v1.4.2

Offline Android customer and agricultural-service ledger built with Kotlin, Jetpack Compose, Room and DataStore. Manage customers, fields, Bigha/minute work, payments, account history, PDFs, diesel and service reminders.

## Latest changes

- Native English/Hindi UI with instant, persistent language selection.
- Offline expandable version history in More → About.
- GPS and satellite field measurement with two-decimal display and full-precision internal area.
- Editable two-decimal Bigha prefill using the owner's Acre-to-Bigha factor.
- Compact customer/profile flows, simplified work entry, corrected document pickers and Maps integration.
- R8 optimization, obfuscation and resource shrinking.

## Documentation and source

The Android Studio project is in **[HarvestPay-v1.3.1-source](HarvestPay-v1.3.1-source/)**. The directory name is retained for workspace compatibility; the app's current version is **1.4.2**, versionCode **14**, package `com.harvestpay.app`.

- [Project README and build instructions](HarvestPay-v1.3.1-source/README.md)
- [Detailed project guide](HarvestPay-v1.3.1-source/PROJECT_GUIDE.md)
- [Changelog](HarvestPay-v1.3.1-source/CHANGELOG.md)
- [Verification and delivery report](HarvestPay-v1.3.1-source/RELEASE_REPORT.md)

## Download

Get the latest optimized APK from [GitHub Releases](https://github.com/tarrun01/Harvest-Pay/releases). Android 6.0/API 23 or newer is required. v1.4.2 uses the existing development certificate explicitly authorized by the owner; no production keystore was supplied.

Install compatible updates over the existing app. Preserve the ledger and stop on any signing mismatch; do not uninstall or clear app data to bypass it. Exported backups contain customer/account data, so keep them private.

## Build

Open the project directory in Android Studio, configure SDK platform 36/build-tools 36.0.0 and a compatible Java 21 Gradle runtime, then use its Gradle wrapper:

```bash
./gradlew :app:assemblePreview
```

`preview` inherits release R8 settings and the existing development signing certificate. Google Maps configuration is kept in ignored local settings; see the project README. R8 is bundled with AGP and does not need a separate installation.

Developed by Tarun.
