package com.harvestpay.app.ui.screens

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.ui.components.SectionHeader
import com.harvestpay.app.util.ReceiptData
import com.harvestpay.app.util.ReceiptGenerator
import com.harvestpay.app.util.ShareUtils
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatDate
import com.harvestpay.app.util.formatMoney

@Composable
fun CustomersScreen(
    uiState: HarvestUiState,
    onAddCustomer: () -> Unit,
    onOpenCustomer: (Long) -> Unit,
    onEditCustomer: (Long) -> Unit,
    onDeleteCustomer: (CustomerEntity) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("Recent") }
    var deleteTarget by remember { mutableStateOf<CustomerEntity?>(null) }
    val summaries = remember(uiState.customerSummaries, query, sort) {
        uiState.customerSummaries.filter { summary ->
            query.isBlank() || listOf(
                summary.customer.name,
                summary.customer.mobile,
                summary.customer.village,
                summary.customer.address,
            ).any { it.contains(query, ignoreCase = true) }
        }.let { list ->
            when (sort) {
                "A–Z" -> list.sortedBy { it.customer.name.lowercase() }
                "Highest pending" -> list.sortedByDescending { it.pending }
                else -> list.sortedByDescending { it.customer.createdAt }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search name, mobile, village or address") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                DropdownField(
                    label = "Sort",
                    value = sort,
                    options = listOf("Recent", "A–Z", "Highest pending"),
                    optionLabel = { it },
                    onSelected = { sort = it },
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = onAddCustomer) { Icon(Icons.Outlined.Add, null); Text("Customer") }
            }
        }
        if (summaries.isEmpty()) {
            item { EmptyState("No customers found", if (query.isBlank()) "Add a customer to get started." else "Try a different search.") }
        } else {
            items(summaries, key = { it.customer.id }) { summary ->
                CustomerListCard(
                    summary = summary,
                    onOpen = { onOpenCustomer(summary.customer.id) },
                    onEdit = { onEditCustomer(summary.customer.id) },
                    onDelete = { deleteTarget = summary.customer },
                )
            }
        }
        item { Spacer(Modifier.height(36.dp)) }
    }

    deleteTarget?.let { customer ->
        ConfirmDialog(
            title = "Delete ${customer.name}?",
            message = "Their fields, work and payment records will also be permanently deleted. Export a backup first if needed.",
            confirmLabel = "Delete customer",
            destructive = true,
            onConfirm = { onDeleteCustomer(customer) },
            onDismiss = { deleteTarget = null },
        )
    }
}

@Composable
private fun CustomerListCard(
    summary: CustomerSummary,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(summary.customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        listOf(summary.customer.village, summary.customer.mobile).filter(String::isNotBlank).joinToString(" • "),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        formatMoney(if (summary.advance > 0.005) summary.advance else summary.pending),
                        fontWeight = FontWeight.Bold,
                        color = if (summary.pending > 0.005) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    )
                    Text(if (summary.advance > 0.005) "advance" else "pending", style = MaterialTheme.typography.labelSmall)
                }
            }
            Text("${summary.fields.size} fields • ${formatBigha(summary.fields.sumOf { it.sizeBigha })} Bigha registered")
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.align(Alignment.End)) {
                IconButton(onClick = { ShareUtils.dial(context, summary.customer.mobile) }) { Icon(Icons.Outlined.Call, "Call") }
                IconButton(onClick = { ShareUtils.openWhatsApp(context, summary.customer.mobile, ShareUtils.paymentMessage(summary)) }) {
                    Icon(Icons.Outlined.Chat, "WhatsApp")
                }
                IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, "Edit") }
                IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
fun CustomerFormScreen(
    existing: CustomerEntity?,
    onSave: (CustomerEntity) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember(existing) { mutableStateOf(existing?.name.orEmpty()) }
    var mobile by remember(existing) { mutableStateOf(existing?.mobile.orEmpty()) }
    var address by remember(existing) { mutableStateOf(existing?.address.orEmpty()) }
    var village by remember(existing) { mutableStateOf(existing?.village.orEmpty()) }
    var notes by remember(existing) { mutableStateOf(existing?.notes.orEmpty()) }
    var previousDue by remember(existing) {
        mutableStateOf(existing?.previousDue?.takeIf { it > 0.0 }?.toString().orEmpty())
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text(if (existing == null) "Add customer" else "Edit customer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item { OutlinedTextField(name, { name = it }, label = { Text("Customer name *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item {
            OutlinedTextField(
                mobile,
                { value -> if (value.length <= 13) mobile = value.filter { it.isDigit() || it == '+' } },
                label = { Text("Mobile number *") },
                prefix = { Text("+91 ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item { OutlinedTextField(village, { village = it }, label = { Text("Village") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(address, { address = it }, label = { Text("Address") }, minLines = 2, modifier = Modifier.fillMaxWidth()) }
        item { DecimalField(previousDue, { previousDue = it }, "Previous Due (₹) — optional") }
        item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        existing?.let { item { Text("Added ${formatDate(java.time.Instant.ofEpochMilli(it.createdAt).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toEpochDay())}", style = MaterialTheme.typography.bodySmall) } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = {
                        onSave(
                            CustomerEntity(
                                id = existing?.id ?: 0,
                                name = name,
                                mobile = mobile,
                                address = address,
                                village = village,
                                notes = notes,
                                previousDue = previousDue.toDoubleOrNull() ?: 0.0,
                                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Save customer") }
            }
        }
        item { Spacer(Modifier.height(36.dp)) }
    }
}

@Composable
fun FieldFormScreen(
    customerId: Long,
    existing: FieldEntity?,
    onSave: (FieldEntity) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember(existing) { mutableStateOf(existing?.fieldName.orEmpty()) }
    var number by remember(existing) { mutableStateOf(existing?.fieldNumber.orEmpty()) }
    var location by remember(existing) { mutableStateOf(existing?.location.orEmpty()) }
    var size by remember(existing) { mutableStateOf(existing?.sizeBigha?.toString().orEmpty()) }
    var notes by remember(existing) { mutableStateOf(existing?.notes.orEmpty()) }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(if (existing == null) "Add field" else "Edit field", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item { OutlinedTextField(name, { name = it }, label = { Text("Field name *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(number, { number = it }, label = { Text("Field number") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(location, { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth()) }
        item { DecimalField(size, { size = it }, "Size in Bigha *") }
        item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(
                    onClick = {
                        onSave(
                            FieldEntity(
                                id = existing?.id ?: 0,
                                customerId = customerId,
                                fieldName = name,
                                fieldNumber = number,
                                location = location,
                                sizeBigha = size.toDoubleOrNull() ?: 0.0,
                                notes = notes,
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) { Text("Save field") }
            }
        }
    }
}

@Composable
fun CustomerProfileScreen(
    summary: CustomerSummary?,
    settings: AppSettings,
    onEdit: () -> Unit,
    onAddField: () -> Unit,
    onEditField: (Long) -> Unit,
    onDeleteField: (FieldEntity) -> Unit,
    onAddWork: () -> Unit,
    onAddPayment: () -> Unit,
    onDeleteWork: (com.harvestpay.app.data.WorkEntryEntity) -> Unit,
    onDeletePayment: (PaymentEntity) -> Unit,
) {
    if (summary == null) {
        EmptyState("Customer not found", "This customer may have been deleted.")
        return
    }
    val context = LocalContext.current
    var tab by remember { mutableIntStateOf(0) }
    var deleteField by remember { mutableStateOf<FieldEntity?>(null) }
    var deleteWork by remember { mutableStateOf<WorkSummary?>(null) }
    var deletePayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var receiptToSave by remember { mutableStateOf<ReceiptData?>(null) }
    val saveReceipt = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val data = receiptToSave
        if (uri != null && data != null) context.contentResolver.openOutputStream(uri)?.use { ReceiptGenerator.writePdf(data, it) }
        receiptToSave = null
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(summary.customer.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(summary.customer.mobile, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (summary.customer.village.isNotBlank()) Text(summary.customer.village)
                            if (summary.customer.address.isNotBlank()) Text(summary.customer.address, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, "Edit customer") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { ShareUtils.dial(context, summary.customer.mobile) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Outlined.Call, null); Text("Call")
                        }
                        FilledTonalButton(
                            onClick = { ShareUtils.openWhatsApp(context, summary.customer.mobile, ShareUtils.paymentMessage(summary)) },
                            modifier = Modifier.weight(1f),
                        ) { Icon(Icons.Outlined.Chat, null); Text("WhatsApp") }
                    }
                    OutlinedButton(
                        onClick = { ReceiptGenerator.shareCustomer(context, summary, settings) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.PictureAsPdf, contentDescription = null)
                        Text("Share PDF")
                    }
                }
            }
        }
        item {
            MetricGrid(
                listOf(
                    "Fields" to summary.fields.size.toString(),
                    "Registered Bigha" to formatBigha(summary.registeredBigha),
                    "Previous due" to formatMoney(summary.previousDue),
                    "Work amount" to formatMoney(summary.workTotal),
                    "Total bill" to formatMoney(summary.totalBill),
                    "Paid" to formatMoney(summary.totalPaid),
                    "Pending" to formatMoney(summary.pending),
                    "Advance credit" to formatMoney(summary.advance),
                    "Bigha ploughed" to formatBigha(summary.totalBigha),
                ),
            )
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onAddField, modifier = Modifier.weight(1f)) { Text("Add field") }
                OutlinedButton(onClick = onAddWork, modifier = Modifier.weight(1f)) { Text("Add work") }
                Button(onClick = onAddPayment, modifier = Modifier.weight(1f)) { Text("Payment") }
            }
        }
        item {
            val tabs = listOf("Fields", "Work", "Payments", "Notes")
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { index, title -> Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) }) }
            }
        }
        when (tab) {
            0 -> {
                if (summary.fields.isEmpty()) item { EmptyState("No fields", "Add this customer's first field.") }
                else items(summary.fields, key = { "field-${it.id}" }) { field ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(field.fieldName, fontWeight = FontWeight.SemiBold)
                                Text("${formatBigha(field.sizeBigha)} Bigha" + if (field.location.isNotBlank()) " • ${field.location}" else "")
                                if (field.notes.isNotBlank()) Text(field.notes, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onEditField(field.id) }) { Icon(Icons.Outlined.Edit, "Edit field") }
                            IconButton(onClick = { deleteField = field }) { Icon(Icons.Outlined.Delete, "Delete field", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
            1 -> {
                if (summary.work.isEmpty()) item { EmptyState("No work history", "Record a ploughing job for this customer.") }
                else items(summary.work.sortedByDescending { it.work.workDate }, key = { "work-${it.work.id}" }) { work ->
                    WorkHistoryCard(
                        work = work,
                        payments = summary.payments.filter { it.workEntryId == work.work.id },
                        onMessage = { ReceiptGenerator.share(context, ReceiptGenerator.from(work, settings), whatsapp = true) },
                        onShareReceipt = { ReceiptGenerator.share(context, ReceiptGenerator.from(work, settings), whatsapp = false) },
                        onSavePdf = {
                            receiptToSave = ReceiptGenerator.from(work, settings)
                            saveReceipt.launch("HarvestPay-${summary.customer.name}-${formatDate(work.work.workDate)}.pdf")
                        },
                        onDelete = { deleteWork = work },
                    )
                }
            }
            2 -> {
                if (summary.previousDue > 0.005) item {
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Opening / previous due", fontWeight = FontWeight.SemiBold)
                                Text("Balance brought forward", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                formatMoney(summary.previousDue),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
                if (summary.payments.isEmpty()) item {
                    EmptyState("No payments", "Recorded payments will appear below the opening balance.")
                } else items(summary.payments.sortedByDescending { it.paymentDate }, key = { "payment-${it.id}" }) { payment ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(formatMoney(payment.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${formatDate(payment.paymentDate)} • ${payment.paymentMethod}")
                                if (payment.notes.isNotBlank()) Text(payment.notes, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { deletePayment = payment }) { Icon(Icons.Outlined.Delete, "Delete payment", tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
            else -> item {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        summary.customer.notes.ifBlank { "No customer notes added." },
                        modifier = Modifier.padding(18.dp),
                        color = if (summary.customer.notes.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }

    deleteField?.let { field ->
        ConfirmDialog("Delete field?", "Work history will keep its financial record, but this field will be removed.", "Delete", true, { onDeleteField(field) }, { deleteField = null })
    }
    deleteWork?.let { work ->
        ConfirmDialog("Delete work entry?", "Linked payments will remain in the customer ledger but will no longer be tied to this work.", "Delete", true, { onDeleteWork(work.work) }, { deleteWork = null })
    }
    deletePayment?.let { payment ->
        ConfirmDialog("Delete payment?", "The pending balance will increase by ${formatMoney(payment.amount)}.", "Delete", true, { onDeletePayment(payment) }, { deletePayment = null })
    }
}

@Composable
private fun WorkHistoryCard(
    work: WorkSummary,
    payments: List<PaymentEntity>,
    onMessage: () -> Unit,
    onShareReceipt: () -> Unit,
    onSavePdf: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(work.field?.fieldName ?: "Field", fontWeight = FontWeight.Bold)
                Text(formatMoney(work.work.totalAmount), fontWeight = FontWeight.Bold)
            }
            Text(
                "${formatDate(work.work.workDate)} • ${work.work.workType} • " +
                    "${formatBigha(work.work.sizeBigha)} Bigha @ ${formatMoney(work.work.ratePerBigha)} • " +
                    "${formatBigha(work.work.roundMultiplier)} rounds",
            )
            Text("Paid ${formatMoney(work.paid)} • Pending ${formatMoney(work.pending)}", style = MaterialTheme.typography.bodySmall)
            if (payments.isNotEmpty()) {
                Text(
                    payments.joinToString(prefix = "Payments: ", separator = " • ") {
                        "${formatMoney(it.amount)} ${it.paymentMethod} (${formatDate(it.paymentDate)})"
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (work.work.notes.isNotBlank()) Text(work.work.notes, style = MaterialTheme.typography.bodySmall)
            AssistChip(onClick = {}, label = { Text(work.status.name.replace('_', ' ')) })
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalButton(onClick = onMessage) {
                    Icon(Icons.Outlined.Chat, contentDescription = null)
                    Text("Message")
                }
                IconButton(onClick = onShareReceipt) { Icon(Icons.Outlined.Share, "Share receipt") }
                IconButton(onClick = onSavePdf) { Icon(Icons.Outlined.PictureAsPdf, "Save PDF") }
                IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, "Delete work", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
