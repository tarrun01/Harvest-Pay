package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.CustomerYearArchiveEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomerReportFactoryTest {
    private fun day(year: Int, month: Int = 1, day: Int = 1) = LocalDate.of(year, month, day).toEpochDay()
    private val customer = CustomerEntity(id = 1, name = "Ramesh", mobile = "")
    private fun work(id: Long, date: Long, amount: Double) = WorkEntryEntity(id, 1, null, date, 1.0, amount, subtotal = amount, totalAmount = amount)
    private fun payment(id: Long, date: Long, amount: Double) = PaymentEntity(id, 1, null, date, amount, "Cash")

    private fun state(): HarvestUiState {
        val oldWork = work(1, day(2026, 3, 5), 50_000.0)
        val currentWork = listOf(work(3, day(2027, 3, 12), 4_000.0), work(2, day(2027, 3, 5), 2_000.0))
        val oldPay = payment(1, day(2026, 5, 1), 42_000.0)
        val currentPay = payment(2, day(2027, 3, 20), 3_000.0)
        val archive = CustomerYearArchiveEntity(1, 2026, workTotal = 50_000.0, paymentsTotal = 42_000.0, closingOutstanding = 8_000.0, archivedAt = 1)
        return BusinessCalculator.buildUiState(
            listOf(customer), emptyList(), listOf(oldWork) + currentWork, listOf(oldPay, currentPay),
            customerYearArchives = listOf(archive),
        )
    }

    @Test fun `current and outstanding exclude archived details and retain opening due`() {
        val state = state()
        val summary = state.customerSummaries.single()
        listOf(CustomerReportType.CURRENT_ACCOUNT, CustomerReportType.OUTSTANDING_ACCOUNT).forEach { type ->
            val section = CustomerReportFactory.create(state, summary, CustomerReportSelection(type), LocalDate.of(2027, 6, 1)).sections.single()
            assertEquals(8_000.0, section.openingOutstanding, 0.001)
            assertEquals(listOf(2L, 3L), section.work.map { it.work.id })
            assertEquals(listOf(2L), section.payments.map { it.id })
            assertEquals(11_000.0, section.closingOutstanding, 0.001)
        }
    }

    @Test fun `archived custom and complete reports select and order records without mutation`() {
        val state = state()
        val summary = state.customerSummaries.single()
        val before = state.workEntries to state.payments
        val archived = CustomerReportFactory.create(state, summary, CustomerReportSelection(CustomerReportType.ARCHIVED_YEAR, archivedYear = 2026)).sections.single()
        assertEquals(listOf(1L), archived.work.map { it.work.id })
        assertEquals(8_000.0, archived.closingOutstanding, 0.001)
        val custom = CustomerReportFactory.create(state, summary, CustomerReportSelection(CustomerReportType.CUSTOM_DATE_RANGE, day(2027, 3, 1), day(2027, 3, 10))).sections.single()
        assertEquals(listOf(2L), custom.work.map { it.work.id })
        val complete = CustomerReportFactory.create(state, summary, CustomerReportSelection(CustomerReportType.COMPLETE_HISTORY), LocalDate.of(2027, 6, 1))
        assertEquals(listOf(2026, 2027), complete.sections.map { it.year })
        assertTrue(complete.sections.all { section -> section.work.zipWithNext().all { (a, b) -> a.work.workDate <= b.work.workDate } })
        assertEquals(before, state.workEntries to state.payments)
    }
}
