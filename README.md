# Harvest Pay V 1.0.0

Harvest Pay is an offline-first Android ledger for a tractor owner who charges farmers by field size. It is built with Kotlin, Jetpack Compose, Material 3, Room, MVVM, Navigation Compose and DataStore.

## What is included

- Local owner login with PBKDF2 password verification, show/hide password, Remember Me, auto-lock and logout
- Dashboard totals for customers, fields, Bigha, earnings, collections and pending balances
- Customer CRUD, duplicate-mobile protection, search, sorting, call/WhatsApp actions and full profiles
- Multiple fields per customer with decimal Bigha sizes
- Ploughing entries with rate presets, rounds, discount, extras and live charge calculation
- Paid, partially paid and pending states with work-level payment allocation and customer ledgers
- Pending-payment queue, full/partial payment actions and WhatsApp reminder drafts
- Local scheduled notifications using WorkManager
- Filtered reports by period, customer, village and payment state
- Receipt PDF creation, save/share actions and WhatsApp sharing
- Complete JSON backup/restore plus customer and payment CSV exports through Android's document picker
- Light, dark and system themes; owner/business and default-rate settings
- No Firebase, server, cloud database, analytics SDK or Internet permission

## Open and run

1. Install current Android Studio with JDK 17 and Android SDK Platform 36.
2. Open this `HarvestPay` folder in Android Studio.
3. Allow Gradle sync to finish.
4. Run the `app` configuration on an Android 6.0 (API 23) or newer device/emulator.

Command-line verification:

```bash
./gradlew testDebugUnitTest assembleDebug
```

The generated debug APK is at `apk/HarvestPay-debug.apk`.

## Initial access

Use the mobile number and initial password from the supplied Harvest Pay product brief. The password is never rendered by the app and its readable value is not stored in source or preferences; login compares a PBKDF2-HMAC-SHA256 result in constant time.

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
