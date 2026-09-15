package com.harvestpay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.ui.components.SectionHeader
import com.harvestpay.app.util.ShareUtils
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatDate
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
) {
    val stats = uiState.stats
    val summariesByCustomer = remember(uiState.customerSummaries) {
        uiState.customerSummaries.associateBy { it.customer.id }
    }
    val pendingCustomers = remember(uiState.customerSummaries) {
        uiState.customerSummaries.filter { it.pending > 0.005 }.sortedByDescending { it.pending }
    }
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(Modifier.padding(top = 12.dp)) {
                val owner = settings.ownerName.trim()
                val greeting = greetingForHour(LocalTime.now().hour)
                Text(
                    if (owner.isBlank()) greeting else "$greeting, $owner",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text("Today’s Business Insight", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
        item {
            MetricGrid(
                listOf(
                    "Total Customers" to stats.totalCustomers.toString(),
                    "Total fields" to stats.totalFields.toString(),
                    "Bigha ploughed" to formatBigha(stats.totalBigha),
                    "Amount earned" to formatMoney(stats.totalEarned),
                    "Amount received" to formatMoney(stats.totalReceived),
                    "Pending amount" to formatMoney(stats.totalPending),
                    "Advance credit" to formatMoney(stats.totalAdvance),
                    "Received today" to formatMoney(stats.receivedToday),
                    "Pending customers" to stats.pendingCustomers.toString(),
                    "Work types" to uiState.workTypes.size.toString(),
                    "Total Diesel Used (Litres)" to formatBigha(stats.totalDieselLitres),
                    "Total Diesel Amount Spent" to formatMoney(stats.totalDieselAmount),
                ),
            )
        }
        item { SectionHeader("Recent customers", "Add", onAction = onAddCustomer) }
        if (uiState.customers.isEmpty()) {
            item { EmptyState("No customers yet", "Add your first farmer to begin the ledger.") }
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
        item { SectionHeader("Recent work") }
        if (uiState.workSummaries.isEmpty()) {
            item { EmptyState("No work recorded", "New ploughing entries will appear here.") }
        } else {
            items(uiState.workSummaries.take(3), key = { "work-${it.work.id}" }) { work ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(work.customer?.appDisplayName ?: "Customer", fontWeight = FontWeight.SemiBold)
                            Text(formatMoney(work.work.totalAmount), fontWeight = FontWeight.Bold)
                        }
                        Text(
                            "${work.field?.fieldName ?: "Field"} • Field size ${formatBigha(work.work.sizeBigha)} Bigha • Round ${formatBigha(work.work.roundMultiplier)} • ${formatBigha(BusinessCalculator.workDoneBigha(work.work.sizeBigha, work.work.roundMultiplier))} Bigha worked • ${formatDate(work.work.workDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AssistChip(onClick = {}, label = { Text(work.status.name.replace('_', ' ')) })
                    }
                }
            }
        }
        item { SectionHeader("Pending payments", if (stats.pendingCustomers > 0) "View all" else null, onAction = onOpenPayments) }
        if (pendingCustomers.isEmpty()) {
            item { EmptyState("All caught up", "There are no pending customer balances.") }
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
        item { androidx.compose.foundation.layout.Spacer(Modifier.padding(bottom = 42.dp)) }
    }
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
                Text("Bill ${formatMoney(summary.totalBill)}", style = MaterialTheme.typography.bodySmall)
                Text("Paid ${formatMoney(summary.totalPaid)}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "${summary.work.count { it.pending > 0.005 }} pending job(s)" +
                    (summary.lastWorkDate?.let { " • Last work ${formatDate(it)}" } ?: ""),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                if (summary.customer.mobile.isNotBlank()) {
                    IconButton(onClick = { ShareUtils.dial(context, summary.customer.mobile) }) {
                        Icon(Icons.Outlined.Call, contentDescription = "Call")
                    }
                    IconButton(onClick = {
                        ShareUtils.openWhatsApp(context, summary.customer.mobile, ShareUtils.paymentMessage(summary))
                    }) { Icon(Icons.Outlined.Chat, contentDescription = "WhatsApp reminder") }
                }
                OutlinedButton(onClick = onView, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null)
                    Text("Details")
                }
                Button(onClick = { confirmPaid = true }, modifier = Modifier.weight(1f)) { Text("Mark paid") }
            }
            TextButton(onClick = onAddPayment, modifier = Modifier.align(Alignment.End)) { Text("Add partial payment") }
        }
    }
    if (confirmPaid) {
        AlertDialog(
            onDismissRequest = { confirmPaid = false },
            title = { Text("Mark complete balance paid?") },
            text = { Text("A payment of ${formatMoney(summary.pending)} will be recorded today.") },
            confirmButton = { TextButton(onClick = { onMarkPaid(); confirmPaid = false }) { Text("Mark as paid") } },
            dismissButton = { TextButton(onClick = { confirmPaid = false }) { Text("Cancel") } },
        )
    }
}
