package com.harvestpay.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatDate
import com.harvestpay.app.util.formatDateTime
import com.harvestpay.app.util.formatMoney
import com.harvestpay.app.util.todayEpochDay
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun PaymentsScreen(
    uiState: HarvestUiState,
    reminders: List<ReminderEntity>,
    onOpenCustomer: (Long) -> Unit,
    onAddPayment: (Long?) -> Unit,
    onMarkPaid: (Long) -> Unit,
    onAddReminder: (Long) -> Unit,
    onOpenDieselPayments: () -> Unit,
    onDeletePayment: (PaymentEntity) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit,
) {
    var tab by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("Highest amount") }
    var deletePayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var deleteReminder by remember { mutableStateOf<ReminderEntity?>(null) }
    val pending = remember(uiState.customerSummaries, search, sort) {
        uiState.customerSummaries.filter {
            it.pending > 0.005 && (
                search.isBlank() || it.customer.name.contains(search, true) ||
                    it.customer.nickname.contains(search, true) || it.customer.mobile.contains(search)
                )
        }.let { list ->
            when (sort) {
                "Oldest work" -> list.sortedBy { it.lastWorkDate ?: Long.MAX_VALUE }
                "Name" -> list.sortedBy { it.customer.name }
                else -> list.sortedByDescending { it.pending }
            }
        }
    }
    val advances = remember(uiState.customerSummaries) {
        uiState.customerSummaries.filter { it.advance > 0.005 }
            .sortedByDescending { it.advance }
    }
    val dieselPaid = remember(uiState.dieselEntries) {
        uiState.dieselEntries.filter { it.paymentStatus.equals("Paid", ignoreCase = true) }.sumOf { it.totalAmount }
    }
    val dieselUnpaid = remember(uiState.dieselEntries) {
        uiState.dieselEntries.filter { it.paymentStatus.equals("Unpaid", ignoreCase = true) }.sumOf { it.totalAmount }
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            PrimaryScrollableTabRow(selectedTabIndex = tab, modifier = Modifier.padding(top = 10.dp), edgePadding = 0.dp) {
                listOf("Pending", "History", "Diesel Payments", "Reminders", "Advances").forEachIndexed { index, title ->
                    Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) })
                }
            }
        }
        if (tab == 0) {
            item {
                OutlinedTextField(
                    search,
                    { search = it },
                    label = { Text("Search pending customers") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropdownField(
                        "Sort",
                        sort,
                        listOf("Highest amount", "Oldest work", "Name"),
                        { it },
                        { sort = it },
                        Modifier.weight(1f),
                    )
                    Button(onClick = { onAddPayment(null) }) { Icon(Icons.Outlined.Add, null); Text("Payment") }
                }
            }
            if (pending.isEmpty()) item { EmptyState("No pending payments", "Every recorded balance is settled.") }
            else items(pending, key = { "pending-${it.customer.id}" }) { summary ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    PendingCustomerCard(
                        summary,
                        onView = { onOpenCustomer(summary.customer.id) },
                        onAddPayment = { onAddPayment(summary.customer.id) },
                        onMarkPaid = { onMarkPaid(summary.customer.id) },
                    )
                    OutlinedButton(onClick = { onAddReminder(summary.customer.id) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.NotificationsActive, null)
                        Text("Schedule reminder")
                    }
                }
            }
        } else if (tab == 1) {
            if (uiState.payments.isEmpty()) item { EmptyState("No payment history", "Payments will appear here as they are recorded.") }
            else items(uiState.payments.sortedByDescending { it.paymentDate }, key = { "payment-${it.id}" }) { payment ->
                val customer = uiState.customers.firstOrNull { it.id == payment.customerId }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(formatMoney(payment.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${customer?.appDisplayName ?: "Customer"} • ${formatDate(payment.paymentDate)}")
                            Text(payment.paymentMethod + if (payment.notes.isNotBlank()) " • ${payment.notes}" else "", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deletePayment = payment }) { Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        } else if (tab == 2) {
            item {
                MetricGrid(
                    listOf(
                        "Diesel Paid" to formatMoney(dieselPaid),
                        "Diesel Unpaid" to formatMoney(dieselUnpaid),
                    ),
                )
            }
            item {
                Button(onClick = onOpenDieselPayments, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.LocalGasStation, contentDescription = null)
                    Text("Add or manage diesel")
                }
            }
            if (uiState.dieselEntries.isEmpty()) {
                item { EmptyState("No diesel payments", "Saved diesel purchases will appear here.") }
            } else {
                items(
                    uiState.dieselEntries.sortedWith(
                        compareByDescending<com.harvestpay.app.data.DieselEntryEntity> { it.entryDate }
                            .thenByDescending { it.createdAt },
                    ),
                    key = { "diesel-payment-${it.id}" },
                ) { entry ->
                    Card(onClick = onOpenDieselPayments, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(Icons.Outlined.LocalGasStation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    "${formatBigha(entry.litres)} Litres • ${formatMoney(entry.totalAmount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text("${formatDate(entry.entryDate)} • ${entry.paymentStatus}")
                                Text(
                                    "Brought by ${entry.broughtBy}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        } else if (tab == 3) {
            if (reminders.isEmpty()) item { EmptyState("No reminders", "Schedule a local notification for a pending customer.") }
            else items(reminders, key = { "reminder-${it.id}" }) { reminder ->
                val customer = uiState.customers.firstOrNull { it.id == reminder.customerId }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(customer?.appDisplayName ?: "Customer", fontWeight = FontWeight.Bold)
                            Text(formatDateTime(reminder.reminderAt))
                            Text(reminder.message, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deleteReminder = reminder }) { Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        } else {
            if (advances.isEmpty()) {
                item { EmptyState("No advance balances", "Extra customer payments will appear here as reusable credit.") }
            } else {
                item {
                    Text(
                        "Advance credit is applied automatically to each customer's next work order.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(advances, key = { "advance-${it.customer.id}" }) { summary ->
                    Card(onClick = { onOpenCustomer(summary.customer.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(summary.customer.appDisplayName, fontWeight = FontWeight.Bold)
                                if (summary.customer.mobile.isNotBlank()) {
                                    Text(summary.customer.mobile, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    formatMoney(summary.advance),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text("advance", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }
    deletePayment?.let { payment ->
        ConfirmDialog("Delete payment?", "This will increase the related pending balance.", "Delete", true, { onDeletePayment(payment) }, { deletePayment = null })
    }
    deleteReminder?.let { reminder ->
        ConfirmDialog("Delete reminder?", "The scheduled notification will be cancelled.", "Delete", true, { onDeleteReminder(reminder) }, { deleteReminder = null })
    }
}

@Composable
fun PaymentFormScreen(
    uiState: HarvestUiState,
    preselectedCustomerId: Long?,
    onSave: (CustomerEntity, Double, Long, String, String) -> Unit,
    onCancel: () -> Unit,
) {
    var customer by remember { mutableStateOf<CustomerEntity?>(null) }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(todayEpochDay()) }
    var method by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }
    androidx.compose.runtime.LaunchedEffect(preselectedCustomerId, uiState.customers) {
        if (customer == null && preselectedCustomerId != null) customer = uiState.customers.firstOrNull { it.id == preselectedCustomerId }
    }
    val summary = uiState.customerSummaries.firstOrNull { it.customer.id == customer?.id }
    androidx.compose.runtime.LaunchedEffect(customer?.id, summary?.pending) {
        if (amount.isBlank() && (summary?.pending ?: 0.0) > 0.005) {
            amount = summary?.pending.toString()
        }
    }
    val numericAmount = amount.toDoubleOrNull() ?: 0.0
    val projectedTotalPaid = (summary?.totalPaid ?: 0.0) + numericAmount
    val projectedPending = com.harvestpay.app.domain.BusinessCalculator.pending(
        summary?.totalBill ?: 0.0,
        projectedTotalPaid,
    )
    val projectedAdvance = com.harvestpay.app.domain.BusinessCalculator.money(
        (projectedTotalPaid - (summary?.totalBill ?: 0.0)).coerceAtLeast(0.0),
    )

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Record payment", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item {
            DropdownField(
                "Customer *",
                customer,
                uiState.customerSummaries.map { it.customer },
                { selected ->
                    val account = uiState.customerSummaries.first { it.customer.id == selected.id }
                    when {
                        account.pending > 0.005 -> "${selected.appDisplayName} • ${formatMoney(account.pending)} pending"
                        account.advance > 0.005 -> "${selected.appDisplayName} • ${formatMoney(account.advance)} advance"
                        else -> "${selected.appDisplayName} • settled"
                    }
                },
                { selected ->
                    customer = selected
                    val account = uiState.customerSummaries.firstOrNull { it.customer.id == selected.id }
                    amount = account?.pending?.takeIf { it > 0.005 }?.toString().orEmpty()
                },
            )
        }
        summary?.let { selected ->
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Customer account", fontWeight = FontWeight.Bold)
                        Text("Work total ${formatMoney(selected.totalBill)}")
                        Text("Payments received ${formatMoney(selected.totalPaid)}")
                        when {
                            projectedAdvance > 0.005 -> Text(
                                "After payment: ${formatMoney(projectedAdvance)} advance",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            projectedPending > 0.005 -> Text(
                                "After payment: ${formatMoney(projectedPending)} pending",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            else -> Text(
                                "After payment: settled",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
        item { DecimalField(amount, { amount = it }, "Payment amount *", prefix = "₹") }
        summary?.takeIf { it.pending > 0.005 }?.let { selected ->
            item {
                OutlinedButton(onClick = { amount = selected.pending.toString() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Use full pending amount")
                }
            }
        }
        item {
            Text(
                "Payments clear the oldest due first. Any extra becomes advance credit for the next work order.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { DateField("Payment date", date, { date = it }) }
        item { DropdownField("Payment method", method, listOf("Cash", "UPI", "Bank Transfer", "Other"), { it }, { method = it }) }
        item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = { customer?.let { onSave(it, numericAmount, date, method, notes) } },
                    enabled = customer != null && numericAmount > 0,
                    modifier = Modifier.weight(1f),
                ) { Text("Save payment") }
            }
        }
    }
}

@Composable
fun ReminderFormScreen(
    customer: CustomerEntity?,
    pendingAmount: Double,
    onSave: (Long, String) -> Unit,
    onCancel: () -> Unit,
) {
    if (customer == null) { EmptyState("Customer not found", "Return to pending payments and try again."); return }
    val context = LocalContext.current
    var date by remember { mutableLongStateOf(LocalDate.now().plusDays(1).toEpochDay()) }
    var hour by remember { mutableIntStateOf(9) }
    var message by remember(customer.id, pendingAmount) { mutableStateOf("${customer.name} has ${formatMoney(pendingAmount)} pending.") }
    var pendingTrigger by remember { mutableStateOf<Long?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val trigger = pendingTrigger
        if (granted && trigger != null) onSave(trigger, message)
        pendingTrigger = null
    }
    fun submit() {
        val trigger = LocalDate.ofEpochDay(date).atTime(hour, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            pendingTrigger = trigger
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else onSave(trigger, message)
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Payment reminder", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item { Text("${customer.appDisplayName} • ${formatMoney(pendingAmount)} pending", color = MaterialTheme.colorScheme.error) }
        item { DateField("Reminder date", date, { date = it }) }
        item { DropdownField("Reminder time", hour, listOf(8, 9, 12, 17, 19), { h -> if (h < 12) "$h:00 AM" else if (h == 12) "12:00 PM" else "${h - 12}:00 PM" }, { hour = it }) }
        item { OutlinedTextField(message, { message = it }, label = { Text("Notification message") }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(onClick = ::submit, modifier = Modifier.weight(1f)) { Text("Schedule") }
            }
        }
    }
}
