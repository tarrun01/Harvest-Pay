package com.harvestpay.app.ui.screens

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.domain.DashboardInsight
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.ui.components.LedgerErrorState
import com.harvestpay.app.ui.components.LedgerLoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DashboardInsightScreen(
    state: HarvestUiState,
    insight: DashboardInsight,
    onRetry: () -> Unit,
    onOpenRecord: (InsightTarget) -> Unit,
) {
    if (state.loading) { LedgerLoadingState(); return }
    state.error?.let { LedgerErrorState(it, onRetry); return }
    var query by rememberSaveable(insight) { mutableStateOf("") }
    val page by produceState<InsightPage?>(null, state, insight, query) {
        value = withContext(Dispatchers.Default) { buildInsightPage(state, insight, query) }
    }
    val content = page
    if (content == null) { LedgerLoadingState(); return }
    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item(key = "summary") {
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(insight.period(state), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    content.summary.forEachIndexed { index, (label, value) ->
                        if (index == 0) {
                            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        item(key = "search") {
            OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text(content.searchHint) }, leadingIcon = { Icon(Icons.Outlined.Search, null) }, singleLine = true, shape = RoundedCornerShape(14.dp))
        }
        if (content.rows.isEmpty()) item(key = "empty") {
            EmptyState(if (query.isBlank()) content.emptyTitle else AppText.text(R.string.text_no_matches), if (query.isBlank()) content.emptyBody else AppText.text(R.string.text_try_a_different_name_date_or_search_term))
        }
        items(content.rows, key = { it.id }) { row ->
            Card(onClick = { onOpenRecord(row.target) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(row.title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(row.value, fontWeight = FontWeight.Bold, color = if (row.unpaid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                    }
                    Text(row.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    row.details.forEach { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        row.status?.let { Text(AppText.label(it), style = MaterialTheme.typography.labelMedium, color = if (row.unpaid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, AppText.text(R.string.text_open_record), Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
