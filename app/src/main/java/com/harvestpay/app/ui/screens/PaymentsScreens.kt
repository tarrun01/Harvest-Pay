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
import androidx.compose.material3.TabRow
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
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
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
    onDeletePayment: (PaymentEntity) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit,
) {
    var tab by remember { mutableIntStateOf(0) }
    var search by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("Highest amount") }
    var deletePayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var deleteReminder by remember { mutableStateOf<ReminderEntity?>(null) }
    val pending = uiState.customerSummaries.filter {
        it.pending > 0.005 && (search.isBlank() || it.customer.name.contains(search, true) || it.customer.mobile.contains(search))
    }.let { list ->
        when (sort) {
            "Oldest work" -> list.sortedBy { it.lastWorkDate ?: Long.MAX_VALUE }
            "Name" -> list.sortedBy { it.customer.name }
            else -> list.sortedByDescending { it.pending }
        }
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            TabRow(selectedTabIndex = tab, modifier = Modifier.padding(top = 10.dp)) {
                listOf("Pending", "History", "Reminders").forEachIndexed { index, title ->
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
                            Text("${customer?.name ?: "Customer"} • ${formatDate(payment.paymentDate)}")
                            Text(payment.paymentMethod + if (payment.notes.isNotBlank()) " • ${payment.notes}" else "", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deletePayment = payment }) { Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        } else {
            if (reminders.isEmpty()) item { EmptyState("No reminders", "Schedule a local notification for a pending customer.") }
            else items(reminders, key = { "reminder-${it.id}" }) { reminder ->
                val customer = uiState.customers.firstOrNull { it.id == reminder.customerId }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(customer?.name ?: "Customer", fontWeight = FontWeight.Bold)
                            Text(formatDateTime(reminder.reminderAt))
                            Text(reminder.message, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deleteReminder = reminder }) { Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
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
    onSave: (WorkSummary, Double, Long, String, String) -> Unit,
    onCancel: () -> Unit,
) {
    var customer by remember { mutableStateOf<CustomerEntity?>(null) }
    var work by remember { mutableStateOf<WorkSummary?>(null) }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(todayEpochDay()) }
    var method by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }
    androidx.compose.runtime.LaunchedEffect(preselectedCustomerId, uiState.customers) {
        if (customer == null && preselectedCustomerId != null) customer = uiState.customers.firstOrNull { it.id == preselectedCustomerId }
    }
    val pendingWork = uiState.workSummaries.filter { it.work.customerId == customer?.id && it.pending > 0.005 }
    androidx.compose.runtime.LaunchedEffect(customer?.id, pendingWork.size) {
        if (work !in pendingWork) work = pendingWork.firstOrNull()
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Record payment", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item {
            DropdownField("Customer *", customer, uiState.customerSummaries.filter { it.pending > 0.005 }.map { it.customer }, { "${it.name} • ${formatMoney(uiState.customerSummaries.first { summary -> summary.customer.id == it.id }.pending)} pending" }, {
                customer = it; work = null; amount = ""
            })
        }
        item {
            DropdownField(
                "Pending job *",
                work,
                pendingWork,
                { "${it.field?.fieldName ?: "Field"} • ${formatDate(it.work.workDate)} • ${formatMoney(it.pending)}" },
                { selected -> work = selected; amount = selected.pending.toString() },
                enabled = customer != null && pendingWork.isNotEmpty(),
            )
        }
        work?.let { selected ->
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Job total ${formatMoney(selected.work.totalAmount)}")
                        Text("Already paid ${formatMoney(selected.paid)}")
                        Text("Pending ${formatMoney(selected.pending)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        item { DecimalField(amount, { amount = it }, "Payment amount *", prefix = "₹") }
        item {
            work?.let { selected ->
                OutlinedButton(onClick = { amount = selected.pending.toString() }, modifier = Modifier.fillMaxWidth()) { Text("Use full pending amount") }
            }
        }
        item { DateField("Payment date", date, { date = it }) }
        item { DropdownField("Payment method", method, listOf("Cash", "UPI", "Bank Transfer", "Other"), { it }, { method = it }) }
        item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = { work?.let { onSave(it, amount.toDoubleOrNull() ?: 0.0, date, method, notes) } },
                    enabled = work != null && (amount.toDoubleOrNull() ?: 0.0) > 0,
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
        item { Text("${customer.name} • ${formatMoney(pendingAmount)} pending", color = MaterialTheme.colorScheme.error) }
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
