package com.harvestpay.app

import androidx.activity.result.contract.ActivityResultContracts
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith

/** Exercise the real FragmentActivity host, rather than a test ComponentActivity. */
@RunWith(AndroidJUnit4::class)
class MainActivityResultIntegrationTest {
    @Test
    fun documentPickerAcceptsRegistryRequestCodes() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.activityResultRegistry.register(
                    "document-request-regression",
                    ActivityResultContracts.CreateDocument("application/json"),
                ) { }.launch("harvestpay-regression.json")
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            // Cancel the picker; do not write a backup or alter any records.
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
        }
    }

    @Test
    fun permissionRequestAcceptsRegistryRequestCodes() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.activityResultRegistry.register(
                    "permission-request-regression",
                    ActivityResultContracts.RequestMultiplePermissions(),
                ) { }.launch(arrayOf(android.Manifest.permission.CAMERA))
                // CAMERA is deliberately undeclared: Android denies it without a
                // prompt, but it still exercises the host's permission request path.
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        }
    }
}
