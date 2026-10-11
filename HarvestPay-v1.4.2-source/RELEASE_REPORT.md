# Harvest Pay v1.4.3 delivery report

## Requested changes

- Payments opens History first; Pending is the second tab.
- Pending customer cards use the standard neutral card surface and a compact layout showing only customer display name and pending amount.
- The card opens customer Details; action buttons separately invoke Mark paid, Add partial payment and Schedule reminder. Full-payment confirmation is retained.
- About → What's New includes packaged English/Hindi v1.4.3 descriptions and the installed-version badge.
- Version 1.4.3 / code 15. No database schema, calculation or payment persistence changes.

## Build and verification

| Property | Result |
| --- | --- |
| APK | `app/build/outputs/apk/preview/HarvestPay-v1.4.3-optimized.apk` |
| Size | 4,882,854 bytes (approximately 4.66 MiB) |
| SHA-256 | `6dcbbf0ac1599657c5a566559c7c2355d64e5e42832c361a06ed9ee14a8a5a6c` |
| Package/version | `com.harvestpay.app` / 1.4.3 / code 15 |
| Variant | `preview`, inheriting release R8 optimization, obfuscation and resource shrinking |
| Android support | min SDK 23 / target and compile SDK 36 |
| Signing | Existing owner-authorized development certificate; APK v1/v2 verified |
| Certificate SHA-256 | `611b7528e2e558021aeebf2a8cb82477f239fcb53c8ebe05e743acafe9c3d95d` |
| Device | Samsung A34 SM-A346E / Android 16 |

- `testDebugUnitTest --tests '*.LocalizationResourcesTest'` passed: 2 tests, zero failures/errors. This includes paired language resources and the new release-note arrays.
- `assemblePreview` and optimized-variant vital lint passed. R8 minification and optimized resource-shrinking tasks ran. Build time: 4m 32s.
- `adb install -r` succeeded with the compatible certificate. No uninstall or data clearing. Cold launch: 634 ms reported by Activity Manager.
- Tapping bottom Payments from Dashboard opened History first with existing payment records. Pending is the second tab.
- Pending screen XML showed the compact name/amount header and separate Mark paid, Add partial payment and Schedule reminder buttons for both pending customers.

Mapping, signature output, metadata and checksum are preserved in ignored `app/build/outputs/release-diagnostics/v1.4.3/`.

## Scope and source

Only Payments presentation, app version/history resources and documentation changed. Existing dashboard cards, billing calculations, database schema/migrations and payment/reminder persistence are unchanged. `Mark paid` retains the original confirmation message and invokes the same ViewModel action only after confirmation.

The source directory name remains `HarvestPay-v1.4.2-source` in the existing repository layout; the Android app version is 1.4.3. Source publication to `main` is authorized. No APK upload to GitHub Releases is part of this update request.

## Completed action and language checks

- Neutral compact cards were visually inspected in English and Hindi. Both pending customers fit on the screen; long partial-payment labels wrap without clipping.
- Mark paid opened the correct balance confirmation; it was cancelled without recording payment.
- Add partial payment opened the existing form with the correct customer and pending balance; it was closed without saving.
- Schedule reminder opened the correct customer’s existing reminder form; it was cancelled without scheduling.
- Tapping the customer card’s name opened that customer’s Details. Nested action buttons invoked their own actions rather than the card’s Details action.
- More → About showed Version 1.4.3. Expanded notes and Current Version badge were verified in English and Hindi; older history entries remained ordered below.
- Dashboard customer/work/field counts and current-account totals matched the pre-update snapshot after installation. No payment or reminder was created during testing.
- The inspected current app process had no AndroidRuntime crash or authorization errors. The original English locale was restored after Hindi checks.

## Publication and limitations

Implementation commit `b818de5fb0e97e9988f72ad058c8ff945f3874b2` was pushed to GitHub main. This follow-up report records the completed manual checks.

The APK remains development-signed as authorized by the owner. No automated device suite that may uninstall the populated app was run. Actual payment recording and reminder scheduling were not performed against real customer records; existing callback/confirmation/persistence code is retained.

Prior v1.4.2 verification is preserved in [RELEASE_REPORT_1.4.2.md](RELEASE_REPORT_1.4.2.md).
