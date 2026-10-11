package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.BuildConfig

@Composable
fun LanguageScreen() {
    val current = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(AppText.text(R.string.language_title), style = MaterialTheme.typography.headlineSmall)
        listOf(Triple("en", AppText.text(R.string.language_english), AppText.text(R.string.text_use_harvest_pay_in_english)), Triple("hi", AppText.text(R.string.language_hindi), AppText.text(R.string.language_hindi_description))).forEach { (code, title, subtitle) ->
            Card(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code)) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = current == code, onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code)) })
                    Column { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}

/** Local release records: missing historical documentation is stated explicitly. */
internal data class VersionHistoryEntry(val versionName: String, val displayOrder: Int, val notesResource: Int)
internal val versionHistory = listOf(
    VersionHistoryEntry("1.4.2", 3, com.harvestpay.app.R.array.release_notes_142),
    VersionHistoryEntry("1.4.1", 2, com.harvestpay.app.R.array.release_notes_141),
    VersionHistoryEntry("1.4.0", 1, com.harvestpay.app.R.array.release_notes_unknown),
    VersionHistoryEntry("1.2.2", 0, com.harvestpay.app.R.array.release_notes_122),
).sortedByDescending { it.displayOrder }

@Composable
fun VersionHistoryScreen() {
    val context = LocalContext.current
    val resources = LocalResources.current
    var expanded by rememberSaveable { mutableStateOf<String?>(null) }
    // Installed metadata, rather than the first release row, determines the badge.
    val installedVersion = remember(context) {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: BuildConfig.VERSION_NAME
    }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text(AppText.text(R.string.text_what_s_new), style = MaterialTheme.typography.headlineSmall) }
        items(versionHistory, key = { it.versionName }) { release ->
            val open = expanded == release.versionName
            Card(onClick = { expanded = if (open) null else release.versionName }, modifier = Modifier.fillMaxWidth().animateContentSize()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("v${release.versionName}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        if (release.versionName == installedVersion) SuggestionChip(onClick = {}, label = { Text(AppText.text(R.string.text_current_version)) })
                        Icon(if (open) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = if (open) AppText.text(R.string.text_collapse_release_notes) else AppText.text(R.string.text_expand_release_notes))
                    }
                    AnimatedVisibility(open) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            resources.getStringArray(release.notesResource).forEach { Text(AppText.text(R.string.text_1_s_5ff08e, it), style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
            }
        }
    }
}
