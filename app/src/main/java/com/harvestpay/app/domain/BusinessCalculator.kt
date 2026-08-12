package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

object BusinessCalculator {
    private const val EPSILON = 0.005

    fun subtotal(sizeBigha: Double, ratePerBigha: Double, rounds: Int = 1): Double =
        money(sizeBigha * ratePerBigha * rounds.coerceAtLeast(1))

    fun finalAmount(subtotal: Double, discount: Double, extraCharges: Double): Double =
        money((subtotal - discount + extraCharges).coerceAtLeast(0.0))

    fun pending(total: Double, paid: Double): Double = money((total - paid).coerceAtLeast(0.0))

    fun status(total: Double, paid: Double): PaymentStatus = when {
        total <= EPSILON || paid >= total - EPSILON -> PaymentStatus.PAID
        paid > EPSILON -> PaymentStatus.PARTIALLY_PAID
        else -> PaymentStatus.PENDING
    }

    fun buildUiState(
        customers: List<CustomerEntity>,
        fields: List<FieldEntity>,
        workEntries: List<WorkEntryEntity>,
        payments: List<PaymentEntity>,
    ): HarvestUiState {
        val customersById = customers.associateBy { it.id }
        val fieldsById = fields.associateBy { it.id }
        val paymentsByWork = payments.filter { it.workEntryId != null }.groupBy { it.workEntryId }

        val workSummaries = workEntries.map { work ->
            val paid = money(paymentsByWork[work.id].orEmpty().sumOf { it.amount })
            WorkSummary(
                work = work,
                customer = customersById[work.customerId],
                field = work.fieldId?.let(fieldsById::get),
                paid = paid,
                pending = pending(work.totalAmount, paid),
                status = status(work.totalAmount, paid),
            )
        }

        val workByCustomer = workSummaries.groupBy { it.work.customerId }
        val paymentsByCustomer = payments.groupBy { it.customerId }
        val fieldsByCustomer = fields.groupBy { it.customerId }
        val customerSummaries = customers.map { customer ->
            val customerWork = workByCustomer[customer.id].orEmpty()
            val customerPayments = paymentsByCustomer[customer.id].orEmpty()
            val totalBill = money(customerWork.sumOf { it.work.totalAmount })
            val totalPaid = money(customerPayments.sumOf { it.amount })
            CustomerSummary(
                customer = customer,
                fields = fieldsByCustomer[customer.id].orEmpty(),
                work = customerWork,
                payments = customerPayments,
                totalBigha = customerWork.sumOf { it.work.sizeBigha },
                totalBill = totalBill,
                totalPaid = totalPaid,
                pending = pending(totalBill, totalPaid),
                lastWorkDate = customerWork.maxOfOrNull { it.work.workDate },
            )
        }

        val today = LocalDate.now().toEpochDay()
        val totalEarned = money(workEntries.sumOf { it.totalAmount })
        val totalReceived = money(payments.sumOf { it.amount })
        return HarvestUiState(
            customers = customers,
            fields = fields,
            workEntries = workEntries,
            payments = payments,
            workSummaries = workSummaries,
            customerSummaries = customerSummaries,
            stats = DashboardStats(
                totalCustomers = customers.size,
                totalFields = fields.size,
                totalBigha = workEntries.sumOf { it.sizeBigha },
                totalEarned = totalEarned,
                totalReceived = totalReceived,
                totalPending = pending(totalEarned, totalReceived),
                receivedToday = money(payments.filter { it.paymentDate == today }.sumOf { it.amount }),
                pendingCustomers = customerSummaries.count { it.pending > EPSILON },
            ),
            loading = false,
        )
    }

    fun money(value: Double): Double = BigDecimal.valueOf(value)
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()
}
