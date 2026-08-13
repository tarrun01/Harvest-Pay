package com.harvestpay.app.util

import com.harvestpay.app.data.CustomerEntity
import com.harvestpay.app.domain.CustomerSummary
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareUtilsTest {
    @Test
    fun paymentMessageHasNoUnwantedBlankLines() {
        val message = ShareUtils.paymentMessage(
            CustomerSummary(
                customer = CustomerEntity(id = 1, name = "Ramesh", mobile = "9876543210"),
                fields = emptyList(),
                work = emptyList(),
                payments = emptyList(),
                totalBigha = 0.0,
                totalBill = 800.0,
                totalPaid = 200.0,
                pending = 600.0,
                advance = 0.0,
                lastWorkDate = null,
            ),
        )

        assertFalse(message.contains("\n\n"))
        assertTrue(message.startsWith("Namaste Ramesh,"))
        assertTrue(message.endsWith("Thank you."))
    }
}
