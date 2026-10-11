package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
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
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.PaymentEntity
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.ui.components.SectionHeader
import com.harvestpay.app.util.ReceiptData
import com.harvestpay.app.util.ReceiptGenerator
import com.harvestpay.app.util.PdfLanguage
import com.harvestpay.app.util.ShareUtils
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatUiDate as formatDate
import com.harvestpay.app.util.formatMoney
import com.harvestpay.app.domain.CustomerReportFactory
import com.harvestpay.app.domain.CustomerReportSelection
import com.harvestpay.app.domain.CustomerReportType
import java.time.LocalDate

@Composable
fun CustomersScreen(
    uiState: HarvestUiState,
    onAddCustomer: () -> Unit,
    onOpenCustomer: (Long) -> Unit,
    onEditCustomer: (Long) -> Unit,
    onDeleteCustomer: (CustomerEntity, String, (Boolean) -> Unit) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf("Recent") }
    var deleteTarget by remember { mutableStateOf<CustomerEntity?>(null) }
    var adminPassword by remember { mutableStateOf("") }
    val summaries = remember(uiState.customerSummaries, query, sort) {
        uiState.customerSummaries.filter { summary ->
            query.isBlank() || listOf(
                summary.customer.name,
                summary.customer.nickname,
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
                label = { Text(AppText.text(R.string.text_search_name_nickname_mobile_village_or_address)) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                DropdownField(
                    label = AppText.text(R.string.text_sort),
                    value = sort,
                    options = listOf("Recent", "A–Z", "Highest pending"),
                    optionLabel = { AppText.label(it) },
                    onSelected = { sort = it },
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = onAddCustomer) { Icon(Icons.Outlined.Add, null); Text(AppText.text(R.string.text_customer)) }
            }
        }
        if (summaries.isEmpty()) {
            item { EmptyState(AppText.text(R.string.text_no_customers_found), if (query.isBlank()) AppText.text(R.string.text_add_a_customer_to_get_started) else AppText.text(R.string.text_try_a_different_search)) }
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
        AlertDialog(
            onDismissRequest = { deleteTarget = null; adminPassword = "" }, title = { Text(AppText.text(R.string.text_delete_customer)) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(AppText.text(R.string.text_1_s_s_fields_work_and_payments_will_be_permanently_dele, customer.name)); OutlinedTextField(adminPassword, { adminPassword = it }, label = { Text(AppText.text(R.string.text_enter_admin_password)) }, visualTransformation = PasswordVisualTransformation(), singleLine = true) } },
            confirmButton = { TextButton(enabled = adminPassword.isNotBlank(), onClick = { onDeleteCustomer(customer, adminPassword) { if (it) { deleteTarget = null; adminPassword = "" } } }) { Text(AppText.text(R.string.text_delete), color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { deleteTarget = null; adminPassword = "" }) { Text(AppText.text(R.string.text_cancel)) } },
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
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(summary.customer.appDisplayName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatMoney(summary.pending),
                    fontWeight = FontWeight.Bold,
                    color = if (summary.pending > 0.005) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
                Text(AppText.text(R.string.text_pending_amount), style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, AppText.text(R.string.text_edit)) }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete), tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun CustomerFormScreen(
    existing: CustomerEntity?,
    onSave: (CustomerEntity) -> Unit,
    onCancel: () -> Unit,
) {
    var name by rememberSaveable(existing) { mutableStateOf(existing?.name.orEmpty()) }
    var nickname by rememberSaveable(existing) { mutableStateOf(existing?.nickname.orEmpty()) }
    var mobile by rememberSaveable(existing) { mutableStateOf(existing?.mobile.orEmpty()) }
    var address by rememberSaveable(existing) { mutableStateOf(existing?.address.orEmpty()) }
    var village by rememberSaveable(existing) { mutableStateOf(existing?.village.orEmpty()) }
    var notes by rememberSaveable(existing) { mutableStateOf(existing?.notes.orEmpty()) }
    var previousDue by remember(existing) {
        mutableStateOf(existing?.previousDue?.takeIf { it > 0.0 }?.toString().orEmpty())
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text(if (existing == null) AppText.text(R.string.text_add_customer) else AppText.text(R.string.text_edit_customer), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    name,
                    { name = it },
                    label = { Text(AppText.text(R.string.text_customer_name)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    nickname,
                    { nickname = it },
                    label = { Text(AppText.text(R.string.text_nickname)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            OutlinedTextField(
                mobile,
                { value -> if (value.length <= 13) mobile = value.filter { it.isDigit() || it == '+' } },
                label = { Text(AppText.text(R.string.text_mobile_number_optional)) },
                prefix = { Text("+91 ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item { OutlinedTextField(village, { village = it }, label = { Text(AppText.text(R.string.text_village)) }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(address, { address = it }, label = { Text(AppText.text(R.string.text_address)) }, minLines = 2, modifier = Modifier.fillMaxWidth()) }
        item { DecimalField(previousDue, { previousDue = it }, AppText.text(R.string.text_previous_due_optional)) }
        item { OutlinedTextField(notes, { notes = it }, label = { Text(AppText.text(R.string.text_notes)) }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        existing?.let { item { Text(AppText.text(R.string.text_added_1_s, formatDate(java.time.Instant.ofEpochMilli(it.createdAt).atZone(java.time.ZoneId.systemDefault()).toLocalDate().toEpochDay())), style = MaterialTheme.typography.bodySmall) } }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_cancel)) }
                Button(
                    onClick = {
                        onSave(
                            CustomerEntity(
                                id = existing?.id ?: 0,
                                name = name,
                                nickname = nickname,
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
                ) { Text(AppText.text(R.string.text_save_customer)) }
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
    measurement: com.harvestpay.app.measurement.MeasurementDraft? = null,
    onViewBoundary: () -> Unit = {},
) {
    var name by androidx.compose.runtime.saveable.rememberSaveable(customerId, existing?.id, measurement?.measuredAt) { mutableStateOf(existing?.fieldName.orEmpty()) }
    var number by androidx.compose.runtime.saveable.rememberSaveable(customerId, existing?.id, measurement?.measuredAt) { mutableStateOf(existing?.fieldNumber.orEmpty()) }
    var location by androidx.compose.runtime.saveable.rememberSaveable(customerId, existing?.id, measurement?.measuredAt) { mutableStateOf(existing?.location.orEmpty()) }
    var size by androidx.compose.runtime.saveable.rememberSaveable(customerId, existing?.id, measurement?.measuredAt) { mutableStateOf(existing?.sizeBigha?.toString() ?: measurement?.factorUsed?.let { com.harvestpay.app.measurement.MeasurementFormatting.bighaInput(measurement.areaSquareMeters, it) }.orEmpty()) }
    var notes by androidx.compose.runtime.saveable.rememberSaveable(customerId, existing?.id, measurement?.measuredAt) { mutableStateOf(existing?.notes.orEmpty()) }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(if (existing == null) AppText.text(R.string.text_add_field) else AppText.text(R.string.text_edit_field), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
        item { OutlinedTextField(name, { name = it }, label = { Text(AppText.text(R.string.text_field_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(number, { number = it }, label = { Text(AppText.text(R.string.text_field_number)) }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(location, { location = it }, label = { Text(AppText.text(R.string.text_location)) }, modifier = Modifier.fillMaxWidth()) }
        item { DecimalField(size, { size = it }, AppText.text(R.string.text_size_in_bigha)) }
        if (measurement != null && (size.toDoubleOrNull() ?: 0.0) <= 0.0) item { Text(AppText.text(R.string.text_this_small_measurement_rounds_to_0_00_bigha_enter_a_pos), color = MaterialTheme.colorScheme.error) }
        if (measurement != null) item { Text(AppText.text(R.string.text_1_s_acres_2_s_bigha_per_acre_you_can_edit_the_field_siz, com.harvestpay.app.measurement.MeasurementFormatting.display(measurement.acres), measurement.factorUsed)) }
        if (existing != null && existing.boundaryCoordinates != "[]") item { OutlinedButton(onClick = onViewBoundary) { Text(AppText.text(R.string.text_view_field_boundary)) } }
        item { OutlinedTextField(notes, { notes = it }, label = { Text(AppText.text(R.string.text_notes)) }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_cancel)) }
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
                                measurementMethod = existing?.measurementMethod ?: measurement?.method?.name ?: "MANUAL",
                                measuredAreaSquareMeters = existing?.measuredAreaSquareMeters ?: measurement?.areaSquareMeters,
                                measuredAreaAcres = existing?.measuredAreaAcres ?: measurement?.acres,
                                measurementBighaPerAcre = existing?.measurementBighaPerAcre ?: measurement?.factorUsed,
                                measuredAt = existing?.measuredAt ?: measurement?.measuredAt,
                                boundaryCoordinates = existing?.boundaryCoordinates ?: measurement?.let { com.harvestpay.app.measurement.encodeBoundary(it.points) } ?: "[]",
                                originalGpsTrack = existing?.originalGpsTrack ?: measurement?.let { com.harvestpay.app.measurement.encodeBoundary(it.originalTrack) } ?: "[]",
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                ) { Text(AppText.text(R.string.text_save_field)) }
            }
        }
    }
}

@Composable
fun CustomerProfileScreen(
    summary: CustomerSummary?,
    uiState: HarvestUiState,
    settings: AppSettings,
    onEdit: () -> Unit,
    onAddField: () -> Unit,
    onEditField: (Long) -> Unit,
    onDeleteField: (FieldEntity) -> Unit,
    onAddWork: () -> Unit,
    onAddPayment: () -> Unit,
    onDeleteWork: (com.harvestpay.app.data.WorkEntryEntity) -> Unit,
    onEditWork: (Long) -> Unit,
    onDeletePayment: (PaymentEntity) -> Unit,
    onOpenArchivedYear: (Int) -> Unit,
) {
    if (summary == null) {
        EmptyState(AppText.text(R.string.text_customer_not_found), AppText.text(R.string.text_this_customer_may_have_been_deleted))
        return
    }
    val context = LocalContext.current
    var tab by remember { mutableIntStateOf(0) }
    var deleteField by remember { mutableStateOf<FieldEntity?>(null) }
    var deleteWork by remember { mutableStateOf<WorkSummary?>(null) }
    var deletePayment by remember { mutableStateOf<PaymentEntity?>(null) }
    var receiptToSave by remember { mutableStateOf<ReceiptData?>(null) }
    var showReportType by remember { mutableStateOf(false) }
    var showPdfLanguage by remember { mutableStateOf(false) }
    var showDateRange by remember { mutableStateOf(false) }
    var showArchivedYear by remember { mutableStateOf(false) }
    var reportSelection by remember { mutableStateOf<CustomerReportSelection?>(null) }
    var fromDate by remember { mutableStateOf(LocalDate.now().withDayOfYear(1).toEpochDay()) }
    var toDate by remember { mutableStateOf(LocalDate.now().toEpochDay()) }
    val saveReceipt = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val data = receiptToSave
        if (uri != null && data != null) context.contentResolver.openOutputStream(uri)?.use { ReceiptGenerator.writePdf(context, data, it) }
        receiptToSave = null
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(summary.customer.appDisplayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            if (summary.customer.mobile.isNotBlank()) {
                                Text(summary.customer.mobile, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (summary.customer.village.isNotBlank()) Text(summary.customer.village)
                            if (summary.customer.address.isNotBlank()) Text(summary.customer.address, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, AppText.text(R.string.text_edit_customer)) }
                    }
                    if (summary.customer.mobile.isNotBlank()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(onClick = { ShareUtils.dial(context, summary.customer.mobile) }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Outlined.Call, null); Text(AppText.text(R.string.text_call))
                            }
                            FilledTonalButton(
                                onClick = { ShareUtils.openWhatsApp(context, summary.customer.mobile, ShareUtils.paymentMessage(summary)) },
                                modifier = Modifier.weight(1f),
                            ) { Icon(Icons.Outlined.Chat, null); Text(AppText.text(R.string.text_whatsapp)) }
                        }
                    }
                    OutlinedButton(
                        onClick = { showReportType = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.PictureAsPdf, contentDescription = null)
                        Text(AppText.text(R.string.text_share_pdf))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(AppText.text(R.string.text_total_bill), fontWeight = FontWeight.SemiBold)
                        Text(formatMoney(summary.totalBill), fontWeight = FontWeight.Bold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(AppText.text(R.string.text_pending_amount), fontWeight = FontWeight.SemiBold)
                        Text(formatMoney(summary.pending), fontWeight = FontWeight.ExtraBold,
                            color = if (summary.pending > 0.005) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(AppText.text(R.string.text_account_status), fontWeight = FontWeight.Bold)
                Text(AppText.label(if (summary.pending > 0.005) "Unpaid" else "Settled"), color = if (summary.pending > 0.005) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            } }
        }
        item {
            MetricGrid(
                listOf(
                    AppText.text(R.string.text_fields) to summary.fields.size.toString(),
                    AppText.text(R.string.text_registered_bigha) to formatBigha(summary.registeredBigha),
                    AppText.text(R.string.text_opening_outstanding) to formatMoney(summary.previousDue),
                    AppText.text(R.string.text_opening_advance) to formatMoney(summary.openingAdvance),
                    AppText.label("Paid") to formatMoney(summary.totalPaid),
                    AppText.text(R.string.text_advance_credit) to formatMoney(summary.advance),
                    AppText.text(R.string.text_bigha_ploughed) to formatBigha(summary.totalBigha),
                ),
            )
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onAddField, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_add_field)) }
                OutlinedButton(onClick = onAddWork, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_add_work)) }
                Button(onClick = onAddPayment, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_payment)) }
            }
        }
        item {
            val tabs = listOf(AppText.text(R.string.text_fields), AppText.text(R.string.text_work), AppText.text(R.string.text_payments), AppText.text(R.string.text_archived))
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { index, title -> Tab(selected = tab == index, onClick = { tab = index }, text = { Text(title) }) }
            }
        }
        when (tab) {
            0 -> {
                if (summary.fields.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_fields), AppText.text(R.string.text_add_this_customer_s_first_field)) }
                else items(summary.fields, key = { "field-${it.id}" }) { field ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(field.fieldName, fontWeight = FontWeight.SemiBold)
                                Text(AppText.text(R.string.text_1_s_bigha, com.harvestpay.app.measurement.MeasurementFormatting.display(field.sizeBigha)) + if (field.location.isNotBlank()) AppText.text(R.string.text_1_s, field.location) else "")
                                if (field.notes.isNotBlank()) Text(field.notes, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onEditField(field.id) }) { Icon(Icons.Outlined.Edit, AppText.text(R.string.text_edit_field)) }
                            IconButton(onClick = { deleteField = field }) { Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete_field), tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
            1 -> {
                if (summary.work.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_work_history), AppText.text(R.string.text_record_a_ploughing_job_for_this_customer)) }
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
                        onEdit = { onEditWork(work.work.id) },
                    )
                }
            }
            2 -> {
                if (summary.previousDue > 0.005) item {
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(AppText.text(R.string.text_opening_previous_due), fontWeight = FontWeight.SemiBold)
                                Text(AppText.text(R.string.text_balance_brought_forward), style = MaterialTheme.typography.bodySmall)
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
                    EmptyState(AppText.text(R.string.text_no_payments), AppText.text(R.string.text_recorded_payments_will_appear_below_the_opening_balance))
                } else items(summary.payments.sortedByDescending { it.paymentDate }, key = { "payment-${it.id}" }) { payment ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(formatMoney(payment.amount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(AppText.text(R.string.text_1_s_2_s, formatDate(payment.paymentDate), payment.paymentMethod))
                                if (payment.notes.isNotBlank()) Text(payment.notes, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { deletePayment = payment }) { Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete_payment), tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
            3 -> {
                if (summary.archivedYears.isEmpty()) item { EmptyState(AppText.text(R.string.text_no_archived_history), AppText.text(R.string.text_completed_account_years_will_appear_here_after_archivin)) }
                else items(summary.archivedYears, key = { "archive-$it" }) { year ->
                    Card(onClick = { onOpenArchivedYear(year) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text(AppText.text(R.string.text_1_s_account, year), fontWeight = FontWeight.Bold); Text(AppText.text(R.string.text_read_only_historical_account), style = MaterialTheme.typography.bodySmall) }
                            Text(AppText.text(R.string.text_view), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }

    deleteField?.let { field ->
        ConfirmDialog(AppText.text(R.string.text_delete_field_39d219), AppText.text(R.string.text_work_history_will_keep_its_financial_record_but_this_fi), AppText.text(R.string.text_delete), true, { onDeleteField(field) }, { deleteField = null })
    }
    deleteWork?.let { work ->
        ConfirmDialog(AppText.text(R.string.text_delete_work_entry), AppText.text(R.string.text_linked_payments_will_remain_in_the_customer_ledger_but), AppText.text(R.string.text_delete), true, { onDeleteWork(work.work) }, { deleteWork = null })
    }
    deletePayment?.let { payment ->
        ConfirmDialog(AppText.text(R.string.text_delete_payment_522c64), AppText.text(R.string.text_the_pending_balance_will_increase_by_1_s, formatMoney(payment.amount)), AppText.text(R.string.text_delete), true, { onDeletePayment(payment) }, { deletePayment = null })
    }
    if (showReportType) {
        AlertDialog(
            onDismissRequest = { showReportType = false },
            title = { Text(AppText.text(R.string.text_select_report_type)) },
            text = { Column {
                CustomerReportType.entries.filter { it != CustomerReportType.OUTSTANDING_ACCOUNT }.forEach { type ->
                    TextButton(onClick = {
                        showReportType = false
                        when (type) {
                            CustomerReportType.CUSTOM_DATE_RANGE -> showDateRange = true
                            CustomerReportType.ARCHIVED_YEAR -> showArchivedYear = true
                            else -> { reportSelection = CustomerReportSelection(type); showPdfLanguage = true }
                        }
                    }) { Text(AppText.label(type.title), modifier = Modifier.fillMaxWidth()) }
                }
            } },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showReportType = false }) { Text(AppText.text(R.string.text_cancel)) } },
        )
    }
    if (showDateRange) {
        AlertDialog(
            onDismissRequest = { showDateRange = false },
            title = { Text(AppText.text(R.string.text_custom_date_range)) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField(AppText.text(R.string.text_from_date), fromDate, { fromDate = it })
                DateField(AppText.text(R.string.text_to_date), toDate, { toDate = it })
                if (fromDate > toDate) Text(AppText.text(R.string.text_from_date_must_be_on_or_before_to_date), color = MaterialTheme.colorScheme.error)
            } },
            confirmButton = { TextButton(enabled = fromDate <= toDate, onClick = {
                reportSelection = CustomerReportSelection(CustomerReportType.CUSTOM_DATE_RANGE, fromDate, toDate)
                showDateRange = false; showPdfLanguage = true
            }) { Text(AppText.text(R.string.text_continue)) } },
            dismissButton = { TextButton(onClick = { showDateRange = false }) { Text(AppText.text(R.string.text_cancel)) } },
        )
    }
    if (showArchivedYear) {
        AlertDialog(
            onDismissRequest = { showArchivedYear = false },
            title = { Text(AppText.text(R.string.text_select_archived_year)) },
            text = { Column {
                if (summary.archivedYears.isEmpty()) Text(AppText.text(R.string.text_no_archived_account_years_are_available))
                summary.archivedYears.forEach { year -> TextButton(onClick = {
                    reportSelection = CustomerReportSelection(CustomerReportType.ARCHIVED_YEAR, archivedYear = year)
                    showArchivedYear = false; showPdfLanguage = true
                }) { Text(year.toString(), modifier = Modifier.fillMaxWidth()) } }
            } },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showArchivedYear = false }) { Text(AppText.text(R.string.text_cancel)) } },
        )
    }
    if (showPdfLanguage) {
        AlertDialog(
            onDismissRequest = { showPdfLanguage = false }, title = { Text(AppText.text(R.string.text_select_pdf_language)) },
            text = { Column { listOf(PdfLanguage.ENGLISH to "English", PdfLanguage.HINDI to "हिंदी", PdfLanguage.BOTH to AppText.text(R.string.text_both)).forEach { (language, label) -> TextButton(onClick = {
                val selection = reportSelection ?: CustomerReportSelection(CustomerReportType.CURRENT_ACCOUNT)
                showPdfLanguage = false
                ReceiptGenerator.shareCustomer(context, CustomerReportFactory.create(uiState, summary, selection), settings, language)
            }) { Text(label, modifier = Modifier.fillMaxWidth()) } } } },
            confirmButton = {}, dismissButton = { TextButton(onClick = { showPdfLanguage = false }) { Text(AppText.text(R.string.text_cancel)) } },
        )
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
    onEdit: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(work.field?.fieldName ?: AppText.text(R.string.text_field), fontWeight = FontWeight.Bold)
                Text(formatMoney(work.work.totalAmount), fontWeight = FontWeight.Bold)
            }
            Text(if (work.work.billingMode == BillingType.PER_MINUTE) AppText.text(R.string.text_1_s_2_s_3_s_min_4_s_min, formatDate(work.work.workDate), work.work.workType, work.work.durationMinutes, formatMoney(work.work.ratePerBigha)) else AppText.text(R.string.text_1_s_2_s_field_size_3_s_bigha_round_4_s_work_done_5_s_bi, formatDate(work.work.workDate), work.work.workType, formatBigha(work.work.sizeBigha), formatBigha(work.work.roundMultiplier), formatBigha(BusinessCalculator.billableBigha(work.work)), formatMoney(work.work.ratePerBigha)))
            Text(AppText.text(R.string.text_paid_1_s_pending_2_s, formatMoney(work.paid), formatMoney(work.pending)), style = MaterialTheme.typography.bodySmall)
            if (payments.isNotEmpty()) {
                Text(
                    payments.joinToString(prefix = AppText.text(R.string.text_payments_eaa085), separator = " • ") {
                        AppText.text(R.string.text_1_s_2_s_3_s, formatMoney(it.amount), it.paymentMethod, formatDate(it.paymentDate))
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (work.work.notes.isNotBlank()) Text(work.work.notes, style = MaterialTheme.typography.bodySmall)
            AssistChip(onClick = {}, label = { Text(AppText.label(work.status.name)) })
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalButton(onClick = onMessage) {
                    Icon(Icons.Outlined.Chat, contentDescription = null)
                    Text(AppText.text(R.string.text_message))
                }
                IconButton(onClick = onShareReceipt) { Icon(Icons.Outlined.Share, AppText.text(R.string.text_share_receipt)) }
                IconButton(onClick = onSavePdf) { Icon(Icons.Outlined.PictureAsPdf, AppText.text(R.string.text_save_pdf)) }
                IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, AppText.text(R.string.text_edit_work)) }
                IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete_work), tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
