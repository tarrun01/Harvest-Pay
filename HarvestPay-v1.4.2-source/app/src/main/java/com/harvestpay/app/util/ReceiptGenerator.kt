package com.harvestpay.app.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.harvestpay.app.R
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.data.BillingType
import com.harvestpay.app.data.billingMode
import com.harvestpay.app.domain.BusinessCalculator
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.CustomerAccountReport
import com.harvestpay.app.domain.CustomerReportSection
import com.harvestpay.app.domain.WorkSummary
import java.io.File
import java.io.OutputStream

enum class PdfLanguage { ENGLISH, HINDI, BOTH }

data class ReceiptData(
    val businessName: String,
    val ownerLine: String,
    val customerName: String,
    val mobile: String,
    val fieldName: String,
    val fieldSize: Double,
    val round: Double,
    val workDoneBigha: Double,
    val rate: Double,
    val workDate: Long,
    val total: Double,
    val paid: Double,
    val pending: Double,
    val status: String,
    val billingType: BillingType = BillingType.PER_BIGHA,
    val durationMinutes: Int = 0,
)

object ReceiptGenerator {
    fun from(summary: WorkSummary, settings: AppSettings): ReceiptData = ReceiptData(
        businessName = settings.businessName.ifBlank { "Harvest Pay" },
        ownerLine = listOf(settings.ownerName, settings.ownerMobile, settings.tractorNumber)
            .filter(String::isNotBlank).joinToString(" • "),
        customerName = summary.customer?.name.orEmpty(),
        mobile = summary.customer?.mobile.orEmpty(),
        fieldName = summary.field?.fieldName ?: "Field",
        fieldSize = summary.work.sizeBigha,
        round = summary.work.roundMultiplier,
        workDoneBigha = BusinessCalculator.billableBigha(summary.work),
        rate = summary.work.ratePerBigha,
        workDate = summary.work.workDate,
        total = summary.work.totalAmount,
        paid = summary.paid,
        pending = summary.pending,
        status = summary.status.name.replace('_', ' '),
        billingType = summary.work.billingMode,
        durationMinutes = summary.work.durationMinutes,
    )

    fun writePdf(context: Context, data: ReceiptData, output: OutputStream) {
        val document = PdfDocument()
        try {
            val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val canvas = page.canvas
            drawPdfLogo(context, canvas, 485f, 32f, 50f)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(37, 107, 59) }
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 28f
            canvas.drawText(data.businessName.uppercase(), 297f, 78f, paint)
            paint.textSize = 11f
            paint.typeface = Typeface.DEFAULT
            if (data.ownerLine.isNotBlank()) canvas.drawText(data.ownerLine, 297f, 103f, paint)
            paint.color = Color.rgb(231, 169, 59)
            canvas.drawRect(55f, 128f, 540f, 133f, paint)

            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.DKGRAY
            paint.textSize = 14f
            var y = 174f
            val billingRows = if (data.billingType == BillingType.PER_MINUTE) {
                listOf("Duration" to "${data.durationMinutes} minutes", "Rate per Minute" to formatMoney(data.rate))
            } else {
                listOf(
                    "Field Size" to "${formatBigha(data.fieldSize)} Bigha",
                    "Round" to formatBigha(data.round),
                    "Work Done (Field Size × Round)" to "${formatBigha(data.workDoneBigha)} Bigha",
                    "Rate per Bigha per Round" to formatMoney(data.rate),
                )
            }
            val rows = buildList {
                add("Customer Name" to data.customerName)
                if (data.mobile.isNotBlank()) add("Mobile Number" to data.mobile)
                add("Field Name" to data.fieldName)
                addAll(billingRows)
                add("Work Date" to formatDate(data.workDate))
                add("Total Amount" to formatMoney(data.total))
                add("Paid Amount" to formatMoney(data.paid))
                add("Pending Amount" to formatMoney(data.pending))
                add("Payment Status" to data.status)
            }
            rows.forEach { (label, value) ->
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(label, 70f, y, paint)
                paint.typeface = Typeface.DEFAULT
                canvas.drawText(value, 285f, y, paint)
                y += 40f
            }
            paint.color = Color.LTGRAY
            canvas.drawLine(70f, y, 525f, y, paint)
            paint.color = Color.DKGRAY
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 12f
            canvas.drawText("Thank you for your business", 297f, y + 42f, paint)
            document.finishPage(page)
            document.writeTo(output)
        } finally {
            document.close()
        }
    }

    fun share(context: Context, data: ReceiptData, whatsapp: Boolean) {
        val directory = File(context.cacheDir, "receipts").apply { mkdirs() }
        val file = File(directory, "HarvestPay-${data.customerName.filter(Char::isLetterOrDigit)}-${data.workDate}.pdf")
        file.outputStream().use { writePdf(context, data, it) }
        sharePdf(context, FileProvider.getUriForFile(context, "${context.packageName}.files", file),
            "Harvest Pay receipt for ${data.customerName}", "Share receipt", whatsapp)
    }

    fun shareCustomer(context: Context, summary: CustomerSummary, settings: AppSettings, language: PdfLanguage) {
        val section = CustomerReportSection(
            title = "Current Account",
            year = java.time.LocalDate.now().year,
            openingOutstanding = summary.previousDue,
            openingAdvance = summary.openingAdvance,
            work = summary.work,
            payments = summary.payments,
            workTotal = summary.workTotal,
            paymentsTotal = summary.totalPaid,
            closingOutstanding = summary.pending,
            closingAdvance = summary.advance,
        )
        shareCustomer(context, CustomerAccountReport(summary, "Current Account", listOf(section)), settings, language)
    }

    fun shareCustomer(context: Context, accountReport: CustomerAccountReport, settings: AppSettings, language: PdfLanguage) {
        val directory = File(context.cacheDir, "reports").apply { mkdirs() }
        val safeName = accountReport.summary.customer.name.filter(Char::isLetterOrDigit).ifBlank { "Customer" }
        val safeType = accountReport.title.filter(Char::isLetterOrDigit)
        val file = File(directory, "HarvestPay-$safeName-$safeType.pdf")
        file.outputStream().use { writeCustomerPdf(context, accountReport, settings, language, it) }
        sharePdf(context, FileProvider.getUriForFile(context, "${context.packageName}.files", file),
            "Harvest Pay ${accountReport.title} for ${accountReport.summary.customer.name}", "Share customer PDF", false)
    }

    fun writeCustomerPdf(
        context: Context,
        summary: CustomerSummary,
        settings: AppSettings,
        language: PdfLanguage,
        output: OutputStream,
    ) {
        val section = CustomerReportSection(
            title = "Current Account",
            year = java.time.LocalDate.now().year,
            openingOutstanding = summary.previousDue,
            openingAdvance = summary.openingAdvance,
            work = summary.work,
            payments = summary.payments,
            workTotal = summary.workTotal,
            paymentsTotal = summary.totalPaid,
            closingOutstanding = summary.pending,
            closingAdvance = summary.advance,
        )
        writeCustomerPdf(context, CustomerAccountReport(summary, "Current Account", listOf(section)), settings, language, output)
    }

    fun writeCustomerPdf(
        context: Context,
        accountReport: CustomerAccountReport,
        settings: AppSettings,
        language: PdfLanguage,
        output: OutputStream,
    ) {
        val summary = accountReport.summary
        val typeface = ResourcesCompat.getFont(context, R.font.noto_sans_devanagari_regular) ?: Typeface.DEFAULT
        val report = CustomerPdfWriter(
            context,
            settings.businessName.ifBlank { "Harvest Pay" },
            listOf(settings.ownerName, settings.ownerMobile, settings.tractorNumber).filter(String::isNotBlank).joinToString(" • "),
            language,
            typeface,
        )
        try {
            report.section(label(language, "Customer details", "ग्राहक विवरण"))
            report.row(label(language, "Customer", "ग्राहक"), summary.customer.name)
            if (summary.customer.mobile.isNotBlank()) report.row(label(language, "Mobile", "मोबाइल"), summary.customer.mobile)
            if (summary.customer.village.isNotBlank()) report.row(label(language, "Village", "गाँव"), summary.customer.village)
            if (summary.customer.address.isNotBlank()) report.row(label(language, "Address", "पता"), summary.customer.address)
            report.row(label(language, "Fields", "खेत"), summary.fields.size.toString())
            report.row(label(language, "Registered area", "कुल पंजीकृत क्षेत्र"), "${formatBigha(summary.registeredBigha)} Bigha")

            report.section(label(language, accountReport.title, when (accountReport.title) {
                "Outstanding Account" -> "बकाया खाता"
                "Archived Year" -> "संग्रहीत वर्ष"
                "Complete History" -> "पूर्ण इतिहास"
                "Custom Date Range" -> "चुनी हुई तिथि सीमा"
                "Full Current Year" -> "पूर्ण चालू वर्ष"
                else -> "चालू खाता"
            }))
            accountReport.sections.forEachIndexed { index, section ->
                if (accountReport.sections.size > 1 || section.title != accountReport.title) report.section(section.title)
                report.row(label(language, "Account year", "खाता वर्ष"), section.year?.toString() ?: section.title)
                val originYear = if (index == accountReport.sections.lastIndex && accountReport.title in setOf("Current Account", "Outstanding Account")) summary.openingFromYear else section.year?.minus(1)
                if (section.openingOutstanding > 0.005) {
                    val openingLabel = originYear?.let { "Opening Outstanding from $it" } ?: "Opening Outstanding"
                    report.row(label(language, openingLabel, "पिछला बकाया"), formatMoney(section.openingOutstanding), true)
                }
                if (section.openingAdvance > 0.005) {
                    val openingLabel = originYear?.let { "Opening Advance from $it" } ?: "Opening Advance"
                    report.row(label(language, openingLabel, "पिछला अग्रिम"), formatMoney(section.openingAdvance), true)
                }

                report.section(label(language, "Date-wise work history", "दिनांकवार कार्य विवरण"))
                if (section.work.isEmpty()) report.paragraph(label(language, "No work has been recorded.", "कोई कार्य दर्ज नहीं है।"))
                else {
                    report.workHeader()
                    section.work.forEach(report::workRow)
                }
                report.row(label(language, "Total work charges", "कुल कार्य शुल्क"), formatMoney(section.workTotal), true)

                report.section(label(language, "Payments received", "प्राप्त भुगतान"))
                if (section.payments.isEmpty()) report.paragraph(label(language, "No payments have been recorded.", "कोई भुगतान दर्ज नहीं है।"))
                else {
                    report.paymentHeader()
                    section.payments.forEach {
                        report.paymentRow(formatDate(it.paymentDate), it.paymentMethod, it.notes, "-${formatMoney(it.amount)}")
                    }
                }
                report.row(label(language, "Total payments", "कुल भुगतान"), formatMoney(section.paymentsTotal), true)
                report.section(label(language, "Closing balance", "अंतिम शेष"))
                when {
                    section.closingOutstanding > 0.005 -> report.row(label(language, "Current due", "कुल बकाया"), formatMoney(section.closingOutstanding), true)
                    section.closingAdvance > 0.005 -> report.row(label(language, "Remaining advance", "शेष अग्रिम"), formatMoney(section.closingAdvance), true)
                    else -> report.row(label(language, "Current due", "कुल बकाया"), formatMoney(0.0), true)
                }
                report.row(label(language, "Account status", "खाता स्थिति"), if (section.closingOutstanding > 0.005) label(language, "UNPAID", "बकाया") else label(language, "SETTLED", "चुकता"), true)
            }
            if (summary.customer.notes.isNotBlank()) {
                report.section(label(language, "Customer notes", "ग्राहक नोट्स"))
                report.paragraph(summary.customer.notes)
            }
            report.writeTo(output)
        } finally {
            report.close()
        }
    }

    internal fun chronologicalWork(summary: CustomerSummary): List<WorkSummary> =
        summary.work.sortedWith(compareBy({ it.work.workDate }, { it.work.createdAt }, { it.work.id }))

    internal fun workType(work: WorkSummary, language: PdfLanguage): String {
        val hindi = work.work.workTypeHindi.ifBlank { defaultHindi(work.work.workType) }
        return when (language) {
            PdfLanguage.ENGLISH -> work.work.workType
            PdfLanguage.HINDI -> hindi.ifBlank { work.work.workType }
            PdfLanguage.BOTH -> if (hindi.isBlank()) work.work.workType else "${work.work.workType} / $hindi"
        }
    }

    private fun defaultHindi(name: String): String = when (name.trim().lowercase()) {
        "cultivating", "cultivator" -> "कल्टीवेटर"
        "rotavator", "rotavating" -> "रोटावेटर"
        "ploughing", "plowing" -> "जुताई"
        "sowing" -> "बुवाई"
        else -> ""
    }

    private fun sharePdf(context: Context, uri: android.net.Uri, text: String, chooserTitle: String, whatsapp: Boolean) {
        val baseIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, text)
            clipData = ClipData.newUri(context.contentResolver, "Harvest Pay PDF", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (whatsapp) {
            val direct = listOf("com.whatsapp", "com.whatsapp.w4b").asSequence()
                .map { Intent(baseIntent).setPackage(it) }
                .firstOrNull { it.resolveActivity(context.packageManager) != null }
            if (direct != null && runCatching { context.startActivity(direct) }.isSuccess) return
        }
        runCatching { context.startActivity(Intent.createChooser(baseIntent, if (chooserTitle == "Share customer PDF") com.harvestpay.app.localization.AppText.text(com.harvestpay.app.R.string.share_customer_pdf) else com.harvestpay.app.localization.AppText.label(chooserTitle))) }
    }
}

private fun label(language: PdfLanguage, english: String, hindi: String): String = when (language) {
    PdfLanguage.ENGLISH -> english
    PdfLanguage.HINDI -> hindi
    PdfLanguage.BOTH -> "$english / $hindi"
}

private class CustomerPdfWriter(
    private val context: Context,
    private val businessName: String,
    private val ownerLine: String,
    private val language: PdfLanguage,
    private val typeface: Typeface,
) : AutoCloseable {
    private val document = PdfDocument()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.typeface = this@CustomerPdfWriter.typeface }
    private var page: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var pageNumber = 0
    private var y = 0f
    private val left = 36f
    private val right = 559f

    init { nextPage() }

    fun section(title: String) {
        ensure(46f); y += 10f
        paint.color = Color.rgb(37, 107, 59); paint.typeface = Typeface.create(typeface, Typeface.BOLD); paint.textSize = 15f
        canvas!!.drawText(title, left, y, paint); y += 8f
        paint.color = Color.rgb(231, 169, 59); canvas!!.drawRect(left, y, right, y + 3f, paint); y += 20f
    }

    fun row(label: String, value: String, emphasize: Boolean = false) {
        ensure(24f)
        paint.color = if (emphasize) Color.rgb(37, 107, 59) else Color.DKGRAY
        paint.textSize = 10.5f; paint.typeface = Typeface.create(typeface, Typeface.BOLD)
        canvas!!.drawText(ellipsize(label, 320f), left + 8f, y, paint)
        paint.textAlign = Paint.Align.RIGHT; canvas!!.drawText(ellipsize(value, 180f), right - 8f, y, paint)
        paint.textAlign = Paint.Align.LEFT; y += 23f
    }

    fun paragraph(text: String) {
        if (text.isBlank()) return
        paint.color = Color.DKGRAY; paint.typeface = typeface; paint.textSize = 10.5f
        wrap(text, right - left - 16f).forEach { line -> ensure(17f); canvas!!.drawText(line, left + 8f, y, paint); y += 16f }
        y += 4f
    }

    fun workHeader() {
        ensure(27f); paint.color = Color.rgb(235, 241, 236); canvas!!.drawRect(left, y - 14f, right, y + 8f, paint)
        paint.color = Color.DKGRAY; paint.typeface = Typeface.create(typeface, Typeface.BOLD); paint.textSize = 7.7f
        canvas!!.drawText(label(language, "Date", "दिनांक"), left + 3f, y, paint)
        canvas!!.drawText(label(language, "Work type", "कार्य"), left + 66f, y, paint)
        canvas!!.drawText(label(language, "Field", "खेत"), left + 203f, y, paint)
        canvas!!.drawText(label(language, "Area / Duration", "क्षेत्र / समय"), left + 305f, y, paint)
        canvas!!.drawText(label(language, "Rate", "दर"), left + 397f, y, paint)
        paint.textAlign = Paint.Align.RIGHT; canvas!!.drawText(label(language, "Charges", "शुल्क"), right - 3f, y, paint)
        paint.textAlign = Paint.Align.LEFT; y += 24f
    }

    fun workRow(summary: WorkSummary) {
        ensure(25f)
        val work = summary.work
        paint.color = Color.DKGRAY; paint.typeface = typeface; paint.textSize = 8.1f
        canvas!!.drawText(formatDate(work.workDate), left + 3f, y, paint)
        canvas!!.drawText(ellipsize(ReceiptGenerator.workType(summary, language), 130f), left + 66f, y, paint)
        canvas!!.drawText(ellipsize(summary.field?.fieldName ?: label(language, "Field", "खेत"), 96f), left + 203f, y, paint)
        val quantity = if (work.billingMode == BillingType.PER_MINUTE) "${work.durationMinutes} min" else "${formatBigha(BusinessCalculator.billableBigha(work))} Bigha"
        val rate = if (work.billingMode == BillingType.PER_MINUTE) "${formatMoney(work.ratePerBigha)}/min" else "${formatMoney(work.ratePerBigha)}/Bigha"
        canvas!!.drawText(ellipsize(quantity, 86f), left + 305f, y, paint)
        canvas!!.drawText(ellipsize(rate, 83f), left + 397f, y, paint)
        paint.textAlign = Paint.Align.RIGHT; canvas!!.drawText(formatMoney(work.totalAmount), right - 3f, y, paint)
        paint.textAlign = Paint.Align.LEFT; y += 22f
    }

    fun paymentHeader() {
        ensure(25f); paint.color = Color.rgb(235, 241, 236); canvas!!.drawRect(left, y - 14f, right, y + 7f, paint)
        paint.color = Color.DKGRAY; paint.typeface = Typeface.create(typeface, Typeface.BOLD); paint.textSize = 8.5f
        canvas!!.drawText(label(language, "Date", "दिनांक"), left + 5f, y, paint)
        canvas!!.drawText(label(language, "Method / notes", "माध्यम / नोट्स"), left + 110f, y, paint)
        paint.textAlign = Paint.Align.RIGHT; canvas!!.drawText(label(language, "Amount", "राशि"), right - 5f, y, paint)
        paint.textAlign = Paint.Align.LEFT; y += 24f
    }

    fun paymentRow(date: String, method: String, notes: String, amount: String) {
        ensure(23f); paint.color = Color.DKGRAY; paint.typeface = typeface; paint.textSize = 9.5f
        canvas!!.drawText(date, left + 5f, y, paint)
        canvas!!.drawText(ellipsize(listOf(method, notes).filter(String::isNotBlank).joinToString(" • "), 315f), left + 110f, y, paint)
        paint.textAlign = Paint.Align.RIGHT; canvas!!.drawText(amount, right - 5f, y, paint)
        paint.textAlign = Paint.Align.LEFT; y += 21f
    }

    fun writeTo(output: OutputStream) { finishPage(); document.writeTo(output) }

    override fun close() { if (page != null) finishPage(); document.close() }

    private fun nextPage() {
        pageNumber += 1
        page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()); canvas = page!!.canvas
        drawPdfLogo(context, canvas!!, 505f, 16f, 50f)
        paint.textAlign = Paint.Align.LEFT; paint.color = Color.rgb(37, 107, 59)
        paint.typeface = Typeface.create(typeface, Typeface.BOLD); paint.textSize = 21f
        canvas!!.drawText(businessName.uppercase(), left, 48f, paint)
        paint.textAlign = Paint.Align.RIGHT; paint.textSize = 10f; paint.typeface = typeface
        canvas!!.drawText(ellipsize(label(language, "Customer account report", "ग्राहक खाता रिपोर्ट"), 200f), 490f, 45f, paint)
        if (ownerLine.isNotBlank()) canvas!!.drawText(ellipsize(ownerLine, 220f), 490f, 61f, paint)
        paint.textAlign = Paint.Align.LEFT; paint.color = Color.rgb(231, 169, 59)
        canvas!!.drawRect(left, 72f, right, 75f, paint); y = 104f
    }

    private fun ensure(height: Float) { if (y + height > 790f) { finishPage(); nextPage() } }

    private fun finishPage() {
        val current = page ?: return
        paint.textAlign = Paint.Align.CENTER; paint.typeface = typeface; paint.textSize = 9f; paint.color = Color.GRAY
        canvas!!.drawText("${label(language, "Made with love for every farmer", "हर किसान के लिए प्रेम से बनाया गया")} • ${label(language, "Page", "पृष्ठ")} $pageNumber", 297f, 818f, paint)
        paint.textAlign = Paint.Align.LEFT; document.finishPage(current); page = null; canvas = null
    }

    private fun wrap(text: String, maxWidth: Float): List<String> {
        val words = text.replace('\n', ' ').split(Regex("\\s+")).filter(String::isNotBlank)
        val lines = mutableListOf<String>(); var line = ""
        words.forEach { word ->
            val candidate = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(candidate) <= maxWidth) line = candidate else { if (line.isNotBlank()) lines += line; line = word }
        }
        if (line.isNotBlank()) lines += line
        return lines
    }

    private fun ellipsize(text: String, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 1 && paint.measureText(text.take(end) + "…") > maxWidth) end -= 1
        return text.take(end) + "…"
    }
}
