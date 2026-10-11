package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.HoursUpdateFrequency
import com.harvestpay.app.domain.ServiceCalculator
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable fun ServiceReminderScreen(current: AppSettings, onSave: (AppSettings) -> Unit, onMarkCompleted: (Double) -> Unit) {
    var settings by remember(current) { mutableStateOf(current) }
    var currentHours by remember(current.currentTractorHours) { mutableStateOf(current.currentTractorHours.toString()) }
    var interval by remember(current.serviceIntervalHours) { mutableStateOf(current.serviceIntervalHours.toString()) }
    var lastService by remember(current.lastServiceHours) { mutableStateOf(current.lastServiceHours.toString()) }
    var completionDialog by remember { mutableStateOf(false) }; var completedAt by remember(current.currentTractorHours) { mutableStateOf(current.currentTractorHours.toString()) }
    val currentValue = currentHours.toDoubleOrNull() ?: 0.0; val intervalValue = interval.toDoubleOrNull() ?: 0.0; val lastValue = lastService.toDoubleOrNull() ?: 0.0
    val status = ServiceCalculator.status(currentValue, lastValue, intervalValue)
    val history = current.serviceHistory.split(';').mapNotNull { r -> r.split(',').takeIf { it.size == 2 }?.let { p -> p[0].toDoubleOrNull()?.let { it to (p[1].toLongOrNull() ?: 0L) } } }.asReversed()
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(AppText.text(R.string.text_service_reminder), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item { Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(AppText.text(R.string.text_enable_service_reminder), fontWeight = FontWeight.SemiBold); Text(AppText.text(R.string.text_ask_for_tractor_hours_and_calculate_the_next_service), style = MaterialTheme.typography.bodySmall) }; Switch(settings.serviceReminderEnabled, { settings = settings.copy(serviceReminderEnabled = it) }) } } }
        if (settings.serviceReminderEnabled) {
            item { DecimalField(currentHours, { currentHours = it }, AppText.text(R.string.text_current_tractor_hours)) }; item { DecimalField(interval, { interval = it }, AppText.text(R.string.text_service_interval_hours)) }; item { DecimalField(lastService, { lastService = it }, AppText.text(R.string.text_last_service_at_hours)) }
            item { DropdownField(AppText.text(R.string.text_current_hours_update_frequency), settings.hoursUpdateFrequency, HoursUpdateFrequency.entries, { AppText.label(it.name) }, { settings = settings.copy(hoursUpdateFrequency = it) }) }
            item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                ServiceRow(AppText.text(R.string.text_current_hours), AppText.text(R.string.text_1_s_hrs, ServiceCalculator.formatHours(currentValue))); ServiceRow(AppText.text(R.string.text_last_service), AppText.text(R.string.text_1_s_hrs, ServiceCalculator.formatHours(lastValue))); ServiceRow(AppText.text(R.string.text_service_interval), AppText.text(R.string.text_1_s_hrs, ServiceCalculator.formatHours(intervalValue))); ServiceRow(AppText.text(R.string.text_next_service), AppText.text(R.string.text_1_s_hrs, ServiceCalculator.formatHours(status.nextServiceHours))); ServiceRow(AppText.text(R.string.text_remaining), if (status.remainingHours >= 0) AppText.text(R.string.text_1_s_hrs, ServiceCalculator.formatHours(status.remainingHours)) else AppText.text(R.string.text_0_hrs)); HorizontalDivider(); Text(if (status.remainingHours < -0.005) AppText.text(R.string.label_service_overdue_by_1_s_hrs, ServiceCalculator.formatHours(-status.remainingHours)) else AppText.label(status.label), color = if (status.remainingHours > 0.005) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontWeight = FontWeight.ExtraBold)
            } } }
            item { OutlinedButton(onClick = { completedAt = currentHours; completionDialog = true }, modifier = Modifier.fillMaxWidth()) { Text(AppText.text(R.string.text_mark_service_completed)) } }
            if (history.isNotEmpty()) { item { Text(AppText.text(R.string.text_service_history), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }; items(history) { (hours, timestamp) -> Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(AppText.text(R.string.text_1_s_hrs, ServiceCalculator.formatHours(hours)), fontWeight = FontWeight.Bold); Text(formatTimestamp(timestamp), style = MaterialTheme.typography.bodySmall) } } } }
        }
        item { Button(onClick = { onSave(settings.copy(currentTractorHours = currentValue, serviceIntervalHours = intervalValue, lastServiceHours = lastValue)) }, modifier = Modifier.fillMaxWidth()) { Text(AppText.text(R.string.text_save_service_settings)) } }
        item { Spacer(Modifier.height(42.dp)) }
    }
    if (completionDialog) AlertDialog(onDismissRequest = { completionDialog = false }, title = { Text(AppText.text(R.string.text_mark_service_completed)) }, text = { DecimalField(completedAt, { completedAt = it }, AppText.text(R.string.text_service_completed_at_hours)) }, confirmButton = { TextButton(onClick = { completedAt.toDoubleOrNull()?.let(onMarkCompleted); completionDialog = false }, enabled = completedAt.toDoubleOrNull() != null) { Text(AppText.text(R.string.text_confirm)) } }, dismissButton = { TextButton(onClick = { completionDialog = false }) { Text(AppText.text(R.string.text_cancel)) } })
}

@Composable private fun ServiceRow(label: String, value: String) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text(value, fontWeight = FontWeight.SemiBold) } }
private fun formatTimestamp(value: Long): String = if (value <= 0) "" else DateTimeFormatter.ofPattern("dd MMM yyyy").format(Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()))

@Composable fun CurrentHoursUpdateDialog(previousHours: Double, onLater: () -> Unit, onSave: (Double) -> Unit) {
    var value by remember(previousHours) { mutableStateOf(previousHours.toString()) }; val numeric = value.toDoubleOrNull()
    AlertDialog(onDismissRequest = onLater, title = { Text(AppText.text(R.string.text_current_hours_update_pending)) }, text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(AppText.text(R.string.text_please_enter_the_tractor_s_current_hour_reading)); DecimalField(value, { value = it }, AppText.text(R.string.text_current_hours)) } }, confirmButton = { TextButton(onClick = { numeric?.let(onSave) }, enabled = numeric != null && numeric >= previousHours) { Text(AppText.text(R.string.text_save)) } }, dismissButton = { TextButton(onClick = onLater) { Text(AppText.text(R.string.text_later)) } })
}
