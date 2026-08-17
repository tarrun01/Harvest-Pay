package com.harvestpay.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardGreetingTest {
    @Test
    fun greetingChangesWithTimeOfDay() {
        assertEquals("Good Morning", greetingForHour(7))
        assertEquals("Good Afternoon", greetingForHour(14))
        assertEquals("Good Evening", greetingForHour(20))
    }
}
