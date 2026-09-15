# Harvest Pay v1.2.2

Harvest Pay is an offline-first Android ledger for a tractor owner who charges farmers by field size. It is built with Kotlin, Jetpack Compose, Material 3, Room, MVVM, Navigation Compose and DataStore.

## What is included

- Full-screen tractor login artwork with fast salted SHA-256 password verification, automatic legacy credential migration, user-changeable password, show/hide password, Remember Me, auto-lock (including Never) and logout
- Dashboard totals for customers, fields, work types, Bigha, earnings, collections, pending balances, advance credit, diesel litres and diesel spending
- Customer CRUD, optional opening/previous due, duplicate-mobile protection, search, sorting, call/WhatsApp actions and full profiles
- Multiple fields per customer with decimal Bigha sizes
- Work entries with managed work types, automatic per-Bigha rates, manual rate overrides, fixed 0.5/1/1.5/2 Round selection, discounts, extras and live charge calculation
- Paid, partially paid, pending and advance states with customer-account ledgers; credit clears the oldest work first and carries forward automatically
- Pending-payment queue, unrestricted customer payments, advance-credit tracking and WhatsApp reminder drafts
- Local scheduled notifications using WorkManager
- Filtered reports by period, customer, village and payment state
- Individual work receipts plus consolidated, multi-page customer account PDFs with every field, work entry, payment and latest balance
- Diesel entry management with quantity, amount, date, Paid/Unpaid status and purchaser name
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

## Download and install the APK

1. Download `HarvestPay-v1.2.2-optimized.apk` for normal daily use. Use the debug APK only for development or troubleshooting.
2. On the Android phone, open the APK and allow installation from that file manager/browser if Android asks.
3. Tap **Install**, then open Harvest Pay.
4. If Android reports that the app conflicts with an existing installation, first export a Harvest Pay JSON backup, uninstall the old build, install v1.2.2, and restore the backup.

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

Room relationships preserve the requested model: one customer has many fields and work entries; a field has many work entries; and a work entry has many payments. Financial history is retained if a field is deleted. Deleting a customer cascades through that customer's complete ledger after confirmation.

## What’s new in v1.2.2

- Added **Share PDF** to the Customer profile for a complete, consolidated customer report.
- The customer PDF includes customer details, all fields and registered Bigha, field-wise work history and totals, total work, previous due, payments, outstanding balance, advance credit and notes.
- Fixed the Work history **Message** action so its generated PDF opens directly in WhatsApp/WhatsApp Business when available, with a safe Android share fallback.
- Added dashboard totals for **Total Diesel Used (Litres)** and **Total Diesel Amount Spent**.
- Added **Diesel Entry** to Quick Add with create, edit and delete support, today’s date by default, Paid/Unpaid status and “Diesel brought by”.
- Added a separate **Diesel Payments** tab in Payments with paid/unpaid totals and complete diesel purchase history.
- Renamed the first dashboard metric from **Customers served** to **Total Customers**.
- Made the customer mobile number optional and safely supports multiple customers without phone numbers.
- Call and WhatsApp actions are hidden when a customer has no mobile number.
- Added an internal-only customer nickname for distinguishing duplicate or similar names; nicknames never appear in PDFs, reports or shared customer output.
- Round is now a keyboard-free selector limited to **0.5, 1, 1.5 and 2**.
- Work-done totals and price use **Field Bigha × Round**, while every individual entry preserves its original field size.
- Statements, individual work PDFs and consolidated customer PDFs show **Field Size** and **Round** separately; calculated Work Done is shown without replacing the original field size.
- Added duplicate diesel-entry protection so totals are not counted twice.
- Added optional **Previous Due (₹)** to New Customer and Edit Customer.
- Opening dues now participate in the customer ledger, total due, payment allocation, advance calculation, WhatsApp summary, CSV/JSON backup and customer PDF.
- Added the farmer message and developer credit to About.
- Upgraded the Room database to schema 5 with safe migrations for diesel, previous-due, optional customer-mobile and nickname support.
- Updated app version to **1.2.2** (version code 9).

## Data and security notes

- Core operation is completely offline. WhatsApp, phone and sharesheet actions hand content to another installed app and never send automatically.
- JSON and CSV exports can contain personal and financial data and are intentionally not encrypted so they remain portable. Store exported files securely.
- This local login is an app access gate. For a higher-risk deployment, add device-backed key storage and SQLCipher database encryption before distributing outside the owner's device.
- Android's automatic cloud backup is disabled; use the explicit Export Backup action instead.

## Extending the app

The repository and shared ViewModel isolate persistence and calculations from UI. Add Room migrations whenever the schema version changes, keep money validation in the domain/ViewModel layer, and add regression tests for every new balance rule.

## Developer

Tarun
