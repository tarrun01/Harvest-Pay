package com.harvestpay.app.ui.screens

import com.harvestpay.app.data.*
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.DashboardInsight
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class DashboardInsightDataTest {
    @org.junit.Before
    fun installResources() = com.harvestpay.app.localization.EnglishResources.install()

    private val customer = CustomerEntity(id = 7, name = "Ramesh", nickname = "North", mobile = "")
    private val field = FieldEntity(id = 31, customerId = 7, fieldName = "Wheat field", sizeBigha = 3.0)
    private val today = LocalDate.now().toEpochDay()

    @Test
    fun `field search preserves the owner and saved field ids`() {
        val state = BusinessCalculator.buildUiState(listOf(customer), listOf(field), emptyList(), emptyList())
        val result = buildInsightPage(state, DashboardInsight.FIELDS, "north")
        assertEquals(InsightTarget.Field(7, 31), result.rows.single().target)
        assertEquals(buildInsightPage(state, DashboardInsight.FIELDS, "").summary, result.summary)
    }

    @Test
    fun `area insight excludes minute jobs while total work includes both`() {
        val area = WorkEntryEntity(id = 41, customerId = 7, fieldId = 31, workDate = today, sizeBigha = 3.0, ratePerBigha = 100.0, subtotal = 300.0, totalAmount = 300.0)
        val timed = area.copy(id = 42, billingType = BillingType.PER_MINUTE.name, durationMinutes = 20)
        val state = BusinessCalculator.buildUiState(listOf(customer), listOf(field), listOf(area, timed), emptyList())
        assertEquals(listOf(InsightTarget.Work(41)), buildInsightPage(state, DashboardInsight.BIGHA, "").rows.map { it.target })
        assertEquals(2, buildInsightPage(state, DashboardInsight.WORK, "").rows.size)
    }

    @Test
    fun `today payment rows open the associated customer and retain their saved date`() {
        val payment = PaymentEntity(id = 51, customerId = 7, workEntryId = null, paymentDate = today, amount = 200.0, paymentMethod = "Cash", notes = "Advance")
        val state = BusinessCalculator.buildUiState(listOf(customer), emptyList(), emptyList(), listOf(payment))
        val row = buildInsightPage(state, DashboardInsight.RECEIVED_TODAY, "").rows.single()
        assertEquals(InsightTarget.Customer(7), row.target)
        assertTrue(row.subtitle.startsWith(com.harvestpay.app.util.formatDate(today)))
        assertEquals(listOf("Advance"), row.details)
    }

    @Test
    fun `diesel record taps retain the saved entry id`() {
        val entry = DieselEntryEntity(id = 61, litres = 10.0, totalAmount = 900.0, entryDate = today, paymentStatus = "Unpaid", broughtBy = "Owner")
        val state = BusinessCalculator.buildUiState(emptyList(), emptyList(), emptyList(), emptyList(), listOf(entry))
        val row = buildInsightPage(state, DashboardInsight.DIESEL_SPENDING, "owner").rows.single()
        assertEquals(InsightTarget.Diesel(61), row.target)
        assertTrue(row.unpaid)
    }
}
