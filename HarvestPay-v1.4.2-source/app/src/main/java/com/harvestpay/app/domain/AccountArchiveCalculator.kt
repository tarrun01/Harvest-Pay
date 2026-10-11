package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.CustomerYearArchiveEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.data.YearArchiveEntity
import java.time.LocalDate

object AccountArchiveCalculator {
    fun year(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).year

    /** Returns the oldest completed account year that still needs an explicit archive. */
    fun pendingYear(
        currentYear: Int,
        work: List<WorkEntryEntity>,
        payments: List<PaymentEntity>,
        archives: List<YearArchiveEntity>,
    ): Int? {
        val archived = archives.mapTo(mutableSetOf()) { it.year }
        val datedYears = buildSet {
            work.forEach { add(year(it.workDate)) }
            payments.forEach { add(year(it.paymentDate)) }
        }.filter { it < currentYear }
        val first = datedYears.minOrNull() ?: return null
        return (first until currentYear).firstOrNull { it !in archived }
    }

    fun buildRows(
        archiveYear: Int,
        archivedAt: Long,
        customers: List<CustomerEntity>,
        work: List<WorkEntryEntity>,
        payments: List<PaymentEntity>,
        existing: List<CustomerYearArchiveEntity>,
    ): List<CustomerYearArchiveEntity> = customers.map { customer ->
        val prior = existing.filter { it.customerId == customer.id && it.year < archiveYear }.maxByOrNull { it.year }
        val openingOutstanding = BusinessCalculator.money(prior?.closingOutstanding ?: customer.previousDue.coerceAtLeast(0.0))
        val openingAdvance = BusinessCalculator.money(prior?.closingAdvance ?: 0.0)
        val workTotal = BusinessCalculator.money(work.asSequence().filter { it.customerId == customer.id && year(it.workDate) == archiveYear }.sumOf { it.totalAmount })
        val paymentsTotal = BusinessCalculator.money(payments.asSequence().filter { it.customerId == customer.id && year(it.paymentDate) == archiveYear }.sumOf { it.amount })
        val net = BusinessCalculator.money(openingOutstanding - openingAdvance + workTotal - paymentsTotal)
        CustomerYearArchiveEntity(
            customerId = customer.id,
            year = archiveYear,
            openingOutstanding = openingOutstanding,
            openingAdvance = openingAdvance,
            workTotal = workTotal,
            paymentsTotal = paymentsTotal,
            closingOutstanding = BusinessCalculator.money(net.coerceAtLeast(0.0)),
            closingAdvance = BusinessCalculator.money((-net).coerceAtLeast(0.0)),
            archivedAt = archivedAt,
        )
    }
}
