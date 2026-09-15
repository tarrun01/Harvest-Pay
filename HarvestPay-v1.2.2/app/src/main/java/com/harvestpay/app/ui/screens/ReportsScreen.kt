package com.harvestpay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.PaymentStatus
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.ui.components.SectionHeader
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatMoney
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun ReportsScreen(uiState: HarvestUiState, onOpenCustomer: (Long) -> Unit) {
    var period by remember { mutableStateOf("This month") }
    var customerId by remember { mutableStateOf<Long?>(null) }
    var village by remember { mutableStateOf("All villages") }
    var status by remember { mutableStateOf("All statuses") }
    val today = LocalDate.now()
    var customFrom by remember { mutableLongStateOf(today.minusMonths(1).toEpochDay()) }
    var customTo by remember { mutableLongStateOf(today.toEpochDay()) }
    val from = when (period) {
        "Today" -> today
        "7 days" -> today.minusDays(6)
        "This month" -> today.withDayOfMonth(1)
        "This year" -> today.withDayOfYear(1)
        "Custom" -> LocalDate.ofEpochDay(customFrom)
        else -> LocalDate.of(1970, 1, 1)
    }.toEpochDay()
    val to = if (period == "Custom") customTo else today.toEpochDay()
    val villages = listOf("All villages") + uiState.customers.map { it.village }.filter(String::isNotBlank).distinct().sorted()
    val customerVillageIds = if (village == "All villages") null else uiState.customers.filter { it.village == village }.map { it.id }.toSet()
    val work = uiState.workSummaries.filter { summary ->
        summary.work.workDate in from..to &&
            (customerId == null || summary.work.customerId == customerId) &&
            (customerVillageIds == null || summary.work.customerId in customerVillageIds) &&
            when (status) {
                "Paid" -> summary.status == PaymentStatus.PAID
                "Partially paid" -> summary.status == PaymentStatus.PARTIALLY_PAID
                "Pending" -> summary.status == PaymentStatus.PENDING
                else -> true
            }
    }
    val workIds = work.map { it.work.id }.toSet()
    val payments = uiState.payments.filter { payment ->
        payment.paymentDate in from..to &&
            (customerId == null || payment.customerId == customerId) &&
            (customerVillageIds == null || payment.customerId in customerVillageIds) &&
            (status == "All statuses" || payment.workEntryId in workIds)
    }
    val totalEarned = work.sumOf { it.work.totalAmount }
    val totalReceived = payments.sumOf { it.amount }
    val customerTotals = work.groupBy { it.work.customerId }.map { (id, rows) ->
        id to rows.sumOf { it.work.totalAmount }
    }.sortedByDescending { it.second }
    val highestPending = uiState.customerSummaries.filter { it.pending > 0.005 }
        .sortedByDescending { it.pending }
    val monthTotals = work.groupBy { YearMonth.from(LocalDate.ofEpochDay(it.work.workDate)) }
        .mapValues { (_, rows) -> rows.sumOf { it.work.totalAmount } }
        .toList().sortedByDescending { it.first }.take(6)

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Business reports", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Today", "7 days", "This month", "This year", "All", "Custom").forEach { option ->
                    FilterChip(selected = period == option, onClick = { period = option }, label = { Text(option) })
                }
            }
        }
        if (period == "Custom") {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DateField("From", customFrom, { customFrom = it.coerceAtMost(customTo) }, Modifier.weight(1f))
                    DateField("To", customTo, { customTo = it.coerceAtLeast(customFrom) }, Modifier.weight(1f))
                }
            }
        }
        item {
            DropdownField(
                "Customer",
                customerId ?: -1L,
                listOf(-1L) + uiState.customers.sortedBy { it.name }.map { it.id },
                { id -> if (id == -1L) "All customers" else uiState.customers.firstOrNull { it.id == id }?.name.orEmpty() },
                { customerId = it.takeUnless { id -> id == -1L } },
            )
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DropdownField("Village", village, villages, { it }, { village = it }, Modifier.weight(1f))
                DropdownField(
                    "Status",
                    status,
                    listOf("All statuses", "Paid", "Partially paid", "Pending"),
                    { it },
                    { status = it },
                    Modifier.weight(1f),
                )
            }
        }
        item {
            MetricGrid(
                listOf(
                    "Earnings" to formatMoney(totalEarned),
                    "Received" to formatMoney(totalReceived),
                    "Pending" to formatMoney((totalEarned - work.sumOf { it.paid }).coerceAtLeast(0.0)),
                    "Bigha ploughed" to formatBigha(
                        work.sumOf { BusinessCalculator.workDoneBigha(it.work.sizeBigha, it.work.roundMultiplier) },
                    ),
                    "Jobs" to work.size.toString(),
                    "Customers" to work.map { it.work.customerId }.distinct().size.toString(),
                ),
            )
        }
        item { SectionHeader("Revenue by month") }
        if (monthTotals.isEmpty()) item { EmptyState("No report data", "Record work to see revenue trends.") }
        else items(monthTotals, key = { it.first.toString() }) { (month, amount) ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(month.month.name.lowercase().replaceFirstChar(Char::uppercase) + " ${month.year}")
                    Text(formatMoney(amount), fontWeight = FontWeight.Bold)
                }
            }
        }
        item { SectionHeader("Most valuable customers") }
        items(customerTotals.take(5), key = { "valuable-${it.first}" }) { (id, amount) ->
            val customer = uiState.customers.firstOrNull { it.id == id }
            Card(onClick = { onOpenCustomer(id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(customer?.name ?: "Customer")
                    Text(formatMoney(amount), fontWeight = FontWeight.Bold)
                }
            }
        }
        item { SectionHeader("Highest pending payments") }
        items(highestPending.take(5), key = { "high-pending-${it.customer.id}" }) { summary ->
            Card(onClick = { onOpenCustomer(summary.customer.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text(summary.customer.name); Text(summary.customer.village, style = MaterialTheme.typography.bodySmall) }
                    AssistChip(onClick = {}, label = { Text(formatMoney(summary.pending)) })
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }
}
