package com.harvestpay.app.measurement

import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class MeasurementFormattingTest {
    @Test fun acceptsExactlyTwoDecimalsWithHalfUpTies() {
        assertEquals("1.50", MeasurementFormatting.input(1.5))
        assertEquals("3.00", MeasurementFormatting.input(3.0))
        assertEquals("1.01", MeasurementFormatting.input(1.005))
        assertEquals("0.76", MeasurementFormatting.input(0.756789))
    }
    @Test fun convertsFromOriginalAreaRatherThanRoundedAcres() {
        val acres = 2.345678912345
        val area = acres * FieldGeometry.SQUARE_METERS_PER_ACRE
        assertEquals("2.35", MeasurementFormatting.display(acres, Locale.ENGLISH))
        assertEquals("3.75", MeasurementFormatting.bighaInput(area, 1.6))
        assertEquals(acres, FieldGeometry.acres(area), 1e-12)
        assertEquals(acres * 1.6, FieldGeometry.bigha(area, 1.6), 1e-12)
    }
    @Test fun presentationSupportsHindiAndDecimalSeparatorLocales() {
        assertEquals("1.50", MeasurementFormatting.display(1.5, Locale.forLanguageTag("hi-IN")))
        assertEquals("1,50", MeasurementFormatting.display(1.5, Locale.GERMANY))
        assertEquals("1.50", MeasurementFormatting.input(1.5))
    }
    @Test fun nonzeroSmallAreaCanBeDetectedBeforeSaving() {
        val area = 0.003 * FieldGeometry.SQUARE_METERS_PER_ACRE
        assertEquals("0.00", MeasurementFormatting.bighaInput(area, 1.0))
        assertTrue(FieldGeometry.acres(area) > 0)
    }
}
