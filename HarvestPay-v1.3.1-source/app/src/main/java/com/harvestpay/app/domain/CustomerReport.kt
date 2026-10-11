package com.harvestpay.app.domain

import com.harvestpay.app.data.CustomerYearArchiveEntity
import com.harvestpay.app.data.PaymentEntity
import java.time.LocalDate

enum class CustomerReportType(val title: String) {
    CURRENT_ACCOUNT("Current Account"),
    OUTSTANDING_ACCOUNT("Outstanding Account"),
    CUSTOM_DATE_RANGE("Custom Date Range"),
    FULL_CURRENT_YEAR("Full Current Year"),
    ARCHIVED_YEAR("Archived Year"),
    COMPLETE_HISTORY("Complete History"),
}

data class CustomerReportSelection(
    val type: CustomerReportType,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val archivedYear: Int? = null,
)

data class CustomerReportSection(
    val title: String,
    val year: Int?,
    val openingOutstanding: Double,
    val openingAdvance: Double,
    val work: List<WorkSummary>,
    val payments: List<PaymentEntity>,
    val workTotal: Double,
    val paymentsTotal: Double,
    val closingOutstanding: Double,
    val closingAdvance: Double,
)

data class CustomerAccountReport(
    val summary: CustomerSummary,
    val title: String,
    val sections: List<CustomerReportSection>,
)

object CustomerReportFactory {
    fun create(
        state: HarvestUiState,
        summary: CustomerSummary,
        selection: CustomerReportSelection,
        currentDate: LocalDate = LocalDate.now(),
    ): CustomerAccountReport {
        val customerId = summary.customer.id
        val allWork = state.workEntries.filter { it.customerId == customerId }
        val fields = state.fields.associateBy { it.id }
        val workSummaries = allWork.map { work ->
            WorkSummary(work, summary.customer, work.fieldId?.let(fields::get), 0.0, work.totalAmount, PaymentStatus.PENDING)
        }
        val allPayments = state.payments.filter { it.customerId == customerId }
        val archiveRows = state.customerYearArchives.filter { it.customerId == customerId }

        fun openingBefore(epochDay: Long): Pair<Double, Double> {
            val net = BusinessCalculator.money(
                summary.customer.previousDue +
                    allWork.filter { it.workDate < epochDay }.sumOf { it.totalAmount } -
                    allPayments.filter { it.paymentDate < epochDay }.sumOf { it.amount },
            )
            return BusinessCalculator.money(net.coerceAtLeast(0.0)) to BusinessCalculator.money((-net).coerceAtLeast(0.0))
        }

        fun section(
            title: String,
            year: Int?,
            work: List<WorkSummary>,
            payments: List<PaymentEntity>,
            opening: Pair<Double, Double>,
            archived: CustomerYearArchiveEntity? = null,
        ): CustomerReportSection {
            val workTotal = BusinessCalculator.money(work.sumOf { it.work.totalAmount })
            val paymentsTotal = BusinessCalculator.money(payments.sumOf { it.amount })
            val net = BusinessCalculator.money(opening.first - opening.second + workTotal - paymentsTotal)
            return CustomerReportSection(
                title = title,
                year = year,
                openingOutstanding = archived?.openingOutstanding ?: opening.first,
                openingAdvance = archived?.openingAdvance ?: opening.second,
                work = work.sortedWith(compareBy({ it.work.workDate }, { it.work.createdAt }, { it.work.id })),
                payments = payments.sortedWith(compareBy({ it.paymentDate }, { it.createdAt }, { it.id })),
                workTotal = archived?.workTotal ?: workTotal,
                paymentsTotal = archived?.paymentsTotal ?: paymentsTotal,
                closingOutstanding = archived?.closingOutstanding ?: BusinessCalculator.money(net.coerceAtLeast(0.0)),
                closingAdvance = archived?.closingAdvance ?: BusinessCalculator.money((-net).coerceAtLeast(0.0)),
            )
        }

        fun yearSection(year: Int, title: String = year.toString()): CustomerReportSection {
            val start = LocalDate.of(year, 1, 1).toEpochDay()
            val row = archiveRows.firstOrNull { it.year == year }
            return section(
                title,
                year,
                workSummaries.filter { AccountArchiveCalculator.year(it.work.workDate) == year },
                allPayments.filter { AccountArchiveCalculator.year(it.paymentDate) == year },
                openingBefore(start),
                row,
            )
        }

        val sections = when (selection.type) {
            CustomerReportType.CURRENT_ACCOUNT,
            CustomerReportType.OUTSTANDING_ACCOUNT,
            -> listOf(
                section(
                    selection.type.title,
                    currentDate.year,
                    summary.work,
                    summary.payments,
                    summary.previousDue to summary.openingAdvance,
                ),
            )
            CustomerReportType.FULL_CURRENT_YEAR -> listOf(yearSection(currentDate.year, "${currentDate.year} Account"))
            CustomerReportType.ARCHIVED_YEAR -> listOf(yearSection(requireNotNull(selection.archivedYear), "${selection.archivedYear} Archived Account"))
            CustomerReportType.CUSTOM_DATE_RANGE -> {
                val from = requireNotNull(selection.fromDate)
                val to = requireNotNull(selection.toDate)
                require(from <= to) { "From Date must be on or before To Date." }
                listOf(
                    section(
                        "${com.harvestpay.app.util.formatDate(from)} – ${com.harvestpay.app.util.formatDate(to)}",
                        null,
                        workSummaries.filter { it.work.workDate in from..to },
                        allPayments.filter { it.paymentDate in from..to },
                        openingBefore(from),
                    ),
                )
            }
            CustomerReportType.COMPLETE_HISTORY -> {
                val years = buildSet {
                    allWork.forEach { add(AccountArchiveCalculator.year(it.workDate)) }
                    allPayments.forEach { add(AccountArchiveCalculator.year(it.paymentDate)) }
                    archiveRows.forEach { add(it.year) }
                    add(currentDate.year)
                }.sorted()
                years.map { yearSection(it) }
            }
        }
        return CustomerAccountReport(summary, selection.type.title, sections)
    }
}
