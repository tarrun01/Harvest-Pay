package com.harvestpay.app.measurement

import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.UUID

class MeasurementRecoveryTest {
    @Test fun restoresInterruptedTrackAsPausedAndRetainsAcceptedAssignment(): Unit = runBlocking {
        val base=InstrumentationRegistry.getInstrumentation().targetContext
        val directory=File(base.cacheDir,"measurement-test-${UUID.randomUUID()}").apply { mkdirs() }
        val context=object: ContextWrapper(base) { override fun getFilesDir()=directory }
        val points=listOf(GeoPoint(25.0,80.0),GeoPoint(25.0,80.001),GeoPoint(25.001,80.001))
        val store=MeasurementStore(context)
        store.set(MeasurementDraft(MeasurementMethod.GPS,points,points,recording=true))
        val disk=File(directory,"field_measurement_draft.json")
        withTimeout(5000) { while(!disk.exists() || !disk.readText().contains("\"recording\":true")) delay(20) }
        val recovered=MeasurementStore(context).draft.value!!
        assertFalse(recovered.recording); assertTrue(recovered.interrupted)
        assertEquals(points,recovered.points); assertEquals(points,recovered.originalTrack)
        store.set(recovered.copy(stage=MeasurementStage.ACCEPTED,factorUsed=1.6,assignedCustomerId=42,measuredAt=123))
        withTimeout(5000) { while(!disk.readText().contains("\"customerId\":42")) delay(20) }
        val accepted=MeasurementStore(context).draft.value!!
        assertEquals(MeasurementStage.ACCEPTED,accepted.stage)
        assertEquals(42L,accepted.assignedCustomerId)
        assertEquals(1.6,accepted.factorUsed!!,0.0)
        directory.deleteRecursively()
        Unit
    }
}
