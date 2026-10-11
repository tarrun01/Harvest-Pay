package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.ReminderEntity
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatUiDate as formatDate
import com.harvestpay.app.util.formatUiDateTime as formatDateTime
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
    var search by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf("Highest amount") }
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
                listOf(AppText.text(R.string.text_history), "Pending", AppText.text(R.string.text_diesel_payments), AppText.text(R.string.text_reminders), AppText.text(R.string.text_advances)).forEachIndexed { index, title ->
                    Tab(selected = tab == index, onClick = { tab = index }, text = { Text(AppText.label(title)) })
                }
            }
        }
        if (tab == 1) {
            item {
                OutlinedTextField(
                    search,
                    { search = it },
                    label = { Text(AppText.text(R.string.text_search_pending_customers)) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropdownField(
                        AppText.text(R.string.text_sort),
                        sort,
                        listOf("Highest amount", "Oldest work", "Name"),
                        { AppText.label(it) },
                        { sort = it },
                        Modifier.weight(1f),
                    )
                    Button(onClick = { onAddPayment(null) }) { Icon(Icons.Outlined.Add, null); Text(AppText.text(R.string.text_payment)) }
                }
            }
            if (pending.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_pending_payments), AppText.text(R.string.text_every_recorded_balance_is_settled)) }
            else items(pending, key = { "pending-${it.customer.id}" }) { summary ->
                CompactPendingCustomerCard(
                    summary,
                    onView = { onOpenCustomer(summary.customer.id) },
                    onAddPayment = { onAddPayment(summary.customer.id) },
                    onMarkPaid = { onMarkPaid(summary.customer.id) },
                    onAddReminder = { onAddReminder(summary.customer.id) },
                )
            }
        } else if (tab == 0) {
            if (uiState.payments.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_payment_history), AppText.text(R.string.text_payments_will_appear_here_as_they_are_recorded)) }
            else items(uiState.payments.sortedByDescending { it.paymentDate }, key = { "payment-${it.id}" }) { payment ->
                val customer = uiState.customers.firstOrNull { it.id == payment.customerId }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(formatMoney(payment.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(AppText.text(R.string.text_1_s_2_s, customer?.appDisplayName ?: AppText.text(R.string.text_customer), formatDate(payment.paymentDate)))
                            Text(payment.paymentMethod + if (payment.notes.isNotBlank()) AppText.text(R.string.text_1_s, payment.notes) else "", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deletePayment = payment }) { Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete), tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        } else if (tab == 2) {
            item {
                MetricGrid(
                    listOf(
                        AppText.text(R.string.text_diesel_paid) to formatMoney(dieselPaid),
                        AppText.text(R.string.text_diesel_unpaid) to formatMoney(dieselUnpaid),
                    ),
                )
            }
            item {
                Button(onClick = onOpenDieselPayments, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.LocalGasStation, contentDescription = null)
                    Text(AppText.text(R.string.text_add_or_manage_diesel))
                }
            }
            if (uiState.dieselEntries.isEmpty()) {
                item { EmptyState(AppText.text(R.string.text_no_diesel_payments), AppText.text(R.string.text_saved_diesel_purchases_will_appear_here)) }
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
                                    AppText.text(R.string.text_1_s_litres_2_s, formatBigha(entry.litres), formatMoney(entry.totalAmount)),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(AppText.text(R.string.text_1_s_2_s, formatDate(entry.entryDate), entry.paymentStatus))
                                Text(
                                    AppText.text(R.string.text_brought_by_1_s, entry.broughtBy),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        } else if (tab == 3) {
            if (reminders.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_reminders), AppText.text(R.string.text_schedule_a_local_notification_for_a_pending_customer)) }
            else items(reminders, key = { "reminder-${it.id}" }) { reminder ->
                val customer = uiState.customers.firstOrNull { it.id == reminder.customerId }
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(customer?.appDisplayName ?: AppText.text(R.string.text_customer), fontWeight = FontWeight.Bold)
                            Text(formatDateTime(reminder.reminderAt))
                            Text(reminder.message, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deleteReminder = reminder }) { Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete), tint = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        } else {
            if (advances.isEmpty()) {
                item { EmptyState(AppText.text(R.string.text_no_advance_balances), AppText.text(R.string.text_extra_customer_payments_will_appear_here_as_reusable_cr)) }
            } else {
                item {
                    Text(
                        AppText.text(R.string.text_advance_credit_is_applied_automatically_to_each_custome),
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
                                Text(AppText.label("ADVANCE"), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }
    deletePayment?.let { payment ->
        ConfirmDialog(AppText.text(R.string.text_delete_payment_522c64), AppText.text(R.string.text_this_will_increase_the_related_pending_balance), AppText.text(R.string.text_delete), true, { onDeletePayment(payment) }, { deletePayment = null })
    }
    deleteReminder?.let { reminder ->
        ConfirmDialog(AppText.text(R.string.text_delete_reminder), AppText.text(R.string.text_the_scheduled_notification_will_be_cancelled), AppText.text(R.string.text_delete), true, { onDeleteReminder(reminder) }, { deleteReminder = null })
    }
}

/** Compact payment queue; the dashboard retains its existing detailed card. */
@Composable
private fun CompactPendingCustomerCard(
    summary: CustomerSummary,
    onView: () -> Unit,
    onAddPayment: () -> Unit,
    onMarkPaid: () -> Unit,
    onAddReminder: () -> Unit,
) {
    var confirmPaid by remember { mutableStateOf(false) }
    Card(onClick = onView, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    summary.customer.appDisplayName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    formatMoney(summary.pending),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { confirmPaid = true }, modifier = Modifier.weight(1f)) {
                    Text(AppText.text(R.string.text_mark_paid))
                }
                OutlinedButton(onClick = onAddPayment, modifier = Modifier.weight(1f)) {
                    Text(AppText.text(R.string.text_add_partial_payment))
                }
            }
            OutlinedButton(onClick = onAddReminder, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.NotificationsActive, contentDescription = null)
                Text(AppText.text(R.string.text_schedule_reminder))
            }
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

@Composable
fun PaymentFormScreen(
    uiState: HarvestUiState,
    preselectedCustomerId: Long?,
    onSave: (CustomerEntity, Double, Long, String, String) -> Unit,
    onCancel: () -> Unit,
) {
    var customer by remember { mutableStateOf<CustomerEntity?>(null) }
    var amount by rememberSaveable { mutableStateOf("") }
    var date by remember { mutableLongStateOf(todayEpochDay()) }
    var method by remember { mutableStateOf("Cash") }
    var notes by rememberSaveable { mutableStateOf("") }
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
        item { Text(AppText.text(R.string.text_record_payment), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item {
            DropdownField(
                AppText.text(R.string.text_customer_6e2808),
                customer,
                uiState.customerSummaries.map { it.customer },
                { selected ->
                    val account = uiState.customerSummaries.first { it.customer.id == selected.id }
                    when {
                        account.pending > 0.005 -> AppText.text(R.string.text_1_s_2_s_pending, selected.appDisplayName, formatMoney(account.pending))
                        account.advance > 0.005 -> AppText.text(R.string.text_1_s_2_s_advance, selected.appDisplayName, formatMoney(account.advance))
                        else -> AppText.text(R.string.text_1_s_settled, selected.appDisplayName)
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
                        Text(AppText.text(R.string.text_customer_account), fontWeight = FontWeight.Bold)
                        Text(AppText.text(R.string.text_work_total_1_s, formatMoney(selected.totalBill)))
                        Text(AppText.text(R.string.text_payments_received_1_s, formatMoney(selected.totalPaid)))
                        when {
                            projectedAdvance > 0.005 -> Text(
                                AppText.text(R.string.text_after_payment_1_s_advance, formatMoney(projectedAdvance)),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            projectedPending > 0.005 -> Text(
                                AppText.text(R.string.text_after_payment_1_s_pending, formatMoney(projectedPending)),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                            else -> Text(
                                AppText.text(R.string.text_after_payment_settled),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
        item { DecimalField(amount, { amount = it }, AppText.text(R.string.text_payment_amount), prefix = "₹") }
        summary?.takeIf { it.pending > 0.005 }?.let { selected ->
            item {
                OutlinedButton(onClick = { amount = selected.pending.toString() }, modifier = Modifier.fillMaxWidth()) {
                    Text(AppText.text(R.string.text_use_full_pending_amount))
                }
            }
        }
        item {
            Text(
                AppText.text(R.string.text_payments_clear_the_oldest_due_first_any_extra_becomes_a),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { DateField(AppText.text(R.string.text_payment_date), date, { date = it }) }
        item { DropdownField(AppText.text(R.string.text_payment_method), method, listOf("Cash", "UPI", "Bank Transfer", "Other"), { AppText.label(it) }, { method = it }) }
        item { OutlinedTextField(notes, { notes = it }, label = { Text(AppText.text(R.string.text_notes)) }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_cancel)) }
                Button(
                    onClick = { customer?.let { onSave(it, numericAmount, date, method, notes) } },
                    enabled = customer != null && numericAmount > 0,
                    modifier = Modifier.weight(1f),
                ) { Text(AppText.text(R.string.text_save_payment)) }
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
    if (customer == null) { EmptyState(AppText.text(R.string.text_customer_not_found), AppText.text(R.string.text_return_to_pending_payments_and_try_again)); return }
    val context = LocalContext.current
    var date by remember { mutableLongStateOf(LocalDate.now().plusDays(1).toEpochDay()) }
    var hour by remember { mutableIntStateOf(9) }
    var message by remember(customer.id, pendingAmount) { mutableStateOf(AppText.text(R.string.text_1_s_has_2_s_pending, customer.name, formatMoney(pendingAmount))) }
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
        item { Text(AppText.text(R.string.text_payment_reminder), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item { Text(AppText.text(R.string.text_1_s_2_s_pending, customer.appDisplayName, formatMoney(pendingAmount)), color = MaterialTheme.colorScheme.error) }
        item { DateField(AppText.text(R.string.text_reminder_date), date, { date = it }) }
        item { DropdownField(AppText.text(R.string.text_reminder_time), hour, listOf(8, 9, 12, 17, 19), { h -> if (h < 12) AppText.text(R.string.text_1_s_00_am, h) else if (h == 12) AppText.text(R.string.text_12_00_pm) else AppText.text(R.string.text_1_s_00_pm, h - 12) }, { hour = it }) }
        item { OutlinedTextField(message, { message = it }, label = { Text(AppText.text(R.string.text_notification_message)) }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_cancel)) }
                Button(onClick = ::submit, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_schedule)) }
            }
        }
    }
}
