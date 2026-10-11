package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.CustomerYearArchiveEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.data.YearArchiveEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AccountArchiveCalculatorTest {
    private val customer = CustomerEntity(id = 1, name = "Ramesh", mobile = "")
    private fun day(year: Int, month: Int = 1, day: Int = 1) = LocalDate.of(year, month, day).toEpochDay()
    private fun work(year: Int, amount: Double, id: Long = amount.toLong()) = WorkEntryEntity(
        id = id, customerId = 1, fieldId = null, workDate = day(year), sizeBigha = 1.0,
        ratePerBigha = amount, subtotal = amount, totalAmount = amount,
    )
    private fun payment(year: Int, amount: Double, id: Long = amount.toLong()) = PaymentEntity(
        id = id, customerId = 1, workEntryId = null, paymentDate = day(year), amount = amount, paymentMethod = "Cash",
    )

    @Test fun `outstanding carries forward once without moving transactions`() {
        val rows = AccountArchiveCalculator.buildRows(2026, 10, listOf(customer), listOf(work(2026, 50_000.0)), listOf(payment(2026, 42_000.0)), emptyList())
        assertEquals(8_000.0, rows.single().closingOutstanding, 0.001)
        assertEquals(0.0, rows.single().closingAdvance, 0.001)
        assertEquals(2026, AccountArchiveCalculator.pendingYear(2027, listOf(work(2026, 1.0)), emptyList(), emptyList()))
        assertNull(AccountArchiveCalculator.pendingYear(2027, listOf(work(2026, 1.0)), emptyList(), listOf(YearArchiveEntity(2026, 10))))
    }

    @Test fun `advance remains advance and reduces next year work`() {
        val first = AccountArchiveCalculator.buildRows(2026, 10, listOf(customer), listOf(work(2026, 2_000.0)), listOf(payment(2026, 4_500.0)), emptyList()).single()
        assertEquals(2_500.0, first.closingAdvance, 0.001)
        val second = AccountArchiveCalculator.buildRows(2027, 20, listOf(customer), listOf(work(2027, 2_000.0)), emptyList(), listOf(first)).single()
        assertEquals(0.0, second.closingOutstanding, 0.001)
        assertEquals(500.0, second.closingAdvance, 0.001)
    }

    @Test fun `next archive opens from prior snapshot`() {
        val prior = CustomerYearArchiveEntity(1, 2026, closingOutstanding = 8_000.0, archivedAt = 1)
        val row = AccountArchiveCalculator.buildRows(2027, 2, listOf(customer), listOf(work(2027, 6_000.0)), listOf(payment(2027, 3_000.0)), listOf(prior)).single()
        assertEquals(8_000.0, row.openingOutstanding, 0.001)
        assertEquals(11_000.0, row.closingOutstanding, 0.001)
    }

    @Test fun `active account uses archive carry forward while retaining historical rows`() {
        val old = work(2026, 4_500.0, 1)
        val current = work(2027, 2_000.0, 2)
        val archive = CustomerYearArchiveEntity(1, 2026, closingAdvance = 2_500.0, archivedAt = 1)
        val state = BusinessCalculator.buildUiState(
            customers = listOf(customer),
            fields = emptyList(),
            workEntries = listOf(old, current),
            payments = emptyList(),
            customerYearArchives = listOf(archive),
        )
        val summary = state.customerSummaries.single()
        assertEquals(listOf(2L), summary.work.map { it.work.id })
        assertEquals(0.0, summary.pending, 0.001)
        assertEquals(500.0, summary.advance, 0.001)
        assertEquals(listOf(1L, 2L), state.workEntries.map { it.id })
    }
}
