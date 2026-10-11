package com.harvestpay.app.util

import com.harvestpay.app.localization.AppText
import com.harvestpay.app.R
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.WorkSummary
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ShareUtils {
    fun paymentMessage(summary: CustomerSummary, work: WorkSummary? = null): String {
        val customer = summary.customer
        val workLines = work?.let {
            val billingLines = if (it.work.billingMode == BillingType.PER_MINUTE) {
                listOf(
                    AppText.text(R.string.text_duration_1_s_minutes, it.work.durationMinutes),
                    AppText.text(R.string.text_rate_1_s_minute, formatMoney(it.work.ratePerBigha)),
                )
            } else {
                listOf(
                    AppText.text(R.string.text_field_size_1_s_bigha, formatBigha(it.work.sizeBigha)),
                    AppText.text(R.string.text_round_1_s, formatBigha(it.work.roundMultiplier)),
                    AppText.text(R.string.text_work_done_1_s_bigha, formatBigha(BusinessCalculator.billableBigha(it.work))),
                    AppText.text(R.string.text_rate_1_s_bigha_round, formatMoney(it.work.ratePerBigha)),
                )
            }
            listOf(AppText.text(R.string.text_field_1_s, it.field?.fieldName ?: "Field")) + billingLines + listOf(
                AppText.text(R.string.text_total_amount_1_s, formatMoney(it.work.totalAmount)),
                AppText.text(R.string.text_amount_paid_1_s, formatMoney(it.paid)),
                AppText.text(R.string.text_pending_amount_1_s, formatMoney(it.pending)),
                AppText.text(R.string.text_work_date_1_s, formatDate(it.work.workDate)),
            )
        } ?: listOf(
            AppText.text(R.string.text_previous_due_1_s, formatMoney(summary.previousDue)),
            AppText.text(R.string.text_total_work_amount_1_s, formatMoney(summary.workTotal)),
            AppText.text(R.string.text_total_bill_1_s, formatMoney(summary.totalBill)),
            AppText.text(R.string.text_amount_paid_1_s, formatMoney(summary.totalPaid)),
            AppText.text(R.string.text_pending_amount_1_s, formatMoney(summary.pending)),
            AppText.text(R.string.text_advance_credit_1_s, formatMoney(summary.advance)),
        )

        val closing = when {
            summary.pending > 0.005 -> AppText.text(R.string.text_please_make_the_pending_payment_when_convenient)
            summary.advance > 0.005 -> AppText.text(R.string.text_your_advance_credit_will_be_adjusted_automatically_agai)
            else -> AppText.text(R.string.text_your_account_is_fully_settled)
        }

        return buildList {
            add(AppText.text(R.string.text_namaste_1_s, customer.name))
            add(AppText.text(R.string.text_harvest_pay_payment_details))
            addAll(workLines)
            add(AppText.text(R.string.text_total_outstanding_amount_1_s, formatMoney(summary.pending)))
            add(closing)
            add(AppText.text(R.string.text_thank_you))
        }.joinToString("\n")
    }

    fun openWhatsApp(context: Context, mobile: String, message: String) {
        if (mobile.isBlank()) {
            Toast.makeText(context, AppText.text(R.string.text_mobile_number_is_not_available_for_this_customer), Toast.LENGTH_SHORT).show()
            return
        }
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
                Toast.makeText(context, AppText.text(R.string.text_no_app_found_to_open_whatsapp), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun dial(context: Context, mobile: String) {
        if (mobile.isBlank()) {
            Toast.makeText(context, AppText.text(R.string.text_mobile_number_is_not_available_for_this_customer), Toast.LENGTH_SHORT).show()
            return
        }
        runCatching {
            context.startActivity(Intent(Intent.ACTION_DIAL, "tel:$mobile".toUri()))
        }.onFailure {
            Toast.makeText(context, AppText.text(R.string.text_no_phone_app_found), Toast.LENGTH_SHORT).show()
        }
    }
}
