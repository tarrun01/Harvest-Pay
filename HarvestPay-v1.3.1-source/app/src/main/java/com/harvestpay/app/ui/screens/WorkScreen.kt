package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.*
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatMoney
import com.harvestpay.app.util.todayEpochDay

@Composable
fun WorkFormScreen(
    uiState: HarvestUiState, settings: AppSettings, preselectedCustomerId: Long?,
    existingWork: WorkEntryEntity? = null,
    onSave: (WorkEntryEntity, Double, String) -> Unit, onCancel: (() -> Unit)? = null,
) {
    var customer by remember(existingWork) { mutableStateOf<CustomerEntity?>(null) }
    var field by remember(existingWork) { mutableStateOf<FieldEntity?>(null) }
    var date by remember(existingWork) { mutableLongStateOf(existingWork?.workDate ?: todayEpochDay()) }
    var size by rememberSaveable(existingWork) { mutableStateOf(existingWork?.sizeBigha?.takeIf { it > 0 }?.toString().orEmpty()) }
    var duration by remember(existingWork) { mutableStateOf(existingWork?.durationMinutes?.takeIf { it > 0 }?.toString().orEmpty()) }
    var rate by rememberSaveable(existingWork, settings.defaultRate) { mutableStateOf((existingWork?.ratePerBigha ?: settings.defaultRate).toString()) }
    var round by remember(existingWork) { mutableDoubleStateOf(existingWork?.roundMultiplier ?: 1.0) }
    var workTypeName by remember(existingWork) { mutableStateOf(existingWork?.workType ?: "Ploughing") }
    var discount by remember(existingWork) { mutableStateOf(existingWork?.discount?.takeIf { it > 0 }?.toString().orEmpty()) }
    var extras by remember(existingWork) { mutableStateOf(existingWork?.extraCharges?.takeIf { it > 0 }?.toString().orEmpty()) }
    var initialPaid by rememberSaveable { mutableStateOf("") }; var method by remember { mutableStateOf("Cash") }
    var notes by rememberSaveable(existingWork) { mutableStateOf(existingWork?.notes.orEmpty()) }
    var initializedType by remember(existingWork) { mutableStateOf(existingWork != null) }

    LaunchedEffect(preselectedCustomerId, existingWork, uiState.customers, uiState.fields) {
        val targetId = existingWork?.customerId ?: preselectedCustomerId
        if (customer == null && targetId != null) customer = uiState.customers.firstOrNull { it.id == targetId }
        if (field == null && existingWork?.fieldId != null) field = uiState.fields.firstOrNull { it.id == existingWork.fieldId }
    }
    val workTypeOptions = uiState.workTypes.ifEmpty { listOf(WorkTypeEntity(name = "Ploughing", hindiName = "जुताई", ratePerBigha = settings.defaultRate)) }
    LaunchedEffect(workTypeOptions) {
        if (!initializedType && workTypeOptions.isNotEmpty()) {
            val initial = workTypeOptions.firstOrNull { it.name == workTypeName } ?: workTypeOptions.first()
            workTypeName = initial.name; rate = initial.ratePerBigha.toString(); initializedType = true
        }
    }
    val selectedWorkType = workTypeOptions.firstOrNull { it.name == workTypeName }
    val billingType = selectedWorkType?.billingMode ?: existingWork?.billingMode ?: BillingType.PER_BIGHA
    val customerFields = uiState.fields.filter { it.customerId == customer?.id }
    val numericSize = size.toDoubleOrNull() ?: 0.0; val numericDuration = duration.toIntOrNull() ?: 0; val numericRate = rate.toDoubleOrNull() ?: 0.0
    val subtotal = BusinessCalculator.subtotal(billingType, numericSize, round, numericDuration, numericRate)
    val finalAmount = BusinessCalculator.finalAmount(subtotal, discount.toDoubleOrNull() ?: 0.0, extras.toDoubleOrNull() ?: 0.0)
    val paid = initialPaid.toDoubleOrNull() ?: 0.0
    val customerSummary = uiState.customerSummaries.firstOrNull { it.customer.id == customer?.id }
    val projectedBill = (customerSummary?.totalBill ?: 0.0) - (existingWork?.totalAmount ?: 0.0) + finalAmount
    val projectedPaid = (customerSummary?.totalPaid ?: 0.0) + paid
    val projectedPending = BusinessCalculator.pending(projectedBill, projectedPaid)
    val projectedAdvance = BusinessCalculator.money((projectedPaid - projectedBill).coerceAtLeast(0.0))
    val validBilling = if (billingType == BillingType.PER_BIGHA) numericSize > 0 && BusinessCalculator.isSupportedRound(round) else numericDuration > 0

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Column(Modifier.padding(top = 14.dp)) {
            Text(if (existingWork == null) AppText.text(R.string.text_new_work_entry) else AppText.text(R.string.text_edit_work_entry), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(AppText.text(R.string.text_the_billing_fields_change_automatically_with_the_select), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }
        item { DropdownField(AppText.text(R.string.text_customer_6e2808), customer, uiState.customers.sortedBy { it.name }, { listOf(it.appDisplayName, it.mobile).filter(String::isNotBlank).joinToString(" • ") }, { customer = it; field = null; size = "" }, enabled = existingWork == null) }
        item { DropdownField(AppText.text(R.string.text_field_optional), field, customerFields, { AppText.text(R.string.text_1_s_2_s_bigha, it.fieldName, formatBigha(it.sizeBigha)) }, { selected -> field = selected; if (billingType == BillingType.PER_BIGHA) size = selected.sizeBigha.toString() }, enabled = customer != null && customerFields.isNotEmpty()) }
        item { DateField(AppText.text(R.string.text_work_date), date, { date = it }) }
        item { DropdownField(AppText.text(R.string.text_work_type), selectedWorkType, workTypeOptions, { AppText.bilingualName(it.name, it.hindiName) }, { selected -> workTypeName = selected.name; rate = selected.ratePerBigha.toString(); if (selected.billingMode == BillingType.PER_BIGHA && field != null) size = field!!.sizeBigha.toString() }) }
        if (billingType == BillingType.PER_BIGHA) {
            item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { DecimalField(size, { size = it }, AppText.text(R.string.text_field_size_bigha), Modifier.weight(1f)); DecimalField(rate, { rate = it }, AppText.text(R.string.text_rate_bigha), Modifier.weight(1f), "₹") } }
            item { DropdownField(AppText.text(R.string.text_round), round, BusinessCalculator.supportedRounds, { formatBigha(it) }, { round = it }) }
        } else item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { DecimalField(duration, { duration = it.substringBefore('.') }, AppText.text(R.string.text_duration_minutes), Modifier.weight(1f)); DecimalField(rate, { rate = it }, AppText.text(R.string.text_rate_minute), Modifier.weight(1f), "₹") } }
        item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (billingType == BillingType.PER_BIGHA) SummaryRow(AppText.text(R.string.text_work_done), AppText.text(R.string.text_1_s_bigha, formatBigha(BusinessCalculator.workDoneBigha(numericSize, round)))) else SummaryRow(AppText.text(R.string.text_duration), AppText.text(R.string.text_1_s_min, numericDuration))
            SummaryRow(AppText.text(R.string.text_final_amount), formatMoney(finalAmount), true)
        } } }
        if (existingWork == null) item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { DecimalField(initialPaid, { initialPaid = it }, AppText.text(R.string.text_amount_paid_now), Modifier.weight(1f), "₹"); DropdownField(AppText.text(R.string.text_method), method, listOf("Cash", "UPI", "Bank Transfer", "Other"), { AppText.label(it) }, { method = it }, Modifier.weight(1f)) } }
        item { Text(if (projectedAdvance > 0.005) AppText.text(R.string.text_customer_account_after_save_1_s_advance, formatMoney(projectedAdvance)) else AppText.text(R.string.text_customer_account_after_save_1_s_pending, formatMoney(projectedPending)), color = if (projectedAdvance > 0.005 || projectedPending <= 0.005) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold) }
        item { OutlinedTextField(notes, { notes = it }, label = { Text(AppText.text(R.string.text_notes)) }, minLines = 1, maxLines = 2, modifier = Modifier.fillMaxWidth()) }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            onCancel?.let { OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_cancel)) } }
            Button(onClick = {
                val c = customer ?: return@Button
                onSave(WorkEntryEntity(
                    id = existingWork?.id ?: 0, customerId = c.id, fieldId = field?.id, workDate = date,
                    sizeBigha = if (billingType == BillingType.PER_BIGHA) numericSize else (field?.sizeBigha ?: existingWork?.sizeBigha ?: 0.0),
                    ratePerBigha = numericRate, billingType = billingType.name, durationMinutes = if (billingType == BillingType.PER_MINUTE) numericDuration else 0,
                    rounds = if (billingType == BillingType.PER_BIGHA) round.toInt().coerceAtLeast(1) else 1, roundMultiplier = if (billingType == BillingType.PER_BIGHA) round else 1.0,
                    workType = workTypeName, workTypeHindi = selectedWorkType?.hindiName ?: existingWork?.workTypeHindi.orEmpty(), subtotal = subtotal,
                    discount = discount.toDoubleOrNull() ?: 0.0, extraCharges = extras.toDoubleOrNull() ?: 0.0, totalAmount = finalAmount, notes = notes,
                    createdAt = existingWork?.createdAt ?: System.currentTimeMillis(),
                ), paid, method)
            }, enabled = customer != null && validBilling && numericRate >= 0, modifier = Modifier.weight(1f)) { Text(if (existingWork == null) AppText.text(R.string.text_save_work) else AppText.text(R.string.text_save_changes)) }
        } }
        item { Spacer(Modifier.height(42.dp)) }
    }
}

@Composable private fun SummaryRow(label: String, value: String, emphasized: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal)
        Text(value, style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge, fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium)
    }
}
