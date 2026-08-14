# Harvest Pay

**Current version:** 1.2.1  
**Platform:** Android 6.0 and newer  
**Operation:** Offline-first; no server account required

## About the app

Harvest Pay is an offline Android business and payment ledger designed for tractor owners who provide agricultural services to farmers. It keeps customer, field, work-order and payment records together and calculates charges using the field size in Bigha.

The app can be used to:

- Add and manage customers and their fields.
- Record decimal field sizes such as 0.5 Bigha.
- Create and manage work types such as Cultivating and Rotavating.
- Set an automatic rate per Bigha for each work type or enter a custom rate.
- Record work orders, discounts, extra charges and payments.
- Track paid, partially paid, pending and advance balances.
- Automatically apply a customer's advance credit to later work.
- View dashboard statistics, reports and complete customer histories.
- Create payment reminders and share payment details through WhatsApp.
- Export and restore local backups.
- Protect the app with a local password and configurable auto-lock.

All core business records remain on the device. WhatsApp, calling and file-sharing features open the corresponding installed Android application only when requested.

## Technology stack

- **Language:** Kotlin
- **UI:** Jetpack Compose and Material 3
- **Architecture:** MVVM with a shared Android ViewModel
- **Database:** Room SQLite with Write-Ahead Logging (WAL)
- **Settings:** Jetpack DataStore Preferences
- **Navigation:** Navigation Compose
- **Background work:** WorkManager
- **Asynchronous processing:** Kotlin Coroutines and Flow
- **Authentication:** Local salted SHA-256 password verification, with legacy PBKDF2 migration
- **Build system:** Gradle Kotlin DSL
- **Code optimization:** R8 code shrinking and resource shrinking in the optimized build
- **Minimum Android version:** Android 6.0 (API 23)
- **Target Android SDK:** API 36

## Download and install the APK

Two APK variants are provided with the release:

- `HarvestPay-v1.2.1-optimized.apk` — recommended for normal daily use.
- `HarvestPay-v1.2.1-debug.apk` — intended for development and troubleshooting.

Installation steps:

1. Download the recommended optimized APK to the Android device.
2. If Android asks for permission, allow the browser or file manager to install apps from that source.
3. Open the downloaded APK and select **Install**.
4. Launch **Harvest Pay** after installation.

If Android reports that the app conflicts with an existing installation, the APK was signed with a different development key. Export a fresh backup from the existing app first, uninstall it, install v1.2.1, and then restore the backup. Uninstalling without a backup permanently removes the app's local records.

## Optimized APK vs debug APK

| Area | Optimized APK | Debug APK |
| --- | --- | --- |
| Recommended use | Everyday use | Development and troubleshooting |
| Approximate size | 3 MB | 22 MB |
| R8 optimization | Enabled | Disabled |
| Unused code and resources | Removed | Retained |
| Code obfuscation | Enabled | Disabled |
| Android Studio debugging | Limited | Supported |
| Performance | Smaller and generally faster | Slightly slower and larger |
| App features and records | Same | Same |

Both APKs contain the same Harvest Pay v1.2.1 functionality and use the same database rules. The optimized APK is the best choice unless a problem needs to be investigated through Android Studio.

## What's new in v1.2.1

- Added fast salted SHA-256 login verification from the first login.
- Added automatic migration for passwords created with the older PBKDF2 method.
- Enabled Room WAL mode for faster database reads and writes.
- Moved ledger calculations away from the main UI thread.
- Kept essential dashboard, settings and reminder data ready in memory.
- Reduced repeated allocations in money, date and number formatting.
- Optimized dashboard customer lookups and pending-payment sorting.
- Added shorter, smoother login and navigation animations.
- Added the new Harvest Pay tractor application icon.
- Improved dark mode with deeper backgrounds, clearer surfaces and better contrast.
- Replaced the dashboard “Namaste” text with a time-based Good Morning, Good Afternoon or Good Evening greeting.
- Added the owner's name from Settings to the dashboard greeting.
- Updated the dashboard heading to “Today’s Business Insight.”
- Removed unwanted blank spaces from WhatsApp payment messages.
- Made the login form 40% transparent so the background remains visible.
- Removed the “All records stay on this device” footer from the login form.
- Retained advance-payment support, managed work types, decimal Bigha values, the Never auto-lock option and password changing.

## Source code

The complete Android Studio project is distributed as `HarvestPay-v1.2.1-source.zip`.

To open it:

1. Extract the ZIP file.
2. Open the extracted `HarvestPay-v1.2.1` folder in Android Studio.
3. Allow Gradle synchronization to complete.
4. Install Android SDK Platform 36 and use JDK 17 if Android Studio requests them.
5. Run the `app` configuration on an Android device or emulator.

## Verification

Harvest Pay v1.2.1 was checked with:

- 15 passing unit and regression tests.
- Android lint with 0 errors.
- Successful debug and release-optimized APK builds.
- APK package, version and signature verification.

## About the Developer

**Tarun**

