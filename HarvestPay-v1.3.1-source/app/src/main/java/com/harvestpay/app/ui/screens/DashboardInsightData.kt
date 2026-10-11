package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.domain.DashboardInsight
import com.harvestpay.app.domain.DashboardRecords
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatUiDate as formatDate
import com.harvestpay.app.util.formatMoney

sealed interface InsightTarget {
    data class Customer(val id: Long) : InsightTarget
    data class Field(val customerId: Long, val fieldId: Long) : InsightTarget
    data class Work(val id: Long) : InsightTarget
    data class Diesel(val id: Long) : InsightTarget
    data object WorkTypes : InsightTarget
}

internal data class InsightRow(
    val id: Long,
    val title: String,
    val value: String,
    val subtitle: String,
    val details: List<String> = emptyList(),
    val status: String? = null,
    val unpaid: Boolean = false,
    val target: InsightTarget,
)

internal data class InsightPage(
    val summary: List<Pair<String, String>>,
    val rows: List<InsightRow>,
    val searchHint: String,
    val emptyTitle: String,
    val emptyBody: String,
)

internal fun DashboardInsight.displayValue(state: HarvestUiState): String = with(state.stats) {
    when (this@displayValue) {
        DashboardInsight.CUSTOMERS -> totalCustomers.toString()
        DashboardInsight.RECEIVED_TODAY -> formatMoney(receivedToday)
        DashboardInsight.FIELDS -> totalFields.toString()
        DashboardInsight.WORK -> state.workSummaries.size.toString()
        DashboardInsight.BIGHA -> formatBigha(totalBigha)
        DashboardInsight.EARNINGS -> formatMoney(totalEarned)
        DashboardInsight.PAYMENTS -> formatMoney(totalReceived)
        DashboardInsight.OUTSTANDING -> formatMoney(totalPending)
        DashboardInsight.ADVANCE -> formatMoney(totalAdvance)
        DashboardInsight.PENDING_CUSTOMERS -> pendingCustomers.toString()
        DashboardInsight.WORK_TYPES -> state.workTypes.size.toString()
        DashboardInsight.DIESEL_VOLUME -> formatBigha(totalDieselLitres)
        DashboardInsight.DIESEL_SPENDING -> formatMoney(totalDieselAmount)
    }
}

internal fun DashboardInsight.period(state: HarvestUiState): String = when (this) {
    DashboardInsight.RECEIVED_TODAY -> formatDate(state.reportingDay)
    DashboardInsight.CUSTOMERS, DashboardInsight.FIELDS -> AppText.text(R.string.text_all_registered)
    DashboardInsight.WORK_TYPES -> AppText.text(R.string.text_available_services)
    DashboardInsight.DIESEL_VOLUME, DashboardInsight.DIESEL_SPENDING -> AppText.text(R.string.text_all_diesel_records)
    else -> AppText.text(R.string.text_current_accounts)
}

/** Presentation only: financial totals come directly from BusinessCalculator's state. */
internal fun buildInsightPage(state: HarvestUiState, insight: DashboardInsight, query: String): InsightPage {
    val search = query.trim()
    fun matches(vararg values: String) = search.isBlank() || values.any { it.contains(search, ignoreCase = true) }
    val customers = state.customers.associateBy { it.id }
    val primarySummary = AppText.label(insight.title) to insight.displayValue(state)
    return when (insight) {
        DashboardInsight.CUSTOMERS, DashboardInsight.OUTSTANDING, DashboardInsight.PENDING_CUSTOMERS, DashboardInsight.ADVANCE -> {
            val accounts = DashboardRecords.customers(state, insight)
            InsightPage(
                summary = listOf(primarySummary) + when (insight) {
                    DashboardInsight.CUSTOMERS -> listOf(AppText.text(R.string.text_unpaid_accounts) to state.stats.pendingCustomers.toString())
                    DashboardInsight.PENDING_CUSTOMERS -> listOf(AppText.text(R.string.text_pending_amount) to formatMoney(state.stats.totalPending))
                    else -> listOf(AppText.text(R.string.text_customers) to accounts.size.toString())
                },
                rows = accounts.filter { matches(it.customer.appDisplayName, it.customer.mobile, it.customer.village, it.customer.address) }.map { account ->
                    InsightRow(
                        id = account.customer.id, title = account.customer.appDisplayName,
                        value = formatMoney(if (insight == DashboardInsight.ADVANCE) account.advance else account.pending),
                        subtitle = AppText.text(R.string.text_1_s_fields, account.fields.size) + account.customer.village.takeIf { it.isNotBlank() }?.let { AppText.text(R.string.text_1_s, it) }.orEmpty(),
                        details = buildList {
                            if (insight == DashboardInsight.ADVANCE) add(AppText.text(R.string.text_available_advance_credit))
                            if (account.previousDue > 0.005 && insight != DashboardInsight.CUSTOMERS) {
                                val year = account.openingFromYear?.let { AppText.text(R.string.text_from_1_s, it) }.orEmpty()
                                add(AppText.text(R.string.text_opening_outstanding_1_s_2_s, year, formatMoney(account.previousDue)))
                                add(AppText.text(R.string.text_already_included_in_this_account_not_an_additional_char))
                            }
                        },
                        status = if (account.pending > 0.005) "Unpaid" else "Settled",
                        unpaid = account.pending > 0.005,
                        target = InsightTarget.Customer(account.customer.id),
                    )
                },
                searchHint = AppText.text(R.string.text_search_customers),
                emptyTitle = if (insight == DashboardInsight.CUSTOMERS) AppText.text(R.string.text_no_customers_yet) else if (insight == DashboardInsight.ADVANCE) AppText.text(R.string.text_no_advance_credit) else AppText.text(R.string.text_all_accounts_settled),
                emptyBody = AppText.text(R.string.text_customer_accounts_will_appear_here_as_you_add_records),
            )
        }
        DashboardInsight.FIELDS -> InsightPage(
            summary = listOf(primarySummary, AppText.text(R.string.text_original_area) to AppText.text(R.string.text_1_s_bigha, formatBigha(DashboardRecords.physicalBigha(state))), AppText.text(R.string.text_field_owners) to DashboardRecords.fieldOwners(state).toString()),
            rows = state.fields.filter { matches(it.fieldName, it.fieldNumber, it.location, customers[it.customerId]?.appDisplayName.orEmpty()) }
                .sortedBy { it.fieldName.lowercase() }.map { field ->
                    InsightRow(field.id, field.fieldName, AppText.text(R.string.text_1_s_bigha, com.harvestpay.app.measurement.MeasurementFormatting.display(field.sizeBigha)), customers[field.customerId]?.appDisplayName ?: AppText.text(R.string.text_customer),
                        details = listOfNotNull(field.fieldNumber.takeIf(String::isNotBlank)?.let { AppText.text(R.string.text_field_no_1_s, it) }, field.location.takeIf(String::isNotBlank), field.notes.takeIf(String::isNotBlank)),
                        target = InsightTarget.Field(field.customerId, field.id))
                },
            searchHint = AppText.text(R.string.text_search_fields_owners_or_location), emptyTitle = AppText.text(R.string.text_no_fields_yet), emptyBody = AppText.text(R.string.text_saved_fields_and_their_original_sizes_will_appear_here),
        )
        DashboardInsight.WORK, DashboardInsight.EARNINGS, DashboardInsight.BIGHA -> {
            val work = state.workSummaries.filter { insight != DashboardInsight.BIGHA || it.work.billingMode == BillingType.PER_BIGHA }
            InsightPage(
                summary = listOf(primarySummary) + buildList {
                    if (insight != DashboardInsight.WORK) add(AppText.text(R.string.text_work_entries) to work.size.toString())
                    if (insight != DashboardInsight.BIGHA) add(AppText.text(R.string.text_area_worked) to AppText.text(R.string.text_1_s_bigha, formatBigha(state.stats.totalBigha)))
                    if (insight != DashboardInsight.BIGHA) add(AppText.text(R.string.text_time_worked) to AppText.text(R.string.text_1_s_min, DashboardRecords.workMinutes(state)))
                    if (insight == DashboardInsight.WORK) add(AppText.text(R.string.text_amount_billed) to formatMoney(state.stats.totalEarned))
                },
                rows = work.filter { matches(it.customer?.appDisplayName.orEmpty(), it.work.workType, it.field?.fieldName.orEmpty(), formatDate(it.work.workDate)) }
                    .sortedWith(compareByDescending<com.harvestpay.app.domain.WorkSummary> { it.work.workDate }.thenByDescending { it.work.createdAt }).map { record ->
                        val entry = record.work
                        val quantity = if (entry.billingMode == BillingType.PER_MINUTE) AppText.text(R.string.text_1_s_min, entry.durationMinutes) else AppText.text(R.string.text_1_s_bigha_worked, formatBigha(BusinessCalculator.billableBigha(entry)))
                        InsightRow(entry.id, record.customer?.appDisplayName ?: AppText.text(R.string.text_customer), formatMoney(entry.totalAmount), AppText.text(R.string.text_1_s_2_s, formatDate(entry.workDate), entry.workType),
                            details = listOf(quantity + record.field?.fieldName?.let { AppText.text(R.string.text_1_s, it) }.orEmpty()),
                            target = InsightTarget.Work(entry.id))
                    },
                searchHint = AppText.text(R.string.text_search_customer_work_type_or_date), emptyTitle = AppText.text(R.string.text_no_work_recorded), emptyBody = AppText.text(R.string.text_work_from_current_accounts_will_appear_here_archived_wo),
            )
        }
        DashboardInsight.PAYMENTS, DashboardInsight.RECEIVED_TODAY -> {
            val payments = DashboardRecords.payments(state, todayOnly = insight == DashboardInsight.RECEIVED_TODAY)
            InsightPage(
                summary = listOf(primarySummary, AppText.text(R.string.text_payments) to payments.size.toString()),
                rows = payments.filter { matches(customers[it.customerId]?.appDisplayName.orEmpty(), it.paymentMethod, it.notes, formatDate(it.paymentDate)) }.map { payment ->
                    InsightRow(payment.id, customers[payment.customerId]?.appDisplayName ?: AppText.text(R.string.text_customer), formatMoney(payment.amount), AppText.text(R.string.text_1_s_2_s, formatDate(payment.paymentDate), payment.paymentMethod),
                        details = listOfNotNull(payment.notes.takeIf(String::isNotBlank)), target = InsightTarget.Customer(payment.customerId))
                },
                searchHint = AppText.text(R.string.text_search_customer_method_or_date), emptyTitle = if (insight == DashboardInsight.RECEIVED_TODAY) AppText.text(R.string.text_no_payments_today) else AppText.text(R.string.text_no_payments_recorded), emptyBody = AppText.text(R.string.text_recorded_customer_payments_will_appear_here),
            )
        }
        DashboardInsight.DIESEL_VOLUME, DashboardInsight.DIESEL_SPENDING -> InsightPage(
            summary = (if (insight == DashboardInsight.DIESEL_VOLUME) listOf(AppText.text(R.string.text_total_volume) to AppText.text(R.string.text_1_s_litres, formatBigha(state.stats.totalDieselLitres)), AppText.text(R.string.text_total_expenditure) to formatMoney(state.stats.totalDieselAmount))
                else listOf(AppText.text(R.string.text_total_expenditure) to formatMoney(state.stats.totalDieselAmount), AppText.text(R.string.text_total_volume) to AppText.text(R.string.text_1_s_litres, formatBigha(state.stats.totalDieselLitres)))) + (AppText.text(R.string.text_entries) to state.dieselEntries.size.toString()),
            rows = state.dieselEntries.filter { matches(it.broughtBy, it.paymentStatus, formatDate(it.entryDate)) }.sortedByDescending { it.entryDate }.map { entry ->
                InsightRow(entry.id, entry.broughtBy, formatMoney(entry.totalAmount), AppText.text(R.string.text_1_s_2_s_litres, formatDate(entry.entryDate), formatBigha(entry.litres)),
                    status = entry.paymentStatus, unpaid = entry.paymentStatus.equals("Unpaid", ignoreCase = true), target = InsightTarget.Diesel(entry.id))
            },
            searchHint = AppText.text(R.string.text_search_person_status_or_date), emptyTitle = AppText.text(R.string.text_no_diesel_entries), emptyBody = AppText.text(R.string.text_diesel_purchases_will_appear_here_with_their_payment_st),
        )
        DashboardInsight.WORK_TYPES -> InsightPage(
            summary = listOf(primarySummary),
            rows = state.workTypes.filter { matches(it.name, it.hindiName) }.map { type ->
                val unit = if (type.billingMode == BillingType.PER_MINUTE) AppText.label("minute") else AppText.text(R.string.text_bigha)
                InsightRow(type.id, type.name, formatMoney(type.ratePerBigha), AppText.text(R.string.text_per_1_s, unit), details = listOfNotNull(type.hindiName.takeIf(String::isNotBlank)), target = InsightTarget.WorkTypes)
            },
            searchHint = AppText.text(R.string.text_search_work_types), emptyTitle = AppText.text(R.string.text_no_work_types), emptyBody = AppText.text(R.string.text_manage_your_services_from_more_work_types),
        )
    }
}
