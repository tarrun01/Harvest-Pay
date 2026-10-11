package com.harvestpay.app.domain

import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.billingMode

/** Display order is shared by the dashboard and its navigation destinations. */
enum class DashboardInsight(val title: String) {
    CUSTOMERS("Total Customers"),
    RECEIVED_TODAY("Received Today"),
    OUTSTANDING("Pending amount"),
    PAYMENTS("Amount received"),
    EARNINGS("Amount earned"),
    WORK("Total Work"),
    FIELDS("Total fields"),
    BIGHA("Bigha ploughed"),
    ADVANCE("Advance credit"),
    PENDING_CUSTOMERS("Pending customers"),
    DIESEL_VOLUME("Total Diesel Used (Litres)"),
    DIESEL_SPENDING("Total Diesel Amount Spent"),
    WORK_TYPES("Work types"),
}

/** Select records from the existing calculated accounts; never reapply archive balances. */
object DashboardRecords {
    fun payments(state: HarvestUiState, todayOnly: Boolean = false): List<PaymentEntity> =
        state.customerSummaries.asSequence().flatMap { it.payments.asSequence() }
            .filter { !todayOnly || it.paymentDate == state.reportingDay }
            .sortedWith(compareByDescending<PaymentEntity> { it.paymentDate }.thenByDescending { it.createdAt })
            .toList()

    fun customers(state: HarvestUiState, insight: DashboardInsight): List<CustomerSummary> =
        when (insight) {
            DashboardInsight.OUTSTANDING, DashboardInsight.PENDING_CUSTOMERS ->
                state.customerSummaries.filter { it.pending > 0.005 }.sortedByDescending { it.pending }
            DashboardInsight.ADVANCE -> state.customerSummaries.filter { it.advance > 0.005 }.sortedByDescending { it.advance }
            else -> state.customerSummaries.sortedBy { it.customer.name.lowercase() }
        }

    fun physicalBigha(state: HarvestUiState): Double = state.fields.sumOf { it.sizeBigha }

    fun fieldOwners(state: HarvestUiState): Int = state.fields.map { it.customerId }.distinct().size

    fun workMinutes(state: HarvestUiState): Long = state.workSummaries.asSequence()
        .filter { it.work.billingMode == BillingType.PER_MINUTE }.sumOf { it.work.durationMinutes.toLong() }
}
