package com.harvestpay.app.util

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.PaymentStatus
import com.harvestpay.app.domain.WorkSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class ReceiptGeneratorTest {
    @Test
    fun `customer work is chronological with stable tie breaking`() {
        val customer = CustomerEntity(id = 1, name = "Customer", mobile = "")
        fun row(id: Long, date: Long, created: Long) = WorkSummary(
            work = WorkEntryEntity(
                id = id, customerId = 1, fieldId = null, workDate = date,
                sizeBigha = 1.0, ratePerBigha = 800.0,
                subtotal = 800.0, totalAmount = 800.0, createdAt = created,
            ),
            customer = customer, field = null, paid = 0.0, pending = 800.0,
            status = PaymentStatus.PENDING,
        )
        val summary = CustomerSummary(
            customer = customer, fields = emptyList(),
            work = listOf(row(3, 20, 1), row(2, 10, 2), row(1, 10, 1)),
            payments = emptyList(), totalBigha = 3.0, totalBill = 2_400.0,
            totalPaid = 0.0, pending = 2_400.0, advance = 0.0, lastWorkDate = 20,
        )

        assertEquals(listOf(1L, 2L, 3L), ReceiptGenerator.chronologicalWork(summary).map { it.work.id })
    }
}
