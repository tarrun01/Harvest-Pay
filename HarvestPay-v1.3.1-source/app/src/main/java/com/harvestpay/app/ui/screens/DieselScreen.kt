package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.harvestpay.app.util.formatUiDate as formatDate
import com.harvestpay.app.util.formatMoney
import com.harvestpay.app.util.todayEpochDay

@Composable
fun DieselEntryScreen(
    entries: List<DieselEntryEntity>,
    totalLitres: Double,
    totalAmount: Double,
    onSave: (DieselEntryEntity, () -> Unit) -> Unit,
    onDelete: (DieselEntryEntity) -> Unit,
    initialEntryId: Long? = null,
) {
    var editing by remember { mutableStateOf<DieselEntryEntity?>(null) }
    var litres by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var date by remember { mutableLongStateOf(todayEpochDay()) }
    var status by rememberSaveable { mutableStateOf("Paid") }
    var broughtBy by rememberSaveable { mutableStateOf("") }
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

    var openedInitialEntry by remember(initialEntryId) { mutableStateOf(false) }
    LaunchedEffect(initialEntryId, entries) {
        if (!openedInitialEntry && initialEntryId != null) {
            entries.firstOrNull { it.id == initialEntryId }?.let {
                edit(it)
                openedInitialEntry = true
            }
        }
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
                    AppText.text(R.string.text_total_diesel_used_litres) to formatBigha(totalLitres),
                    AppText.text(R.string.text_total_diesel_amount_spent) to formatMoney(totalAmount),
                ),
            )
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Outlined.LocalGasStation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            if (editing == null) AppText.text(R.string.text_new_diesel_entry) else AppText.text(R.string.text_edit_diesel_entry),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    DecimalField(litres, { litres = it }, AppText.text(R.string.text_diesel_quantity_litres))
                    DecimalField(amount, { amount = it }, AppText.text(R.string.text_total_diesel_amount))
                    DateField(AppText.text(R.string.text_date), date, { date = it })
                    DropdownField(
                        label = AppText.text(R.string.text_payment_status),
                        value = status,
                        options = listOf("Paid", "Unpaid"),
                        optionLabel = { AppText.label(it) },
                        onSelected = { status = it },
                    )
                    OutlinedTextField(
                        value = broughtBy,
                        onValueChange = { broughtBy = it },
                        label = { Text(AppText.text(R.string.text_diesel_brought_by)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (editing != null) {
                            OutlinedButton(onClick = ::resetForm, modifier = Modifier.weight(1f)) { Text(AppText.text(R.string.text_cancel)) }
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
                        ) { Text(if (editing == null) AppText.text(R.string.text_save_entry) else AppText.text(R.string.text_update_entry)) }
                    }
                }
            }
        }
        item { Text(AppText.text(R.string.text_diesel_history), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        if (entries.isEmpty()) {
            item { EmptyState(AppText.text(R.string.text_no_diesel_entries), AppText.text(R.string.text_saved_diesel_purchases_will_appear_here)) }
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
                                AppText.text(R.string.text_1_s_litres_2_s, formatBigha(entry.litres), formatMoney(entry.totalAmount)),
                                fontWeight = FontWeight.Bold,
                            )
                            Text(AppText.text(R.string.text_1_s_2_s, formatDate(entry.entryDate), entry.paymentStatus))
                            Text(
                                AppText.text(R.string.text_brought_by_1_s, entry.broughtBy),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { edit(entry) }) { Icon(Icons.Outlined.Edit, AppText.text(R.string.text_edit_diesel_entry)) }
                        IconButton(onClick = { deleteTarget = entry }) {
                            Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete_diesel_entry), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }

    deleteTarget?.let { entry ->
        ConfirmDialog(
            title = AppText.text(R.string.text_delete_diesel_entry_40c58c),
            message = AppText.text(R.string.text_1_s_litres_and_2_s_will_be_removed_from_dashboard_total, formatBigha(entry.litres), formatMoney(entry.totalAmount)),
            confirmLabel = AppText.text(R.string.text_delete),
            destructive = true,
            onConfirm = {
                if (editing?.id == entry.id) resetForm()
                onDelete(entry)
            },
            onDismiss = { deleteTarget = null },
        )
    }
}
