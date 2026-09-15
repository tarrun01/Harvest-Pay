package com.harvestpay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.data.WorkTypeEntity
import com.harvestpay.app.data.appDisplayName
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.util.formatMoney
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.todayEpochDay

@Composable
fun WorkFormScreen(
    uiState: HarvestUiState,
    settings: AppSettings,
    preselectedCustomerId: Long?,
    onSave: (WorkEntryEntity, Double, String) -> Unit,
    onCancel: (() -> Unit)? = null,
) {
    var customer by remember { mutableStateOf<CustomerEntity?>(null) }
    var field by remember { mutableStateOf<FieldEntity?>(null) }
    var date by remember { mutableLongStateOf(todayEpochDay()) }
    var size by remember { mutableStateOf("") }
    var rate by remember(settings.defaultRate) { mutableStateOf(settings.defaultRate.toString()) }
    var round by remember { mutableDoubleStateOf(1.0) }
    var workTypeName by remember { mutableStateOf("Ploughing") }
    var appliedInitialWorkTypeRate by remember { mutableStateOf(false) }
    var discount by remember { mutableStateOf("") }
    var extras by remember { mutableStateOf("") }
    var initialPaid by remember { mutableStateOf("") }
    var method by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(preselectedCustomerId, uiState.customers) {
        if (customer == null && preselectedCustomerId != null) {
            customer = uiState.customers.firstOrNull { it.id == preselectedCustomerId }
        }
    }
    LaunchedEffect(uiState.workTypes) {
        if (!appliedInitialWorkTypeRate && uiState.workTypes.isNotEmpty()) {
            val initialType = uiState.workTypes.firstOrNull { it.name == workTypeName }
                ?: uiState.workTypes.first()
            workTypeName = initialType.name
            rate = initialType.ratePerBigha.toString()
            appliedInitialWorkTypeRate = true
        }
    }
    val customerFields = uiState.fields.filter { it.customerId == customer?.id }
    val workTypeOptions = uiState.workTypes.ifEmpty {
        listOf(WorkTypeEntity(name = "Ploughing", ratePerBigha = settings.defaultRate))
    }
    val selectedWorkType = workTypeOptions.firstOrNull { it.name == workTypeName }
        ?: workTypeOptions.firstOrNull()
    val numericSize = size.toDoubleOrNull() ?: 0.0
    val numericRate = rate.toDoubleOrNull() ?: 0.0
    val workDoneBigha = BusinessCalculator.workDoneBigha(numericSize, round)
    val subtotal = BusinessCalculator.subtotal(numericSize, numericRate, round)
    val finalAmount = BusinessCalculator.finalAmount(
        subtotal,
        discount.toDoubleOrNull() ?: 0.0,
        extras.toDoubleOrNull() ?: 0.0,
    )
    val paid = initialPaid.toDoubleOrNull() ?: 0.0
    val customerSummary = uiState.customerSummaries.firstOrNull { it.customer.id == customer?.id }
    val projectedBill = (customerSummary?.totalBill ?: 0.0) + finalAmount
    val projectedPaid = (customerSummary?.totalPaid ?: 0.0) + paid
    val projectedPending = BusinessCalculator.pending(projectedBill, projectedPaid)
    val projectedAdvance = BusinessCalculator.money((projectedPaid - projectedBill).coerceAtLeast(0.0))
    val presets = settings.ratePresets.split(',').mapNotNull { it.trim().toDoubleOrNull() }.distinct()

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 14.dp)) {
                Text("New work entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Choose a work type for its saved rate, then adjust the rate if needed.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            DropdownField(
                "Customer *",
                customer,
                uiState.customers.sortedBy { it.name },
                { listOf(it.appDisplayName, it.mobile).filter(String::isNotBlank).joinToString(" • ") },
                {
                    customer = it
                    field = null
                    size = ""
                },
            )
        }
        item {
            DropdownField(
                label = if (customerFields.isEmpty()) "Field (add one in customer profile)" else "Field",
                value = field,
                options = customerFields,
                optionLabel = { "${it.fieldName} • ${it.sizeBigha} Bigha" },
                onSelected = { selected -> field = selected; size = selected.sizeBigha.toString() },
                enabled = customer != null && customerFields.isNotEmpty(),
            )
        }
        item { DateField("Work date", date, { date = it }) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DecimalField(size, { size = it }, "Size (Bigha) *", Modifier.weight(1f))
                DecimalField(rate, { rate = it }, "Rate / Bigha / Round *", Modifier.weight(1f), "₹")
            }
        }
        if (presets.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Rate presets", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.take(4).forEach { preset ->
                            AssistChip(onClick = { rate = preset.toString() }, label = { Text(formatMoney(preset)) })
                        }
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DropdownField(
                    "Round *",
                    round,
                    BusinessCalculator.supportedRounds,
                    { formatBigha(it) },
                    { round = it },
                    Modifier.weight(1f),
                )
                DropdownField(
                    "Work type *",
                    selectedWorkType,
                    workTypeOptions,
                    { it.name },
                    { selected ->
                        workTypeName = selected.name
                        rate = selected.ratePerBigha.toString()
                    },
                    Modifier.weight(1f),
                )
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DecimalField(discount, { discount = it }, "Discount", Modifier.weight(1f), "₹")
                DecimalField(extras, { extras = it }, "Extra charges", Modifier.weight(1f), "₹")
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryRow("Work done", "${formatBigha(workDoneBigha)} Bigha")
                    SummaryRow("Subtotal", formatMoney(subtotal))
                    SummaryRow("Discount", "− ${formatMoney(discount.toDoubleOrNull() ?: 0.0)}")
                    SummaryRow("Extra charges", "+ ${formatMoney(extras.toDoubleOrNull() ?: 0.0)}")
                    androidx.compose.material3.HorizontalDivider()
                    SummaryRow("Final amount", formatMoney(finalAmount), true)
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DecimalField(initialPaid, { initialPaid = it }, "Amount paid now", Modifier.weight(1f), "₹")
                DropdownField(
                    "Method",
                    method,
                    listOf("Cash", "UPI", "Bank Transfer", "Other"),
                    { it },
                    { method = it },
                    Modifier.weight(1f),
                )
            }
        }
        item {
            Text(
                if (projectedAdvance > 0.005) {
                    "Customer account after save: ${formatMoney(projectedAdvance)} advance"
                } else {
                    "Customer account after save: ${formatMoney(projectedPending)} pending"
                },
                color = if (projectedAdvance > 0.005 || projectedPending <= 0.005) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                fontWeight = FontWeight.SemiBold,
            )
        }
        item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, minLines = 3, modifier = Modifier.fillMaxWidth()) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                onCancel?.let { OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Cancel") } }
                Button(
                    onClick = {
                        val selectedCustomer = customer ?: return@Button
                        onSave(
                            WorkEntryEntity(
                                customerId = selectedCustomer.id,
                                fieldId = field?.id,
                                workDate = date,
                                sizeBigha = numericSize,
                                ratePerBigha = numericRate,
                                rounds = round.toInt().coerceAtLeast(1),
                                roundMultiplier = round,
                                workType = workTypeName,
                                subtotal = subtotal,
                                discount = discount.toDoubleOrNull() ?: 0.0,
                                extraCharges = extras.toDoubleOrNull() ?: 0.0,
                                totalAmount = finalAmount,
                                notes = notes,
                            ),
                            paid,
                            method,
                        )
                    },
                    enabled = customer != null && numericSize > 0 && numericRate >= 0,
                    modifier = Modifier.weight(1f),
                ) { Text("Save work") }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphasized: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal)
        Text(
            value,
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
