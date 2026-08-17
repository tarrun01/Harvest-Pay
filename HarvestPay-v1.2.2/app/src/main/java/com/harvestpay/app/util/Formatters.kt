package com.harvestpay.app.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val india = Locale.Builder().setLanguage("en").setRegion("IN").build()
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", india)
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a", india)
private val currencyFormatter = ThreadLocal.withInitial { NumberFormat.getCurrencyInstance(india) }
private val bighaFormatter = ThreadLocal.withInitial { NumberFormat.getNumberInstance(india) }

fun formatMoney(value: Double): String = requireNotNull(currencyFormatter.get()).run {
    maximumFractionDigits = if (value % 1.0 == 0.0) 0 else 2
    format(value)
}

fun formatBigha(value: Double): String = requireNotNull(bighaFormatter.get()).run {
    maximumFractionDigits = 2
    format(value)
}

fun formatDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(dateFormatter)

fun formatDateTime(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .format(dateTimeFormatter)

fun todayEpochDay(): Long = LocalDate.now().toEpochDay()
