package com.harvestpay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.data.FieldEntity
import com.harvestpay.app.data.WorkEntryEntity
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.util.formatMoney
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
    var rounds by remember { mutableStateOf("1") }
    var workType by remember { mutableStateOf("Ploughing") }
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
    val customerFields = uiState.fields.filter { it.customerId == customer?.id }
    val numericSize = size.toDoubleOrNull() ?: 0.0
    val numericRate = rate.toDoubleOrNull() ?: 0.0
    val numericRounds = rounds.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val subtotal = BusinessCalculator.subtotal(numericSize, numericRate, numericRounds)
    val finalAmount = BusinessCalculator.finalAmount(
        subtotal,
        discount.toDoubleOrNull() ?: 0.0,
        extras.toDoubleOrNull() ?: 0.0,
    )
    val paid = initialPaid.toDoubleOrNull() ?: 0.0
    val status = BusinessCalculator.status(finalAmount, paid).name.replace('_', ' ')
    val presets = settings.ratePresets.split(',').mapNotNull { it.trim().toDoubleOrNull() }.distinct()

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 14.dp)) {
                Text("New ploughing entry", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Charges calculate automatically as you type.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            DropdownField(
                "Customer *",
                customer,
                uiState.customers.sortedBy { it.name },
                { "${it.name} • ${it.mobile}" },
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
                DecimalField(rate, { rate = it }, "Rate / Bigha *", Modifier.weight(1f), "₹")
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
                OutlinedTextField(
                    rounds,
                    { if (it.length <= 2 && it.all(Char::isDigit)) rounds = it },
                    label = { Text("Rounds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                DropdownField(
                    "Work type",
                    workType,
                    listOf("Ploughing", "Harrowing", "Cultivating", "Levelling", "Other"),
                    { it },
                    { workType = it },
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
                "Status: $status • Pending ${formatMoney(BusinessCalculator.pending(finalAmount, paid))}",
                color = if (paid >= finalAmount && finalAmount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
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
                                rounds = numericRounds,
                                workType = workType,
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
