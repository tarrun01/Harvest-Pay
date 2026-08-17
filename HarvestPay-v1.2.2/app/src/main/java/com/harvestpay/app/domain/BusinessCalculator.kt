package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.DieselEntryEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.floor
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
        dieselEntries: List<DieselEntryEntity> = emptyList(),
    ): HarvestUiState {
        val customersById = customers.associateBy { it.id }
        val fieldsById = fields.associateBy { it.id }
        val paymentsByCustomer = payments.groupBy { it.customerId }

        // Customer payments are account credit. Credit clears the oldest work first and any
        // excess stays available for work recorded later, regardless of the payment's source screen.
        val summariesByWorkId = mutableMapOf<Long, WorkSummary>()
        workEntries.groupBy { it.customerId }.forEach { (customerId, customerWork) ->
            val openingDue = customersById[customerId]?.previousDue?.coerceAtLeast(0.0) ?: 0.0
            var availableCredit = money(
                (paymentsByCustomer[customerId].orEmpty().sumOf { it.amount } - openingDue)
                    .coerceAtLeast(0.0),
            )
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
            val previousDue = money(customer.previousDue.coerceAtLeast(0.0))
            val workTotal = money(customerWork.sumOf { it.work.totalAmount })
            val totalBill = money(previousDue + workTotal)
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
                registeredBigha = fieldsByCustomer[customer.id].orEmpty().sumOf { it.sizeBigha },
                workTotal = workTotal,
                previousDue = previousDue,
                previousDuePaid = money(min(previousDue, totalPaid)),
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
            dieselEntries = dieselEntries,
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
                receivedToday = money(payments.sumOf { if (it.paymentDate == today) it.amount else 0.0 }),
                pendingCustomers = customerSummaries.count { it.pending > EPSILON },
                totalDieselLitres = dieselEntries.sumOf { it.litres.coerceAtLeast(0.0) },
                totalDieselAmount = money(dieselEntries.sumOf { it.totalAmount.coerceAtLeast(0.0) }),
            ),
            loading = false,
        )
    }

    /** Fast HALF_UP two-decimal rounding without allocating BigDecimal objects. */
    fun money(value: Double): Double {
        val scaled = value * 100.0
        val rounded = if (scaled >= 0.0) {
            floor(scaled + 0.500000001)
        } else {
            ceil(scaled - 0.500000001)
        }
        return rounded / 100.0
    }
}
