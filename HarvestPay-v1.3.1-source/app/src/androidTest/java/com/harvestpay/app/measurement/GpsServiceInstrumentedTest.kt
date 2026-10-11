package com.harvestpay.app.measurement

import android.Manifest
import android.os.SystemClock
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.harvestpay.app.HarvestPayApplication
import com.harvestpay.app.data.*
import com.harvestpay.app.ui.theme.HarvestPayTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class GpsServiceInstrumentedTest {
    @get:Rule val permission=GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
    @get:Rule val compose=createComposeRule()
    @Test fun precisePermissionAllowsForegroundTrackingPauseAndReview() {
        val app=InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as HarvestPayApplication
        val store=app.measurementStore; val original=store.draft.value; val originalFix=store.fix.value
        try {
            store.begin(MeasurementMethod.GPS)
            // Inject an accurate initialization fix to exercise service lifecycle
            // without depending on an outdoor GNSS lock or claiming a real walk.
            store.fix.value=GpsSample(GeoPoint(25.0,80.0),3.0,SystemClock.elapsedRealtime())
            compose.setContent { HarvestPayTheme(ThemePreference.LIGHT) { MeasurementScreen(AppSettings(),{}, {}) } }
            compose.onNodeWithText("Start Measurement").performClick()
            compose.waitUntil(5000) { store.serviceRunning && store.draft.value?.recording==true }
            compose.onNodeWithText("Pause tracking").performClick()
            compose.waitUntil(5000) { !store.serviceRunning && store.draft.value?.recording==false }
            compose.runOnIdle { store.update { it.copy(points=listOf(GeoPoint(25.0,80.0),GeoPoint(25.0,80.001),GeoPoint(25.001,80.001))) } }
            compose.onNodeWithText("Mark Complete").performClick()
            compose.onNodeWithText("Review Field Boundary").assertIsDisplayed()
            assertEquals(MeasurementStage.REVIEW,store.draft.value?.stage)
            assertFalse(store.serviceRunning)
            InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
            compose.onNodeWithText("Discard measurement?").assertIsDisplayed()
            compose.onNodeWithText("Keep measuring").performClick()
            assertEquals(MeasurementStage.REVIEW,store.draft.value?.stage)
        } finally {
            app.stopService(android.content.Intent(app,GpsMeasurementService::class.java))
            compose.runOnIdle { store.set(original); store.fix.value=originalFix }
        }
    }
}
