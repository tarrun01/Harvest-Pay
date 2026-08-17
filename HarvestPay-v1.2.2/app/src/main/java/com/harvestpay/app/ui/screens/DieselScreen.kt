package com.harvestpay.app.ui.screens

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.DieselEntryEntity
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DateField
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.MetricGrid
import com.harvestpay.app.util.formatBigha
import com.harvestpay.app.util.formatDate
import com.harvestpay.app.util.formatMoney
import com.harvestpay.app.util.todayEpochDay

@Composable
fun DieselEntryScreen(
    entries: List<DieselEntryEntity>,
    totalLitres: Double,
    totalAmount: Double,
    onSave: (DieselEntryEntity, () -> Unit) -> Unit,
    onDelete: (DieselEntryEntity) -> Unit,
) {
    var editing by remember { mutableStateOf<DieselEntryEntity?>(null) }
    var litres by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(todayEpochDay()) }
    var status by remember { mutableStateOf("Paid") }
    var broughtBy by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<DieselEntryEntity?>(null) }

    fun resetForm() {
        editing = null
        litres = ""
        amount = ""
        date = todayEpochDay()
        status = "Paid"
        broughtBy = ""
    }

    fun edit(entry: DieselEntryEntity) {
        editing = entry
        litres = entry.litres.toString()
        amount = entry.totalAmount.toString()
        date = entry.entryDate
        status = entry.paymentStatus
        broughtBy = entry.broughtBy
    }

    val quantity = litres.toDoubleOrNull() ?: 0.0
    val cost = amount.toDoubleOrNull() ?: 0.0
    val valid = quantity > 0 && cost > 0 && broughtBy.isNotBlank()

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            MetricGrid(
                listOf(
                    "Total Diesel Used (Litres)" to formatBigha(totalLitres),
                    "Total Diesel Amount Spent" to formatMoney(totalAmount),
                ),
            )
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Outlined.LocalGasStation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            if (editing == null) "New diesel entry" else "Edit diesel entry",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    DecimalField(litres, { litres = it }, "Diesel quantity (Litres) *")
                    DecimalField(amount, { amount = it }, "Total diesel amount (₹) *")
                    DateField("Date", date, { date = it })
                    DropdownField(
                        label = "Payment status *",
                        value = status,
                        options = listOf("Paid", "Unpaid"),
                        optionLabel = { it },
                        onSelected = { status = it },
                    )
                    OutlinedTextField(
                        value = broughtBy,
                        onValueChange = { broughtBy = it },
                        label = { Text("Diesel brought by *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (editing != null) {
                            OutlinedButton(onClick = ::resetForm, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        }
                        Button(
                            onClick = {
                                val original = editing
                                onSave(
                                    DieselEntryEntity(
                                        id = original?.id ?: 0,
                                        litres = quantity,
                                        totalAmount = cost,
                                        entryDate = date,
                                        paymentStatus = status,
                                        broughtBy = broughtBy,
                                        createdAt = original?.createdAt ?: System.currentTimeMillis(),
                                    ),
                                    ::resetForm,
                                )
                            },
                            enabled = valid,
                            modifier = Modifier.weight(1f),
                        ) { Text(if (editing == null) "Save entry" else "Update entry") }
                    }
                }
            }
        }
        item { Text("Diesel history", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        if (entries.isEmpty()) {
            item { EmptyState("No diesel entries", "Saved diesel purchases will appear here.") }
        } else {
            items(entries, key = { "diesel-${it.id}" }) { entry ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                "${formatBigha(entry.litres)} Litres • ${formatMoney(entry.totalAmount)}",
                                fontWeight = FontWeight.Bold,
                            )
                            Text("${formatDate(entry.entryDate)} • ${entry.paymentStatus}")
                            Text(
                                "Brought by ${entry.broughtBy}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { edit(entry) }) { Icon(Icons.Outlined.Edit, "Edit diesel entry") }
                        IconButton(onClick = { deleteTarget = entry }) {
                            Icon(Icons.Outlined.Delete, "Delete diesel entry", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }

    deleteTarget?.let { entry ->
        ConfirmDialog(
            title = "Delete diesel entry?",
            message = "${formatBigha(entry.litres)} litres and ${formatMoney(entry.totalAmount)} will be removed from dashboard totals.",
            confirmLabel = "Delete",
            destructive = true,
            onConfirm = {
                if (editing?.id == entry.id) resetForm()
                onDelete(entry)
            },
            onDismiss = { deleteTarget = null },
        )
    }
}
