package com.harvestpay.app.domain

import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.HoursUpdateFrequency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceCalculatorTest {
    @Test
    fun `reports ok due and overdue service states`() {
        assertEquals("Service OK", ServiceCalculator.status(620.0, 500.0, 250.0).label)
        assertEquals(130.0, ServiceCalculator.status(620.0, 500.0, 250.0).remainingHours, 0.001)
        assertEquals("Service Due", ServiceCalculator.status(750.0, 500.0, 250.0).label)
        assertEquals("Service Overdue by 25 hrs", ServiceCalculator.status(775.0, 500.0, 250.0).label)
    }

    @Test
    fun `weekly prompt becomes due after seven days`() {
        val day = 24L * 60 * 60 * 1000
        val settings = AppSettings(
            serviceReminderEnabled = true,
            hoursUpdateFrequency = HoursUpdateFrequency.WEEKLY,
            lastHoursUpdatedAt = 10L * day,
        )
        assertFalse(ServiceCalculator.hoursUpdateDue(settings, 16L * day))
        assertTrue(ServiceCalculator.hoursUpdateDue(settings, 17L * day))
        assertFalse(ServiceCalculator.hoursUpdateDue(settings.copy(serviceReminderEnabled = false), 30L * day))
    }
}
