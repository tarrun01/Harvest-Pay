package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class BusinessCalculatorTest {
    @Test
    fun `fast money rounding keeps half up behavior`() {
        assertEquals(1.01, BusinessCalculator.money(1.005), 0.0001)
        assertEquals(-1.01, BusinessCalculator.money(-1.005), 0.0001)
    }

    @Test
    fun `calculates rounded work total with rounds discount and extras`() {
        val subtotal = BusinessCalculator.subtotal(sizeBigha = 2.75, ratePerBigha = 825.0, rounds = 2.0)
        val total = BusinessCalculator.finalAmount(subtotal, discount = 150.0, extraCharges = 75.5)

        assertEquals(4537.50, subtotal, 0.001)
        assertEquals(4463.00, total, 0.001)
    }

    @Test
    fun `supports decimal rounds`() {
        assertEquals(400.0, BusinessCalculator.subtotal(1.0, 800.0, 0.5), 0.001)
    }

    @Test
    fun `never returns a negative final or pending amount`() {
        assertEquals(0.0, BusinessCalculator.finalAmount(100.0, 150.0, 10.0), 0.001)
        assertEquals(0.0, BusinessCalculator.pending(100.0, 125.0), 0.001)
    }

    @Test
    fun `derives each payment status`() {
        assertEquals(PaymentStatus.PENDING, BusinessCalculator.status(4_000.0, 0.0))
        assertEquals(PaymentStatus.PARTIALLY_PAID, BusinessCalculator.status(4_000.0, 1_500.0))
        assertEquals(PaymentStatus.PAID, BusinessCalculator.status(4_000.0, 4_000.0))
    }

    @Test
    fun `builds customer dashboard and ledger totals from related records`() {
        val today = LocalDate.now().toEpochDay()
        val customer = CustomerEntity(id = 1, name = "Ramesh", mobile = "9876543210")
        val field = FieldEntity(id = 10, customerId = 1, fieldName = "North Field", sizeBigha = 5.0)
        val work = WorkEntryEntity(
            id = 100,
            customerId = 1,
            fieldId = 10,
            workDate = today,
            sizeBigha = 5.0,
            ratePerBigha = 800.0,
            subtotal = 4_000.0,
            totalAmount = 4_000.0,
        )
        val payment = PaymentEntity(
            id = 1_000,
            customerId = 1,
            workEntryId = 100,
            paymentDate = today,
            amount = 1_500.0,
            paymentMethod = "Cash",
        )

        val state = BusinessCalculator.buildUiState(
            customers = listOf(customer),
            fields = listOf(field),
            workEntries = listOf(work),
            payments = listOf(payment),
        )

        assertEquals(1, state.stats.totalCustomers)
        assertEquals(1, state.stats.totalFields)
        assertEquals(5.0, state.stats.totalBigha, 0.001)
        assertEquals(4_000.0, state.stats.totalEarned, 0.001)
        assertEquals(1_500.0, state.stats.totalReceived, 0.001)
        assertEquals(2_500.0, state.stats.totalPending, 0.001)
        assertEquals(1_500.0, state.stats.receivedToday, 0.001)
        assertEquals(1, state.stats.pendingCustomers)
        assertEquals(PaymentStatus.PARTIALLY_PAID, state.workSummaries.single().status)
        assertEquals(2_500.0, state.customerSummaries.single().pending, 0.001)
    }

    @Test
    fun `keeps overpayment as advance and applies it to the next work`() {
        val customer = CustomerEntity(id = 1, name = "Ramesh", mobile = "9876543210")
        val firstWork = WorkEntryEntity(
            id = 100,
            customerId = 1,
            fieldId = null,
            workDate = 1,
            sizeBigha = 1.0,
            ratePerBigha = 800.0,
            subtotal = 800.0,
            totalAmount = 800.0,
        )
        val advancePayment = PaymentEntity(
            id = 1_000,
            customerId = 1,
            workEntryId = null,
            paymentDate = 1,
            amount = 1_000.0,
            paymentMethod = "Cash",
        )

        val beforeNextWork = BusinessCalculator.buildUiState(
            listOf(customer), emptyList(), listOf(firstWork), listOf(advancePayment),
        )
        assertEquals(0.0, beforeNextWork.customerSummaries.single().pending, 0.001)
        assertEquals(200.0, beforeNextWork.customerSummaries.single().advance, 0.001)

        val secondWork = firstWork.copy(
            id = 101,
            workDate = 2,
            subtotal = 500.0,
            totalAmount = 500.0,
        )
        val afterNextWork = BusinessCalculator.buildUiState(
            listOf(customer), emptyList(), listOf(firstWork, secondWork), listOf(advancePayment),
        )

        assertEquals(0.0, afterNextWork.customerSummaries.single().advance, 0.001)
        assertEquals(300.0, afterNextWork.customerSummaries.single().pending, 0.001)
        assertEquals(200.0, afterNextWork.workSummaries.first { it.work.id == 101L }.paid, 0.001)
    }

    @Test
    fun `does not use one customer advance to hide another customer balance`() {
        val customers = listOf(
            CustomerEntity(id = 1, name = "Advance", mobile = "9876543210"),
            CustomerEntity(id = 2, name = "Pending", mobile = "9876543211"),
        )
        val work = customers.map { customer ->
            WorkEntryEntity(
                id = customer.id,
                customerId = customer.id,
                fieldId = null,
                workDate = 1,
                sizeBigha = 1.0,
                ratePerBigha = 800.0,
                subtotal = 800.0,
                totalAmount = 800.0,
            )
        }
        val payment = PaymentEntity(
            id = 1,
            customerId = 1,
            workEntryId = null,
            paymentDate = 1,
            amount = 1_600.0,
            paymentMethod = "Cash",
        )

        val state = BusinessCalculator.buildUiState(customers, emptyList(), work, listOf(payment))

        assertEquals(800.0, state.stats.totalAdvance, 0.001)
        assertEquals(800.0, state.stats.totalPending, 0.001)
    }
}
