package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.CustomerReportFactory
import com.harvestpay.app.domain.CustomerReportSelection
import com.harvestpay.app.domain.CustomerReportType
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.util.PdfLanguage
import com.harvestpay.app.util.ReceiptGenerator
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatUiDate as formatDate
import com.harvestpay.app.util.formatMoney

@Composable
fun ArchivedYearScreen(
    uiState: HarvestUiState,
    customerId: Long,
    year: Int,
    settings: AppSettings,
) {
    val summary = uiState.customerSummaries.firstOrNull { it.customer.id == customerId }
    if (summary == null) {
        EmptyState(AppText.text(R.string.text_archived_account_unavailable), AppText.text(R.string.text_the_customer_could_not_be_found))
        return
    }
    val report = remember(uiState, customerId, year) {
        CustomerReportFactory.create(
            uiState,
            summary,
            CustomerReportSelection(CustomerReportType.ARCHIVED_YEAR, archivedYear = year),
        )
    }
    val section = report.sections.single()
    val context = LocalContext.current
    var showLanguage by remember { mutableStateOf(false) }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(AppText.text(R.string.text_1_s_archived_account, year), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(summary.customer.name)
                    Text(AppText.text(R.string.text_read_only_historical_record), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { showLanguage = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.PictureAsPdf, null); Text(AppText.text(R.string.text_share_archived_year_pdf))
                    }
                }
            }
        }
        item {
            MetricGrid(
                listOf(
                    AppText.text(R.string.text_opening_outstanding) to formatMoney(section.openingOutstanding),
                    AppText.text(R.string.text_opening_advance) to formatMoney(section.openingAdvance),
                    AppText.text(R.string.text_work_charges) to formatMoney(section.workTotal),
                    AppText.text(R.string.text_payments) to formatMoney(section.paymentsTotal),
                    AppText.text(R.string.text_closing_due) to formatMoney(section.closingOutstanding),
                    AppText.text(R.string.text_closing_advance) to formatMoney(section.closingAdvance),
                    AppText.text(R.string.text_account_status) to if (section.closingOutstanding > 0.005) "UNPAID" else "SETTLED",
                ),
            )
        }
        item { Text(AppText.text(R.string.text_work_history), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        if (section.work.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_work_entries), AppText.text(R.string.text_no_work_was_recorded_in_1_s, year)) }
        else items(section.work, key = { it.work.id }) { item ->
            val work = item.work
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(work.workType, fontWeight = FontWeight.Bold); Text(formatMoney(work.totalAmount), fontWeight = FontWeight.Bold)
                }
                Text(AppText.text(R.string.text_1_s_2_s, formatDate(work.workDate), item.field?.fieldName ?: AppText.text(R.string.text_field)))
                if (work.billingMode == BillingType.PER_MINUTE) {
                    Text(AppText.text(R.string.text_per_minute_1_s_min_2_s_min, work.durationMinutes, formatMoney(work.ratePerBigha)))
                } else {
                    Text(AppText.text(R.string.text_per_bigha_original_1_s_bigha_round_2_s_calculated_3_s_b, formatBigha(work.sizeBigha), formatBigha(work.roundMultiplier), formatBigha(BusinessCalculator.billableBigha(work)), formatMoney(work.ratePerBigha)))
                }
            } }
        }
        item { Text(AppText.text(R.string.text_payments_advances), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        if (section.payments.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_payments), AppText.text(R.string.text_no_payment_was_recorded_in_1_s, year)) }
        else items(section.payments, key = { it.id }) { payment ->
            Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text(formatDate(payment.paymentDate), fontWeight = FontWeight.SemiBold); Text(payment.paymentMethod + payment.notes.takeIf(String::isNotBlank)?.let { AppText.text(R.string.text_1_s, it) }.orEmpty()) }
                Text(formatMoney(payment.amount), fontWeight = FontWeight.Bold)
            } }
        }
    }

    if (showLanguage) AlertDialog(
        onDismissRequest = { showLanguage = false },
        title = { Text(AppText.text(R.string.text_select_pdf_language)) },
        text = { Column {
            listOf(PdfLanguage.ENGLISH to "English", PdfLanguage.HINDI to "हिंदी", PdfLanguage.BOTH to AppText.text(R.string.text_both)).forEach { (language, label) ->
                TextButton(onClick = { showLanguage = false; ReceiptGenerator.shareCustomer(context, report, settings, language) }) { Text(label, modifier = Modifier.fillMaxWidth()) }
            }
        } },
        confirmButton = {},
        dismissButton = { TextButton(onClick = { showLanguage = false }) { Text(AppText.text(R.string.text_cancel)) } },
    )
}

@Composable
fun YearArchivePromptDialog(year: Int, onLater: () -> Unit, onArchive: () -> Unit) {
    AlertDialog(
        onDismissRequest = onLater,
        title = { Text(AppText.text(R.string.text_1_s_account_year_completed, year)) },
        text = { Text(AppText.text(R.string.text_the_previous_year_s_customer_accounts_are_ready_to_be_a, year + 1)) },
        confirmButton = { Button(onClick = onArchive) { Text(AppText.text(R.string.text_backup_archive)) } },
        dismissButton = { TextButton(onClick = onLater) { Text(AppText.text(R.string.text_later)) } },
    )
}
