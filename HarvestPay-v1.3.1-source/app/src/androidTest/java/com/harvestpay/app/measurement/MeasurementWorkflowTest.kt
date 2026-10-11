package com.harvestpay.app.measurement

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.harvestpay.app.HarvestPayApplication
import com.harvestpay.app.data.*
import com.harvestpay.app.ui.screens.FieldFormScreen
import com.harvestpay.app.ui.theme.HarvestPayTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MeasurementWorkflowTest {
    @get:Rule val compose = createComposeRule()
    private val points=listOf(GeoPoint(0.0,0.0),GeoPoint(0.0,.001),GeoPoint(.001,.001),GeoPoint(.001,0.0))
    private val app get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as HarvestPayApplication

    @Test fun reviewFinishAcceptsBoundaryWithoutSavingAField() {
        val store=app.measurementStore; val original=store.draft.value
        try {
            store.set(MeasurementDraft(method=MeasurementMethod.SATELLITE,points=points,stage=MeasurementStage.REVIEW))
            compose.setContent { HarvestPayTheme(ThemePreference.LIGHT) { MeasurementScreen(AppSettings(),{}, {}) } }
            compose.onNodeWithText("Finish").performClick()
            compose.onNodeWithText("Field Measurement Complete").assertIsDisplayed()
            assertEquals(MeasurementStage.ACCEPTED,store.draft.value?.stage)
            assertNull(store.draft.value?.assignedCustomerId)
            compose.onNodeWithText("Assign to Customer").performClick()
            compose.onNodeWithText("Acre to Bigha conversion").assertIsDisplayed()
        } finally { compose.runOnIdle { store.set(original) } }
    }

    @Test fun invalidBoundaryCannotFinishAndUndoRestoresPreviousPolygon() {
        val store=app.measurementStore; val original=store.draft.value
        try {
            store.set(MeasurementDraft(method=MeasurementMethod.SATELLITE,points=points,stage=MeasurementStage.REVIEW))
            store.edit(points.take(2))
            compose.setContent { HarvestPayTheme(ThemePreference.DARK) { MeasurementScreen(AppSettings(),{}, {}) } }
            compose.onNodeWithText("Finish").assertIsNotEnabled()
            compose.onNodeWithText("Undo edit").performClick()
            compose.onNodeWithText("Finish").assertIsEnabled()
        } finally { compose.runOnIdle { store.set(original) } }
    }

    @Test fun addFieldPrefillsBighaAndKeepsExactAreaAndCustomerId() {
        val draft=MeasurementDraft(MeasurementMethod.GPS,points,originalTrack=points,stage=MeasurementStage.ACCEPTED,measuredAt=123L,factorUsed=1.6,assignedCustomerId=42)
        var saved: FieldEntity?=null
        compose.setContent { HarvestPayTheme(ThemePreference.LIGHT) { FieldFormScreen(42,null,{saved=it},{},measurement=draft) } }
        compose.onNodeWithText("Field name *").performTextInput("Measured field")
        compose.onNodeWithText("Save field").performScrollTo().performClick()
        compose.runOnIdle {
            val field=requireNotNull(saved)
            assertEquals(42L,field.customerId)
            assertEquals(MeasurementFormatting.bighaInput(draft.areaSquareMeters,1.6).toDouble(),field.sizeBigha,0.0)
            assertEquals(draft.areaSquareMeters,requireNotNull(field.measuredAreaSquareMeters),0.0)
            assertEquals(points,decodeBoundary(field.boundaryCoordinates))
            assertEquals("GPS",field.measurementMethod)
            assertEquals(points,decodeBoundary(field.originalGpsTrack))
        }
    }

    @Test fun measurementBackupRoundTripKeepsBoundaryAndConversion() {
        val customer=CustomerEntity(id=42,name="Test",mobile="")
        val field=FieldEntity(id=7,customerId=42,fieldName="Measured",sizeBigha=4.0,measurementMethod="SATELLITE",measuredAreaSquareMeters=FieldGeometry.area(points),measuredAreaAcres=FieldGeometry.acres(FieldGeometry.area(points)),measurementBighaPerAcre=1.6,measuredAt=123,boundaryCoordinates=encodeBoundary(points))
        val snapshot=DatabaseSnapshot(listOf(customer),listOf(field),emptyList(),emptyList(),emptyList(),emptyList())
        val bundle=com.harvestpay.app.util.BackupBundle(snapshot,AppSettings(bighaPerAcre=1.6))
        val restored=com.harvestpay.app.util.BackupCodec.decode(com.harvestpay.app.util.BackupCodec.encode(bundle))
        assertEquals(field,restored.snapshot.fields.single())
        assertEquals(1.6,requireNotNull(restored.settings.bighaPerAcre),0.0)
    }

    @Test fun measuredFieldMetadataPersistsInRoomWithoutChangingBillingSize() = runBlocking {
        val db=Room.inMemoryDatabaseBuilder(app,AppDatabase::class.java).build()
        try {
            val dao=db.harvestDao()
            val customer=dao.insertCustomer(CustomerEntity(name="Test",mobile=""))
            val field=FieldEntity(customerId=customer,fieldName="Measured",sizeBigha=4.0,measurementMethod="SATELLITE",measuredAreaSquareMeters=FieldGeometry.area(points),measuredAreaAcres=FieldGeometry.acres(FieldGeometry.area(points)),measurementBighaPerAcre=1.6,measuredAt=123,boundaryCoordinates=encodeBoundary(points))
            val id=dao.insertField(field)
            val actual=dao.observeFields().first().single()
            assertEquals(field.copy(id=id),actual)
            assertEquals(4.0,actual.sizeBigha,0.0)
        } finally { db.close() }
    }
}
