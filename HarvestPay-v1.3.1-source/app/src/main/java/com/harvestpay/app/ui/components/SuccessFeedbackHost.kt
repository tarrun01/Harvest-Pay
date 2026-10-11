package com.harvestpay.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.harvestpay.app.domain.SuccessFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/** Lives above navigation so confirmation survives the form's successful navigation. */
@Composable
fun SuccessFeedbackHost(events: Flow<SuccessFeedback>, modifier: Modifier = Modifier) {
    var feedback by remember { mutableStateOf<SuccessFeedback?>(null) }
    var visible by remember { mutableStateOf(false) }
    val accessibility = LocalAccessibilityManager.current
    LaunchedEffect(events, accessibility) {
        events.collectLatest { event ->
            feedback = event
            visible = true
            delay(accessibility?.calculateRecommendedTimeoutMillis(2000L, containsIcons = true, containsText = true, containsControls = false) ?: 2000L)
            visible = false
        }
    }
    val scale by animateFloatAsState(if (visible) 1f else 0.8f, tween(180), label = "success check")
    AnimatedVisibility(visible, modifier = modifier, enter = fadeIn(tween(160)) + slideInVertically(tween(180)) { it / 4 }, exit = fadeOut(tween(140))) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).semantics { liveRegion = LiveRegionMode.Polite },
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp,
        ) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CheckCircle, null, Modifier.size(24.dp).graphicsLayer { scaleX = scale; scaleY = scale })
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(feedback?.message.orEmpty(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    feedback?.detail?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}
