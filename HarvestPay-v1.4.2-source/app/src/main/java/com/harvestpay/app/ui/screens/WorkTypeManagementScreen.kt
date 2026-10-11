package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.WorkTypeEntity
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.util.formatMoney

@Composable
fun WorkTypeManagementScreen(
    workTypes: List<WorkTypeEntity>,
    onSave: (WorkTypeEntity) -> Unit,
    onDelete: (WorkTypeEntity) -> Unit,
) {
    var editing by remember { mutableStateOf<WorkTypeEntity?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var hindiName by rememberSaveable { mutableStateOf("") }
    var rate by rememberSaveable { mutableStateOf("") }
    var billingType by remember { mutableStateOf(BillingType.PER_BIGHA) }
    var deleteTarget by remember { mutableStateOf<WorkTypeEntity?>(null) }

    fun resetEditor() {
        editing = null
        name = ""
        hindiName = ""
        rate = ""
        billingType = BillingType.PER_BIGHA
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(AppText.text(R.string.text_work_type_management), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    AppText.text(R.string.text_save_english_and_hindi_names_the_rate_and_whether_billi),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (editing == null) AppText.text(R.string.text_add_work_type) else AppText.text(R.string.text_edit_1_s, editing?.name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(AppText.text(R.string.text_english_name)) },
                        placeholder = { Text(AppText.text(R.string.text_example_water_pump)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(hindiName, { hindiName = it }, label = { Text(AppText.text(R.string.text_hindi_name)) }, placeholder = { Text(AppText.text(R.string.work_type_hindi_example)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    DecimalField(rate, { rate = it }, AppText.text(R.string.text_rate), prefix = "₹")
                    DropdownField(AppText.text(R.string.text_billing_type), billingType, BillingType.entries, { if (it == BillingType.PER_BIGHA) AppText.text(R.string.text_per_bigha) else AppText.text(R.string.text_per_minute) }, { billingType = it })
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AnimatedVisibility(
                            visible = editing != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedButton(onClick = ::resetEditor, modifier = Modifier.fillMaxWidth()) {
                                Text(AppText.text(R.string.text_cancel_edit))
                            }
                        }
                        Button(
                            onClick = {
                                val existing = editing
                                onSave(
                                    WorkTypeEntity(
                                        id = existing?.id ?: 0,
                                        name = name.trim(),
                                        ratePerBigha = rate.toDoubleOrNull() ?: 0.0,
                                        hindiName = hindiName.trim(), billingType = billingType.name,
                                        createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                                    ),
                                )
                                resetEditor()
                            },
                            enabled = name.isNotBlank() && (rate.toDoubleOrNull() ?: -1.0) >= 0,
                            modifier = Modifier.weight(1f),
                        ) { Text(if (editing == null) AppText.text(R.string.text_add_type) else AppText.text(R.string.text_save_changes)) }
                    }
                }
            }
        }
        item { HorizontalDivider() }
        if (workTypes.isEmpty()) {
            item { EmptyState(AppText.text(R.string.text_no_work_types), AppText.text(R.string.text_add_the_first_work_type_and_its_default_rate_above)) }
        } else {
            items(workTypes, key = { it.id }) { workType ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(workType.name, fontWeight = FontWeight.Bold)
                            if (workType.hindiName.isNotBlank()) Text(workType.hindiName)
                            Text(
                                AppText.text(R.string.text_1_s_2_s_5935d7, formatMoney(workType.ratePerBigha), if (workType.billingMode == BillingType.PER_BIGHA) AppText.text(R.string.text_bigha) else AppText.text(R.string.text_minute)),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = {
                            editing = workType
                            name = workType.name
                            hindiName = workType.hindiName
                            rate = workType.ratePerBigha.toString()
                            billingType = workType.billingMode
                        }) { Icon(Icons.Outlined.Edit, AppText.text(R.string.text_edit_1_s, workType.name)) }
                        IconButton(onClick = { deleteTarget = workType }) {
                            Icon(Icons.Outlined.Delete, AppText.text(R.string.text_delete_1_s, workType.name), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }

    deleteTarget?.let { target ->
        ConfirmDialog(
            title = AppText.text(R.string.text_delete_1_s_137cdc, target.name),
            message = AppText.text(R.string.text_existing_work_history_keeps_its_saved_work_type_and_rat),
            confirmLabel = AppText.text(R.string.text_delete_type),
            destructive = true,
            onConfirm = { onDelete(target) },
            onDismiss = { deleteTarget = null },
        )
    }
}
