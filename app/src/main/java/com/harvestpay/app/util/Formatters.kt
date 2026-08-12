package com.harvestpay.app.util

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val india = Locale.Builder().setLanguage("en").setRegion("IN").build()
private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", india)

fun formatMoney(value: Double): String = NumberFormat.getCurrencyInstance(india).apply {
    maximumFractionDigits = if (value % 1.0 == 0.0) 0 else 2
}.format(value)

fun formatBigha(value: Double): String = NumberFormat.getNumberInstance(india).apply {
    maximumFractionDigits = 2
}.format(value)

fun formatDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(dateFormatter)

fun formatDateTime(epochMillis: Long): String = Instant.ofEpochMilli(epochMillis)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a", india))

fun todayEpochDay(): Long = LocalDate.now().toEpochDay()
