package com.harvestpay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.domain.HarvestUiState
import com.harvestpay.app.domain.PaymentStatus
import com.harvestpay.app.ui.components.DropdownField
import com.harvestpay.app.ui.components.EmptyState
import com.harvestpay.app.util.formatMoney

@Composable
fun GlobalSearchScreen(uiState: HarvestUiState, onOpenCustomer: (Long) -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All statuses") }
    val results = uiState.customerSummaries.filter { summary ->
        val fields = summary.fields.joinToString(" ") { "${it.fieldName} ${it.location}" }
        val matches = query.isBlank() || listOf(
            summary.customer.name,
            summary.customer.mobile,
            summary.customer.village,
            summary.customer.address,
            fields,
        ).any { it.contains(query, true) }
        val matchesStatus = when (filter) {
            "Paid" -> summary.pending <= 0.005 && summary.work.isNotEmpty()
            "Partially paid" -> summary.work.any { it.status == PaymentStatus.PARTIALLY_PAID }
            "Pending" -> summary.pending > 0.005
            else -> true
        }
        matches && matchesStatus
    }
    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            OutlinedTextField(
                query,
                { query = it },
                label = { Text("Search customers, fields or villages") },
                leadingIcon = { Icon(Icons.Outlined.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            )
        }
        item { DropdownField("Payment status", filter, listOf("All statuses", "Paid", "Partially paid", "Pending"), { it }, { filter = it }) }
        if (results.isEmpty()) item { EmptyState("No matches", "Try another name, number, field or village.") }
        else items(results, key = { it.customer.id }) { summary ->
            Card(onClick = { onOpenCustomer(summary.customer.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(summary.customer.name, fontWeight = FontWeight.Bold)
                        Text(listOf(summary.customer.village, summary.customer.mobile).filter(String::isNotBlank).joinToString(" • "))
                        Text(summary.fields.joinToString { it.fieldName }, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(formatMoney(summary.pending), color = if (summary.pending > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
