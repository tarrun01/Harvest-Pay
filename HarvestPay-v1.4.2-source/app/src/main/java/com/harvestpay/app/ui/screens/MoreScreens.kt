package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
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
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.harvestpay.app.data.BiometricLockTiming
import com.harvestpay.app.data.ThemePreference
import com.harvestpay.app.BuildConfig
import com.harvestpay.app.MainActivity
import com.harvestpay.app.ui.components.ConfirmDialog
import com.harvestpay.app.ui.components.DecimalField
import com.harvestpay.app.ui.components.DropdownField
import java.time.LocalDate

@Composable
fun MoreScreen(
    onReports: () -> Unit,
    onBackup: () -> Unit,
    onWorkTypes: () -> Unit,
    onServiceReminder: () -> Unit,
    onSettings: () -> Unit,
    onBiometricLock: () -> Unit,
    onChangePassword: () -> Unit,
    onAbout: () -> Unit,
    onLanguage: () -> Unit,
    onLogout: () -> Unit,
) {
    var confirmLogout by remember { mutableStateOf(false) }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text(AppText.text(R.string.text_more), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)) }
        item { MoreRow(Icons.Outlined.QueryStats, AppText.text(R.string.text_reports), AppText.text(R.string.text_earnings_work_and_pending_insights), onReports) }
        item { MoreRow(Icons.Outlined.Agriculture, AppText.text(R.string.text_work_type_management), AppText.text(R.string.text_english_hindi_names_and_bigha_minute_billing), onWorkTypes) }
        item { MoreRow(Icons.Outlined.Build, AppText.text(R.string.text_service_reminder), AppText.text(R.string.text_tractor_hours_interval_and_service_status), onServiceReminder) }
        item { MoreRow(Icons.Outlined.Backup, AppText.text(R.string.text_backup_export), AppText.text(R.string.text_json_backup_restore_and_csv_records), onBackup) }
        item { MoreRow(Icons.Outlined.Language, AppText.text(R.string.language_title), AppText.text(R.string.text_choose_english_or_hindi), onLanguage) }
        item { MoreRow(Icons.Outlined.Settings, AppText.text(R.string.text_settings), AppText.text(R.string.text_owner_rates_theme_and_app_lock), onSettings) }
        item { MoreRow(Icons.Outlined.Fingerprint, AppText.text(R.string.text_biometric_lock), AppText.text(R.string.text_fingerprint_protection_and_lock_timing), onBiometricLock) }
        item { MoreRow(Icons.Outlined.LockReset, AppText.text(R.string.text_change_password), AppText.text(R.string.text_update_the_password_used_to_unlock_harvest_pay), onChangePassword) }
        item { MoreRow(Icons.Outlined.Info, AppText.text(R.string.text_about), AppText.text(R.string.text_harvest_pay_1_s, BuildConfig.VERSION_NAME), onAbout) }
        item { MoreRow(Icons.Outlined.Logout, AppText.text(R.string.text_logout), AppText.text(R.string.text_lock_the_local_ledger), { confirmLogout = true }) }
    }
    if (confirmLogout) {
        ConfirmDialog(AppText.text(R.string.text_log_out), AppText.text(R.string.text_you_will_need_the_owner_mobile_number_and_password_to_o), AppText.text(R.string.text_logout), false, onLogout, { confirmLogout = false })
    }
}

@Composable
fun BiometricLockScreen(settings: AppSettings) {
    var showLockTiming by remember { mutableStateOf(false) }
    val activity = androidx.activity.compose.LocalActivity.current as? MainActivity
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(AppText.text(R.string.text_biometric_lock), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(AppText.text(R.string.text_biometric_lock), fontWeight = FontWeight.SemiBold)
                        Text(AppText.text(R.string.text_use_fingerprint_or_supported_device_biometrics_to_prote), style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = settings.biometricLockEnabled, onCheckedChange = { activity?.requestBiometricToggle() })
                }
            }
        }
        if (settings.biometricLockEnabled) {
            item {
                Card(onClick = { showLockTiming = true }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(AppText.text(R.string.text_lock_timing), fontWeight = FontWeight.SemiBold)
                            Text(AppText.label(settings.biometricLockTiming.label), style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(Icons.Outlined.ChevronRight, null)
                    }
                }
            }
        }
    }
    if (showLockTiming) AlertDialog(
        onDismissRequest = { showLockTiming = false },
        title = { Text(AppText.text(R.string.text_lock_timing)) },
        text = {
            Column {
                BiometricLockTiming.entries.forEach { timing ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = settings.biometricLockTiming == timing,
                            onClick = { activity?.setBiometricLockTiming(timing); showLockTiming = false },
                        )
                        Text(AppText.label(timing.label))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { showLockTiming = false }) { Text(AppText.text(R.string.text_close)) } },
    )
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
    onLandSettings: () -> Unit = {},
) {
    var settings by remember(current) { mutableStateOf(current) }
    var defaultRate by rememberSaveable(current.defaultRate) { mutableStateOf(current.defaultRate.toString()) }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(AppText.text(R.string.text_settings), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item { OutlinedButton(onClick = onLandSettings, modifier = Modifier.fillMaxWidth()) { Text(AppText.text(R.string.text_land_measurement_settings)) } }
        item { Text(AppText.text(R.string.text_owner_business), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item { OutlinedTextField(settings.businessName, { settings = settings.copy(businessName = it) }, label = { Text(AppText.text(R.string.text_business_name)) }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.ownerName, { settings = settings.copy(ownerName = it) }, label = { Text(AppText.text(R.string.text_owner_name)) }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.ownerMobile, { settings = settings.copy(ownerMobile = it) }, label = { Text(AppText.text(R.string.text_owner_mobile_number)) }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.tractorNumber, { settings = settings.copy(tractorNumber = it) }, label = { Text(AppText.text(R.string.text_tractor_number)) }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.village, { settings = settings.copy(village = it) }, label = { Text(AppText.text(R.string.text_village)) }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(settings.address, { settings = settings.copy(address = it) }, label = { Text(AppText.text(R.string.text_address)) }, minLines = 2, modifier = Modifier.fillMaxWidth()) }
        item { Text(AppText.text(R.string.text_rates), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item { DecimalField(defaultRate, { defaultRate = it }, AppText.text(R.string.text_default_rate_per_bigha), prefix = "₹") }
        item { OutlinedTextField(settings.ratePresets, { settings = settings.copy(ratePresets = it) }, label = { Text(AppText.text(R.string.text_rate_presets_comma_separated)) }, supportingText = { Text(AppText.text(R.string.text_example_700_800_900_1000)) }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(AppText.text(R.string.text_indian_rupee), {}, readOnly = true, label = { Text(AppText.text(R.string.text_currency)) }, modifier = Modifier.fillMaxWidth()) }
        item { Text(AppText.text(R.string.text_theme), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    ThemePreference.entries.forEach { theme ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = settings.theme == theme, onClick = {
                                settings = settings.copy(theme = theme)
                                onTheme(theme)
                            })
                            Text(AppText.label(theme.name))
                        }
                    }
                }
            }
        }
        item { Text(AppText.text(R.string.text_app_lock), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) }
        item {
            DropdownField(
                AppText.text(R.string.text_auto_lock_after_inactivity),
                settings.autoLockMinutes,
                listOf(0, 5, 15, 30, 60),
                { if (it == 0) AppText.text(R.string.text_never) else AppText.text(R.string.text_1_s_minutes, it) },
                { settings = settings.copy(autoLockMinutes = it) },
            )
        }
        item {
            Button(
                onClick = { onSave(settings.copy(defaultRate = defaultRate.toDoubleOrNull() ?: 0.0)) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text(AppText.text(R.string.text_save_settings)) }
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
        item { Text(AppText.text(R.string.text_backup_export), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp)) }
        item { Text(AppText.text(R.string.text_the_json_backup_contains_customers_fields_work_payments), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item { BackupAction(Icons.Outlined.Backup, AppText.text(R.string.text_export_complete_backup), AppText.text(R.string.text_save_a_restorable_json_file)) { backupLauncher.launch("HarvestPay-backup-${LocalDate.now()}.json") } }
        item { BackupAction(Icons.Outlined.Backup, AppText.text(R.string.text_import_backup), AppText.text(R.string.text_replace_current_local_records_from_json)) { importLauncher.launch(arrayOf("application/json", "text/plain")) } }
        item { BackupAction(Icons.Outlined.Description, AppText.text(R.string.text_export_customer_records), AppText.text(R.string.text_save_customers_as_csv)) { customerCsvLauncher.launch("HarvestPay-customers-${LocalDate.now()}.csv") } }
        item { BackupAction(Icons.Outlined.Description, AppText.text(R.string.text_export_payment_records), AppText.text(R.string.text_save_payment_history_as_csv)) { paymentCsvLauncher.launch("HarvestPay-payments-${LocalDate.now()}.csv") } }
        item { Spacer(Modifier.height(42.dp)) }
    }
    importedText?.let { text ->
        ConfirmDialog(
            AppText.text(R.string.text_restore_this_backup),
            AppText.text(R.string.text_current_customers_fields_work_payments_and_reminders_wi),
            AppText.text(R.string.text_restore_backup),
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
fun AboutScreen(onVersionHistory: () -> Unit = {}) {
    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Outlined.DarkMode, null, tint = MaterialTheme.colorScheme.primary)
        Text("Harvest Pay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(AppText.text(R.string.text_version_1_s, BuildConfig.VERSION_NAME))
        Text(AppText.text(R.string.text_an_offline_first_ledger_for_tractor_owners_customers_fi))
        Text(AppText.text(R.string.text_your_business_data_stays_on_this_device_unless_you_expo), color = MaterialTheme.colorScheme.onSurfaceVariant)
        androidx.compose.foundation.layout.Spacer(Modifier.height(20.dp))
        Text(
            AppText.text(R.string.text_made_with_for_every_farmer),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            AppText.text(R.string.text_designed_and_developed_by_tarun_kumar),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(onClick = onVersionHistory, modifier = Modifier.fillMaxWidth()) { Text(AppText.text(R.string.text_what_s_new_f6229f)) }
    }
}
