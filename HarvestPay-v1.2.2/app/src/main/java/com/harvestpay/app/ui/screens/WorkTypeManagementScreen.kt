package com.harvestpay.app.ui.screens

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
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.util.formatMoney

@Composable
fun WorkTypeManagementScreen(
    workTypes: List<WorkTypeEntity>,
    onSave: (WorkTypeEntity) -> Unit,
    onDelete: (WorkTypeEntity) -> Unit,
) {
    var editing by remember { mutableStateOf<WorkTypeEntity?>(null) }
    var name by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<WorkTypeEntity?>(null) }

    fun resetEditor() {
        editing = null
        name = ""
        rate = ""
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Work type management", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "The selected work type fills its saved rate in New Work. You can still change that rate for one job.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Card(Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (editing == null) "Add work type" else "Edit ${editing?.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Work type name *") },
                        placeholder = { Text("Example: Rotavating") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DecimalField(rate, { rate = it }, "Rate per Bigha *", prefix = "₹")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AnimatedVisibility(
                            visible = editing != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                            modifier = Modifier.weight(1f),
                        ) {
                            OutlinedButton(onClick = ::resetEditor, modifier = Modifier.fillMaxWidth()) {
                                Text("Cancel edit")
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
                                        createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                                    ),
                                )
                                resetEditor()
                            },
                            enabled = name.isNotBlank() && (rate.toDoubleOrNull() ?: -1.0) >= 0,
                            modifier = Modifier.weight(1f),
                        ) { Text(if (editing == null) "Add type" else "Save changes") }
                    }
                }
            }
        }
        item { HorizontalDivider() }
        if (workTypes.isEmpty()) {
            item { EmptyState("No work types", "Add the first work type and its default rate above.") }
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
                            Text(
                                "${formatMoney(workType.ratePerBigha)} / Bigha",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = {
                            editing = workType
                            name = workType.name
                            rate = workType.ratePerBigha.toString()
                        }) { Icon(Icons.Outlined.Edit, "Edit ${workType.name}") }
                        IconButton(onClick = { deleteTarget = workType }) {
                            Icon(Icons.Outlined.Delete, "Delete ${workType.name}", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }

    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "Delete ${target.name}?",
            message = "Existing work history keeps its saved work type and rate. This only removes it from future selections.",
            confirmLabel = "Delete type",
            destructive = true,
            onConfirm = { onDelete(target) },
            onDismiss = { deleteTarget = null },
        )
    }
}
