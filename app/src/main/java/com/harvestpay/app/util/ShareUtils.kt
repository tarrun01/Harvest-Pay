package com.harvestpay.app.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.WorkSummary
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ShareUtils {
    fun paymentMessage(summary: CustomerSummary, work: WorkSummary? = null): String {
        val customer = summary.customer
        val workLines = work?.let {
            """
            Field: ${it.field?.fieldName ?: "Field"}
            Field Size: ${formatBigha(it.work.sizeBigha)} Bigha
            Rate: ${formatMoney(it.work.ratePerBigha)}/Bigha
            Total Amount: ${formatMoney(it.work.totalAmount)}
            Amount Paid: ${formatMoney(it.paid)}
            Pending Amount: ${formatMoney(it.pending)}
            Work Date: ${formatDate(it.work.workDate)}
            """.trimIndent()
        } ?: """
            Total Work Amount: ${formatMoney(summary.totalBill)}
            Amount Paid: ${formatMoney(summary.totalPaid)}
            Pending Amount: ${formatMoney(summary.pending)}
        """.trimIndent()

        return """
            Namaste ${customer.name},

            Harvest Pay payment details:

            $workLines

            Total Outstanding Amount: ${formatMoney(summary.pending)}

            Please make the pending payment when convenient.

            Thank you.
        """.trimIndent()
    }

    fun openWhatsApp(context: Context, mobile: String, message: String) {
        val digits = mobile.filter(Char::isDigit).let {
            when {
                it.length == 10 -> "91$it"
                it.length == 11 && it.startsWith("0") -> "91${it.drop(1)}"
                else -> it
            }
        }
        val encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.name())
        val intent = Intent(Intent.ACTION_VIEW, "https://wa.me/$digits?text=$encoded".toUri())
            .setPackage("com.whatsapp")
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                context.startActivity(intent.setPackage(null))
            } catch (_: Exception) {
                Toast.makeText(context, "No app found to open WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun dial(context: Context, mobile: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_DIAL, "tel:$mobile".toUri()))
        }.onFailure {
            Toast.makeText(context, "No phone app found", Toast.LENGTH_SHORT).show()
        }
    }
}
