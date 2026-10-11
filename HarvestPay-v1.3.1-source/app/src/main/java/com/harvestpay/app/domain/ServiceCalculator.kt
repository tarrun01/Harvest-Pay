package com.harvestpay.app.domain

import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.HoursUpdateFrequency
import java.time.Instant
import java.time.ZoneId

data class ServiceStatus(val nextServiceHours: Double, val remainingHours: Double, val label: String)

object ServiceCalculator {
    fun status(currentHours: Double, lastServiceHours: Double, intervalHours: Double): ServiceStatus {
        val next = lastServiceHours.coerceAtLeast(0.0) + intervalHours.coerceAtLeast(0.0)
        val remaining = BusinessCalculator.money(next - currentHours.coerceAtLeast(0.0))
        val label = when { remaining < -0.005 -> "Service Overdue by ${formatHours(-remaining)} hrs"; remaining <= 0.005 -> "Service Due"; else -> "Service OK" }
        return ServiceStatus(next, remaining, label)
    }
    fun hoursUpdateDue(settings: AppSettings, nowMillis: Long = System.currentTimeMillis()): Boolean {
        if (!settings.serviceReminderEnabled) return false
        if (settings.lastHoursUpdatedAt <= 0L) return true
        return when (settings.hoursUpdateFrequency) {
            HoursUpdateFrequency.WEEKLY -> nowMillis - settings.lastHoursUpdatedAt >= 7L * 24 * 60 * 60 * 1000
            HoursUpdateFrequency.MONTHLY -> {
                val zone = ZoneId.systemDefault(); val last = Instant.ofEpochMilli(settings.lastHoursUpdatedAt).atZone(zone)
                !last.plusMonths(1).toInstant().isAfter(Instant.ofEpochMilli(nowMillis))
            }
        }
    }
    fun formatHours(value: Double): String = if (kotlin.math.abs(value % 1.0) < 0.005) value.toLong().toString() else "%.2f".format(value).trimEnd('0').trimEnd('.')
}
