package com.harvestpay.app.measurement

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

/** Round presentation only; geometry and stored measurement metadata stay full precision. */
object MeasurementFormatting {
    fun input(value: Double): String = BigDecimal.valueOf(value)
        .setScale(2, RoundingMode.HALF_UP).toPlainString()
    fun display(value: Double, locale: Locale = Locale.getDefault()): String =
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            roundingMode = RoundingMode.HALF_UP
        }.format(BigDecimal.valueOf(value))
    fun bighaInput(squareMeters: Double, factor: Double): String =
        input(FieldGeometry.bigha(squareMeters, factor))
}
