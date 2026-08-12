package com.harvestpay.app.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.ThemePreference
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import java.time.LocalDate

@Composable
fun MoreScreen(
    onReports: () -> Unit,
    onBackup: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit,
) {
    var confirmLogout by remember { mutableStateOf(false) }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("More", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)) }
        item { MoreRow(Icons.Outlined.QueryStats, "Reports", "Earnings, work and pending insights", onReports) }
        item { MoreRow(Icons.Outlined.Backup, "Backup & export", "JSON backup, restore and CSV records", onBackup) }
        item { MoreRow(Icons.Outlined.Settings, "Settings", "Owner, rates, theme and app lock", onSettings) }
        item { MoreRow(Icons.Outlined.Info, "About", "Harvest Pay 1.0.0", onAbout) }
        item { MoreRow(Icons.Outlined.Logout, "Logout", "Lock the local ledger", { confirmLogout = true }) }
    }
    if (confirmLogout) {
        ConfirmDialog("Log out?", "You will need the owner mobile number and password to open Harvest Pay again.", "Logout", false, onLogout, { confirmLogout = false })
    }
}

@Composable
private fun MoreRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ChevronRight, null)
        }
    }
}

@Composable
fun SettingsScreen(
    current: AppSettings,
    onTheme: (ThemePreference) -> Unit,
    onSave: (AppSettings) -> Unit,
) {
    var settings by remember(current) { mutableStateOf(current) }
    var defaultRate by remember(current.defaultRate) { mutableStateOf(current.defaultRate.toString()) }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item { Text("Owner & business", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item { OutlinedTextField(settings.businessName, { settings = settings.copy(businessName = it) }, label = { Text("Business name") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.ownerName, { settings = settings.copy(ownerName = it) }, label = { Text("Owner name") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.ownerMobile, { settings = settings.copy(ownerMobile = it) }, label = { Text("Owner mobile number") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.tractorNumber, { settings = settings.copy(tractorNumber = it) }, label = { Text("Tractor number") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.village, { settings = settings.copy(village = it) }, label = { Text("Village") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.address, { settings = settings.copy(address = it) }, label = { Text("Address") }, minLines = 2, modifier = Modifier.fillMaxWidth()) }
        item { Text("Rates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item { DecimalField(defaultRate, { defaultRate = it }, "Default rate per Bigha", prefix = "₹") }
        item { OutlinedTextField(settings.ratePresets, { settings = settings.copy(ratePresets = it) }, label = { Text("Rate presets (comma separated)") }, supportingText = { Text("Example: 700,800,900,1000") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField("Indian Rupee (₹)", {}, readOnly = true, label = { Text("Currency") }, modifier = Modifier.fillMaxWidth()) }
        item { Text("Theme", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    ThemePreference.entries.forEach { theme ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = current.theme == theme, onClick = { onTheme(theme) })
                            Text(theme.name.lowercase().replaceFirstChar(Char::uppercase))
                        }
                    }
                }
            }
        }
        item { Text("App lock", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item {
            DropdownField(
                "Auto-lock after inactivity",
                settings.autoLockMinutes,
                listOf(5, 15, 30, 60),
                { "$it minutes" },
                { settings = settings.copy(autoLockMinutes = it) },
            )
        }
        item {
            Button(
                onClick = { onSave(settings.copy(defaultRate = defaultRate.toDoubleOrNull() ?: 0.0)) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Save settings") }
        }
        item { Spacer(Modifier.height(42.dp)) }
    }
}

@Composable
fun BackupScreen(
    backupText: () -> String,
    customersCsv: () -> String,
    paymentsCsv: () -> String,
    onRestore: (String) -> Unit,
) {
    val context = LocalContext.current
    var importedText by remember { mutableStateOf<String?>(null) }
    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer -> writer.write(backupText()) } }
    }
    val customerCsvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer -> writer.write(customersCsv()) } }
    }
    val paymentCsvLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { context.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer -> writer.write(paymentsCsv()) } }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        importedText = uri?.let { context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader -> reader.readText() } }
    }

    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Backup & export", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item { Text("The JSON backup contains customers, fields, work, payments, reminders and settings. Keep it somewhere safe.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { BackupAction(Icons.Outlined.Backup, "Export complete backup", "Save a restorable JSON file") { backupLauncher.launch("HarvestPay-backup-${LocalDate.now()}.json") } }
        item { BackupAction(Icons.Outlined.Backup, "Import backup", "Replace current local records from JSON") { importLauncher.launch(arrayOf("application/json", "text/plain")) } }
        item { BackupAction(Icons.Outlined.Description, "Export customer records", "Save customers as CSV") { customerCsvLauncher.launch("HarvestPay-customers-${LocalDate.now()}.csv") } }
        item { BackupAction(Icons.Outlined.Description, "Export payment records", "Save payment history as CSV") { paymentCsvLauncher.launch("HarvestPay-payments-${LocalDate.now()}.csv") } }
        item { Spacer(Modifier.height(42.dp)) }
    }
    importedText?.let { text ->
        ConfirmDialog(
            "Restore this backup?",
            "Current customers, fields, work, payments and reminders will be replaced. This cannot be undone unless you export a backup first.",
            "Restore backup",
            true,
            { onRestore(text) },
            { importedText = null },
        )
    }
}

@Composable
private fun BackupAction(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }
            Icon(Icons.Outlined.ChevronRight, null)
        }
    }
}

@Composable
fun AboutScreen() {
    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Outlined.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
        Text("Harvest Pay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Version 1.0.0")
        Text("An offline-first ledger for tractor owners: customers, fields, ploughing work, payments, reminders, receipts and reports.")
        Text("Your business data stays on this device unless you export or share it.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
