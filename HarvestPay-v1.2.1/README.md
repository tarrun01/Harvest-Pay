# Harvest Pay

Harvest Pay is an offline-first Android ledger for a tractor owner who charges farmers by field size. It is built with Kotlin, Jetpack Compose, Material 3, Room, MVVM, Navigation Compose and DataStore.

## What is included

- Full-screen tractor login artwork with fast salted SHA-256 password verification, automatic legacy credential migration, user-changeable password, show/hide password, Remember Me, auto-lock (including Never) and logout
- Dashboard totals for customers, fields, work types, Bigha, earnings, collections, pending balances and advance credit
- Customer CRUD, duplicate-mobile protection, search, sorting, call/WhatsApp actions and full profiles
- Multiple fields per customer with decimal Bigha sizes
- Work entries with managed work types, automatic per-Bigha rates, manual rate overrides, decimal rounds, discounts, extras and live charge calculation
- Paid, partially paid, pending and advance states with customer-account ledgers; credit clears the oldest work first and carries forward automatically
- Pending-payment queue, unrestricted customer payments, advance-credit tracking and WhatsApp reminder drafts
- Local scheduled notifications using WorkManager
- Filtered reports by period, customer, village and payment state
- Receipt PDF creation, save/share actions and WhatsApp sharing
- Complete JSON backup/restore plus customer and payment CSV exports through Android's document picker
- Light, dark and system themes; owner/business and default-rate settings
- Near-instant navigation animations, eager background ledger loading, WAL database access and allocation-light calculations off the main UI thread
- No Firebase, server, cloud database, analytics SDK or Internet permission

## Open and run

1. Install current Android Studio with JDK 17 and Android SDK Platform 36.
2. Open this `HarvestPay` folder in Android Studio.
3. Allow Gradle sync to finish.
4. Run the `app` configuration on an Android 6.0 (API 23) or newer device/emulator.

Command-line verification:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug assemblePreview
```

The generated debug APK is at `app/build/outputs/apk/debug/app-debug.apk`. The installable, release-optimized preview APK is at `app/build/outputs/apk/preview/app-preview.apk`.

## Initial access

Use the mobile number and initial password from the supplied Harvest Pay product brief. The password is never rendered or stored in readable form. Fresh installs verify the initial password with fast salted SHA-256 immediately; existing PBKDF2 credentials created by earlier versions are accepted once and automatically migrated after successful login.

## Architecture

- `data/`: Room entities, DAO, database, repository and DataStore settings
- `domain/`: monetary calculations and joined dashboard/ledger models
- `security/`: local credential verification and Indian mobile normalization
- `notification/`: WorkManager-based local reminders
- `util/`: JSON/CSV backup, receipt PDF, formatting and external intents
- `ui/`: Material 3 Compose screens, components, theme and navigation

Room relationships preserve the requested model: one customer has many fields and work entries; a field has many work entries; and a work entry has many payments. Financial history is retained if a field is deleted. Deleting a customer cascades through that customer's complete ledger after confirmation.

## Data and security notes

- Core operation is completely offline. WhatsApp, phone and sharesheet actions hand content to another installed app and never send automatically.
- JSON and CSV exports can contain personal and financial data and are intentionally not encrypted so they remain portable. Store exported files securely.
- This local login is an app access gate. For a higher-risk deployment, add device-backed key storage and SQLCipher database encryption before distributing outside the owner's device.
- Android's automatic cloud backup is disabled; use the explicit Export Backup action instead.

## Extending the app

The repository and shared ViewModel isolate persistence and calculations from UI. Add Room migrations whenever the schema version changes, keep money validation in the domain/ViewModel layer, and add regression tests for every new balance rule.
