package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.DashboardInsight
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.DashboardStatCard
import com.harvestpay.app.ui.components.LedgerLoadingState
import com.harvestpay.app.ui.components.LedgerErrorState
import com.harvestpay.app.ui.components.SectionHeader
import com.harvestpay.app.util.ShareUtils
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatUiDate as formatDate
import com.harvestpay.app.util.formatMoney
import java.time.LocalTime

@Composable
fun DashboardScreen(
    uiState: HarvestUiState,
    settings: AppSettings,
    onOpenCustomer: (Long) -> Unit,
    onAddCustomer: () -> Unit,
    onOpenPayments: () -> Unit,
    onMarkPaid: (Long) -> Unit,
    onAddPayment: (Long) -> Unit,
    onOpenInsight: (DashboardInsight) -> Unit,
    onOpenWork: (Long) -> Unit,
    onRetry: () -> Unit,
) {
    if (uiState.loading) { LedgerLoadingState(); return }
    uiState.error?.let { LedgerErrorState(it, onRetry); return }
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val appearance by animateFloatAsState(if (appeared) 1f else 0f, tween(200), label = "dashboard appearance")
    val columns = if (LocalConfiguration.current.screenWidthDp < 360 || LocalDensity.current.fontScale > 1.25f) 1 else 2
    val stats = uiState.stats
    val summariesByCustomer = remember(uiState.customerSummaries) {
        uiState.customerSummaries.associateBy { it.customer.id }
    }
    val pendingCustomers = remember(uiState.customerSummaries) {
        uiState.customerSummaries.filter { it.pending > 0.005 }.sortedByDescending { it.pending }
    }
    LazyColumn(
        modifier = Modifier.graphicsLayer { alpha = appearance },
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(bottom = 6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val owner = settings.ownerName.trim()
                val greeting = AppText.label(greetingForHour(LocalTime.now().hour))
                Text(
                    if (owner.isBlank()) greeting else AppText.text(R.string.text_1_s_2_s_c76fbc, greeting, owner),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(AppText.text(R.string.text_your_business_at_a_glance), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(formatDate(uiState.reportingDay), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(DashboardInsight.entries.filter { it != DashboardInsight.WORK_TYPES }.chunked(columns), key = { "stats-${it.first().name}" }) { insights ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                insights.forEach { insight ->
                    DashboardStatCard(
                        title = AppText.label(insight.title), value = insight.displayValue(uiState), caption = insight.period(uiState), icon = insight.icon(),
                        modifier = Modifier.weight(1f).fillMaxHeight(), featured = insight == DashboardInsight.RECEIVED_TODAY,
                        outstanding = insight == DashboardInsight.OUTSTANDING && stats.totalPending > 0.005,
                        onClick = { onOpenInsight(insight) },
                    )
                }
                if (insights.size < columns) Spacer(Modifier.weight(1f))
            }
        }
        item { SectionHeader(AppText.text(R.string.text_recent_customers), AppText.text(R.string.text_add), onAction = onAddCustomer) }
        if (uiState.customers.isEmpty()) {
            item { EmptyState(AppText.text(R.string.text_no_customers_yet), AppText.text(R.string.text_add_your_first_farmer_to_begin_the_ledger)) }
        } else {
            items(uiState.customers.take(3), key = { "customer-${it.id}" }) { customer ->
                val summary = summariesByCustomer[customer.id]
                Card(onClick = { onOpenCustomer(customer.id) }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(customer.appDisplayName, fontWeight = FontWeight.SemiBold)
                            Text(
                                listOf(customer.village, customer.mobile).filter(String::isNotBlank).joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (summary != null && summary.pending > 0) {
                            Text(formatMoney(summary.pending), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
        item { SectionHeader(AppText.text(R.string.text_recent_work), AppText.text(R.string.text_view_all), onAction = { onOpenInsight(DashboardInsight.WORK) }) }
        if (uiState.workSummaries.isEmpty()) {
            item { EmptyState(AppText.text(R.string.text_no_work_recorded), AppText.text(R.string.text_new_ploughing_entries_will_appear_here)) }
        } else {
            items(uiState.workSummaries.take(3), key = { "work-${it.work.id}" }) { work ->
                Card(onClick = { onOpenWork(work.work.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(work.customer?.appDisplayName ?: AppText.text(R.string.text_customer), modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                            Text(formatMoney(work.work.totalAmount), fontWeight = FontWeight.Bold)
                        }
                        Text(
                            if (work.work.billingMode == BillingType.PER_MINUTE) {
                                AppText.text(R.string.text_1_s_2_s_min_3_s_min_4_s, work.field?.fieldName ?: AppText.text(R.string.text_field), work.work.durationMinutes, formatMoney(work.work.ratePerBigha), formatDate(work.work.workDate))
                            } else {
                                AppText.text(R.string.text_1_s_field_size_2_s_bigha_round_3_s_4_s_bigha_worked_5_s, work.field?.fieldName ?: AppText.text(R.string.text_field), formatBigha(work.work.sizeBigha), formatBigha(work.work.roundMultiplier), formatBigha(BusinessCalculator.billableBigha(work.work)), formatDate(work.work.workDate))
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                            Text(AppText.label(work.status.name), Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
        item { SectionHeader(AppText.text(R.string.text_pending_payments), if (stats.pendingCustomers > 0) AppText.text(R.string.text_view_all) else null, onAction = onOpenPayments) }
        if (pendingCustomers.isEmpty()) {
            item { EmptyState(AppText.text(R.string.text_all_caught_up), AppText.text(R.string.text_there_are_no_pending_customer_balances)) }
        } else {
            items(pendingCustomers.take(4), key = { "pending-${it.customer.id}" }) { summary ->
                PendingCustomerCard(
                    summary = summary,
                    onView = { onOpenCustomer(summary.customer.id) },
                    onAddPayment = { onAddPayment(summary.customer.id) },
                    onMarkPaid = { onMarkPaid(summary.customer.id) },
                )
            }
        }
    }
}

private fun DashboardInsight.icon(): ImageVector = when (this) {
    DashboardInsight.CUSTOMERS, DashboardInsight.PENDING_CUSTOMERS -> Icons.Outlined.People
    DashboardInsight.RECEIVED_TODAY -> Icons.Outlined.Schedule
    DashboardInsight.PAYMENTS -> Icons.Outlined.Payments
    DashboardInsight.FIELDS -> Icons.Outlined.Landscape
    DashboardInsight.WORK, DashboardInsight.BIGHA -> Icons.Outlined.Agriculture
    DashboardInsight.EARNINGS -> Icons.Outlined.TrendingUp
    DashboardInsight.OUTSTANDING, DashboardInsight.ADVANCE -> Icons.Outlined.AccountBalanceWallet
    DashboardInsight.DIESEL_VOLUME, DashboardInsight.DIESEL_SPENDING -> Icons.Outlined.LocalGasStation
    DashboardInsight.WORK_TYPES -> Icons.Outlined.Category
}

internal fun greetingForHour(hour: Int): String = when (hour) {
    in 0..11 -> "Good Morning"
    in 12..16 -> "Good Afternoon"
    else -> "Good Evening"
}

@Composable
fun PendingCustomerCard(
    summary: CustomerSummary,
    onView: () -> Unit,
    onAddPayment: () -> Unit,
    onMarkPaid: () -> Unit,
) {
    val context = LocalContext.current
    var confirmPaid by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.32f)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(summary.customer.appDisplayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (summary.customer.mobile.isNotBlank()) {
                        Text(summary.customer.mobile, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Text(formatMoney(summary.pending), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(AppText.text(R.string.text_bill_1_s, formatMoney(summary.totalBill)), style = MaterialTheme.typography.bodySmall)
                Text(AppText.text(R.string.text_paid_1_s, formatMoney(summary.totalPaid)), style = MaterialTheme.typography.bodySmall)
            }
            Text(
                AppText.text(R.string.text_1_s_pending_job_s, summary.work.count { it.pending > 0.005 }) +
                    (summary.lastWorkDate?.let { AppText.text(R.string.text_last_work_1_s, formatDate(it)) } ?: ""),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (summary.customer.mobile.isNotBlank()) {
                    IconButton(onClick = { ShareUtils.dial(context, summary.customer.mobile) }) {
                        Icon(Icons.Outlined.Call, contentDescription = AppText.text(R.string.text_call))
                    }
                    IconButton(onClick = {
                        ShareUtils.openWhatsApp(context, summary.customer.mobile, ShareUtils.paymentMessage(summary))
                    }) { Icon(Icons.Outlined.Chat, contentDescription = AppText.text(R.string.text_whatsapp_reminder)) }
                }
                OutlinedButton(onClick = onView, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null)
                    Text(AppText.text(R.string.text_details))
                }
                Button(onClick = { confirmPaid = true }, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_mark_paid)) }
            }
            TextButton(onClick = onAddPayment, modifier = Modifier.align(Alignment.End)) { Text(AppText.text(R.string.text_add_partial_payment)) }
        }
    }
    if (confirmPaid) {
        AlertDialog(
            onDismissRequest = { confirmPaid = false },
            title = { Text(AppText.text(R.string.text_mark_complete_balance_paid)) },
            text = { Text(AppText.text(R.string.text_a_payment_of_1_s_will_be_recorded_today, formatMoney(summary.pending))) },
            confirmButton = { TextButton(onClick = { onMarkPaid(); confirmPaid = false }) { Text(AppText.text(R.string.text_mark_as_paid)) } },
            dismissButton = { TextButton(onClick = { confirmPaid = false }) { Text(AppText.text(R.string.text_cancel)) } },
        )
    }
}
