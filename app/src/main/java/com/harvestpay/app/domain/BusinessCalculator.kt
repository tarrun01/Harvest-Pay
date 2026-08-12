package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.math.min

object BusinessCalculator {
    private const val EPSILON = 0.005

    fun subtotal(sizeBigha: Double, ratePerBigha: Double, rounds: Double = 1.0): Double =
        money(sizeBigha * ratePerBigha * rounds.coerceAtLeast(0.0))

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
        val paymentsByCustomer = payments.groupBy { it.customerId }

        // Customer payments are account credit. Credit clears the oldest work first and any
        // excess stays available for work recorded later, regardless of the payment's source screen.
        val summariesByWorkId = mutableMapOf<Long, WorkSummary>()
        workEntries.groupBy { it.customerId }.forEach { (customerId, customerWork) ->
            var availableCredit = money(paymentsByCustomer[customerId].orEmpty().sumOf { it.amount })
            customerWork.sortedWith(
                compareBy<WorkEntryEntity> { it.workDate }
                    .thenBy { it.createdAt }
                    .thenBy { it.id },
            ).forEach { work ->
                val applied = money(min(work.totalAmount.coerceAtLeast(0.0), availableCredit.coerceAtLeast(0.0)))
                availableCredit = money((availableCredit - applied).coerceAtLeast(0.0))
                summariesByWorkId[work.id] = WorkSummary(
                    work = work,
                    customer = customersById[work.customerId],
                    field = work.fieldId?.let(fieldsById::get),
                    paid = applied,
                    pending = pending(work.totalAmount, applied),
                    status = status(work.totalAmount, applied),
                )
            }
        }

        val workSummaries = workEntries.mapNotNull { summariesByWorkId[it.id] }

        val workByCustomer = workSummaries.groupBy { it.work.customerId }
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
                advance = money((totalPaid - totalBill).coerceAtLeast(0.0)),
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
                totalPending = money(customerSummaries.sumOf { it.pending }),
                totalAdvance = money(customerSummaries.sumOf { it.advance }),
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
