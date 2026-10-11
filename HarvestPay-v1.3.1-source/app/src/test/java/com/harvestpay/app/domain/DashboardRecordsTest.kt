package com.harvestpay.app.domain

import com.harvestpay.app.data.*
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class DashboardRecordsTest {
    private val today = LocalDate.now()
    private val customer = CustomerEntity(id = 1, name = "Ramesh", mobile = "", previousDue = 999.0)

    @Test
    fun `payment details match active totals and exclude archived and other dates from today`() {
        val archivedDay = today.minusYears(1).withDayOfYear(1).toEpochDay()
        val archive = CustomerYearArchiveEntity(customerId = 1, year = today.year - 1, closingOutstanding = 500.0, archivedAt = 1)
        val payments = listOf(payment(1, archivedDay, 900.0), payment(2, today.toEpochDay(), 100.0), payment(3, today.plusDays(1).toEpochDay(), 50.0))
        val state = BusinessCalculator.buildUiState(listOf(customer), emptyList(), emptyList(), payments, customerYearArchives = listOf(archive))

        assertEquals(listOf(3L, 2L), DashboardRecords.payments(state).map { it.id })
        assertEquals(listOf(2L), DashboardRecords.payments(state, todayOnly = true).map { it.id })
        assertEquals(state.stats.totalReceived, DashboardRecords.payments(state).sumOf { it.amount }, 0.001)
        assertEquals(state.stats.receivedToday, DashboardRecords.payments(state, todayOnly = true).sumOf { it.amount }, 0.001)
    }

    @Test
    fun `pending overview uses carried balance once and distinguishes advance accounts`() {
        val archive = CustomerYearArchiveEntity(customerId = 1, year = today.year - 1, closingOutstanding = 500.0, archivedAt = 1)
        val state = BusinessCalculator.buildUiState(listOf(customer), emptyList(), emptyList(), listOf(payment(1, today.toEpochDay(), 200.0)), customerYearArchives = listOf(archive))
        val pending = DashboardRecords.customers(state, DashboardInsight.OUTSTANDING)
        assertEquals(1, pending.size)
        assertEquals(300.0, pending.single().pending, 0.001)
        assertEquals(state.stats.totalPending, pending.sumOf { it.pending }, 0.001)

        val paidState = BusinessCalculator.buildUiState(listOf(customer), emptyList(), emptyList(), listOf(payment(1, today.toEpochDay(), 600.0)), customerYearArchives = listOf(archive))
        assertTrue(DashboardRecords.customers(paidState, DashboardInsight.OUTSTANDING).isEmpty())
        assertEquals(100.0, DashboardRecords.customers(paidState, DashboardInsight.ADVANCE).single().advance, 0.001)
    }

    @Test
    fun `physical field area stays separate from rounds and minutes`() {
        val field = FieldEntity(id = 10, customerId = 1, fieldName = "North", sizeBigha = 4.0)
        val areaWork = WorkEntryEntity(id = 20, customerId = 1, fieldId = 10, workDate = today.toEpochDay(), sizeBigha = 4.0, ratePerBigha = 100.0, roundMultiplier = 1.5, subtotal = 600.0, totalAmount = 600.0)
        val minuteWork = areaWork.copy(id = 21, billingType = BillingType.PER_MINUTE.name, durationMinutes = 45, subtotal = 4500.0, totalAmount = 4500.0)
        val state = BusinessCalculator.buildUiState(listOf(customer), listOf(field), listOf(areaWork, minuteWork), emptyList())
        assertEquals(4.0, DashboardRecords.physicalBigha(state), 0.001)
        assertEquals(6.0, state.stats.totalBigha, 0.001)
        assertEquals(45L, DashboardRecords.workMinutes(state))
        assertEquals(1, DashboardRecords.fieldOwners(state))
    }

    @Test
    fun `edited and deleted records immediately change detail selections`() {
        val first = payment(1, today.toEpochDay(), 200.0)
        fun state(payments: List<PaymentEntity>) = BusinessCalculator.buildUiState(listOf(customer), emptyList(), emptyList(), payments)
        assertEquals(200.0, DashboardRecords.payments(state(listOf(first)), true).single().amount, 0.001)
        assertEquals(350.0, DashboardRecords.payments(state(listOf(first.copy(amount = 350.0))), true).single().amount, 0.001)
        assertTrue(DashboardRecords.payments(state(emptyList()), true).isEmpty())
    }

    private fun payment(id: Long, date: Long, amount: Double) = PaymentEntity(id = id, customerId = 1, workEntryId = null, paymentDate = date, amount = amount, paymentMethod = "Cash", createdAt = id)
}
