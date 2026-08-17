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
import com.harvestpay.app.data.AppSettings
import com.harvestpay.app.domain.CustomerSummary
import com.harvestpay.app.domain.WorkSummary
import java.io.File
import java.io.OutputStream

data class ReceiptData(
    val businessName: String,
    val ownerLine: String,
    val customerName: String,
    val mobile: String,
    val fieldName: String,
    val fieldSize: Double,
    val rate: Double,
    val workDate: Long,
    val total: Double,
    val paid: Double,
    val pending: Double,
    val status: String,
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
        rate = summary.work.ratePerBigha,
        workDate = summary.work.workDate,
        total = summary.work.totalAmount,
        paid = summary.paid,
        pending = summary.pending,
        status = summary.status.name.replace('_', ' '),
    )

    fun writePdf(data: ReceiptData, output: OutputStream) {
        val document = PdfDocument()
        try {
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.rgb(37, 107, 59) }

            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 28f
            canvas.drawText(data.businessName.uppercase(), 297f, 78f, paint)
            paint.textSize = 11f
            paint.typeface = Typeface.DEFAULT
            if (data.ownerLine.isNotBlank()) canvas.drawText(data.ownerLine, 297f, 103f, paint)

            paint.color = android.graphics.Color.rgb(231, 169, 59)
            canvas.drawRect(55f, 128f, 540f, 133f, paint)

            paint.textAlign = Paint.Align.LEFT
            paint.color = android.graphics.Color.DKGRAY
            paint.textSize = 14f
            var y = 174f
            val rows = listOf(
                "Customer Name" to data.customerName,
                "Mobile Number" to data.mobile,
                "Field Name" to data.fieldName,
                "Field Size" to "${formatBigha(data.fieldSize)} Bigha",
                "Rate per Bigha" to formatMoney(data.rate),
                "Work Date" to formatDate(data.workDate),
                "Total Amount" to formatMoney(data.total),
                "Paid Amount" to formatMoney(data.paid),
                "Pending Amount" to formatMoney(data.pending),
                "Payment Status" to data.status,
            )
            rows.forEach { (label, value) ->
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(label, 70f, y, paint)
                paint.typeface = Typeface.DEFAULT
                canvas.drawText(value, 260f, y, paint)
                y += 43f
            }

            paint.color = android.graphics.Color.LTGRAY
            canvas.drawLine(70f, y, 525f, y, paint)
            y += 42f
            paint.color = android.graphics.Color.DKGRAY
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 12f
            canvas.drawText("Thank you for your business", 297f, y, paint)
            document.finishPage(page)
            document.writeTo(output)
        } finally {
            document.close()
        }
    }

    fun share(context: Context, data: ReceiptData, whatsapp: Boolean) {
        val directory = File(context.cacheDir, "receipts").apply { mkdirs() }
        val file = File(directory, "HarvestPay-${data.customerName.filter(Char::isLetterOrDigit)}-${data.workDate}.pdf")
        file.outputStream().use { writePdf(data, it) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        sharePdf(
            context = context,
            uri = uri,
            text = "Harvest Pay receipt for ${data.customerName}",
            chooserTitle = "Share receipt",
            whatsapp = whatsapp,
        )
    }

    fun shareCustomer(context: Context, summary: CustomerSummary, settings: AppSettings) {
        val directory = File(context.cacheDir, "reports").apply { mkdirs() }
        val safeName = summary.customer.name.filter(Char::isLetterOrDigit).ifBlank { "Customer" }
        val file = File(directory, "HarvestPay-$safeName-Account.pdf")
        file.outputStream().use { writeCustomerPdf(summary, settings, it) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
        sharePdf(
            context = context,
            uri = uri,
            text = "Harvest Pay account report for ${summary.customer.name}",
            chooserTitle = "Share customer PDF",
            whatsapp = false,
        )
    }

    fun writeCustomerPdf(
        summary: CustomerSummary,
        settings: AppSettings,
        output: OutputStream,
    ) {
        val report = CustomerPdfWriter(
            businessName = settings.businessName.ifBlank { "Harvest Pay" },
            ownerLine = listOf(settings.ownerName, settings.ownerMobile, settings.tractorNumber)
                .filter(String::isNotBlank).joinToString(" • "),
        )
        try {
            report.section("Customer details")
            report.row("Customer", summary.customer.name)
            report.row("Mobile", summary.customer.mobile)
            if (summary.customer.village.isNotBlank()) report.row("Village", summary.customer.village)
            if (summary.customer.address.isNotBlank()) report.row("Address", summary.customer.address)
            report.row("Fields", summary.fields.size.toString())
            report.row("Total Bigha", "${formatBigha(summary.registeredBigha)} Bigha")

            report.section("Account summary")
            report.row("Previous due", formatMoney(summary.previousDue), emphasize = summary.previousDue > 0.005)
            report.row("Total work done", "${summary.work.size} work entries")
            report.row("Total area worked", "${formatBigha(summary.totalBigha)} Bigha")
            report.row("Total work charges", formatMoney(summary.workTotal))
            report.row("Total bill", formatMoney(summary.totalBill), emphasize = true)
            report.row("Payments received", formatMoney(summary.totalPaid))
            report.row("Outstanding balance", formatMoney(summary.pending), emphasize = summary.pending > 0.005)
            if (summary.advance > 0.005) {
                report.row("Advance credit", formatMoney(summary.advance), emphasize = true)
            }

            report.section("Field-wise work history")
            if (summary.fields.isEmpty()) {
                report.paragraph("No fields have been added for this customer.")
            }
            val workByField = summary.work.groupBy { it.work.fieldId }
            summary.fields.sortedBy { it.fieldName.lowercase() }.forEach { field ->
                val fieldWork = workByField[field.id].orEmpty().sortedBy { it.work.workDate }
                report.subsection(field.fieldName)
                report.paragraph(
                    buildList {
                        add("Area: ${formatBigha(field.sizeBigha)} Bigha")
                        if (field.fieldNumber.isNotBlank()) add("No: ${field.fieldNumber}")
                        if (field.location.isNotBlank()) add("Location: ${field.location}")
                    }.joinToString(" • "),
                )
                if (fieldWork.isEmpty()) {
                    report.paragraph("No work recorded for this field.")
                } else {
                    report.workHeader()
                    fieldWork.forEach(report::workRow)
                    report.row(
                        "Field total (${fieldWork.size} entries)",
                        formatMoney(fieldWork.sumOf { it.work.totalAmount }),
                        emphasize = true,
                    )
                }
                if (field.notes.isNotBlank()) report.paragraph("Field notes: ${field.notes}")
            }

            val unassigned = workByField[null].orEmpty() + summary.work.filter { work ->
                work.work.fieldId != null && summary.fields.none { it.id == work.work.fieldId }
            }
            if (unassigned.isNotEmpty()) {
                report.subsection("Other / unassigned work")
                report.workHeader()
                unassigned.distinctBy { it.work.id }.sortedBy { it.work.workDate }.forEach(report::workRow)
                report.row(
                    "Other work total",
                    formatMoney(unassigned.distinctBy { it.work.id }.sumOf { it.work.totalAmount }),
                    emphasize = true,
                )
            }

            report.section("Payments received")
            if (summary.payments.isEmpty()) {
                report.paragraph("No payments have been recorded.")
            } else {
                report.paymentHeader()
                summary.payments.sortedBy { it.paymentDate }.forEach {
                    report.paymentRow(formatDate(it.paymentDate), it.paymentMethod, it.notes, formatMoney(it.amount))
                }
                report.row("Total payments", formatMoney(summary.totalPaid), emphasize = true)
            }

            if (summary.customer.notes.isNotBlank()) {
                report.section("Customer notes")
                report.paragraph(summary.customer.notes)
            }
            report.section("Closing balance")
            when {
                summary.pending > 0.005 -> report.row("Total due", formatMoney(summary.pending), emphasize = true)
                summary.advance > 0.005 -> report.row("Advance available", formatMoney(summary.advance), emphasize = true)
                else -> report.paragraph("Account fully settled.")
            }
            report.writeTo(output)
        } finally {
            report.close()
        }
    }

    private fun sharePdf(
        context: Context,
        uri: android.net.Uri,
        text: String,
        chooserTitle: String,
        whatsapp: Boolean,
    ) {
        val baseIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, text)
            clipData = ClipData.newUri(context.contentResolver, "Harvest Pay PDF", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (whatsapp) {
            val packages = listOf("com.whatsapp", "com.whatsapp.w4b")
            val direct = packages.asSequence()
                .map { Intent(baseIntent).setPackage(it) }
                .firstOrNull { it.resolveActivity(context.packageManager) != null }
            if (direct != null && runCatching { context.startActivity(direct) }.isSuccess) return
        }
        runCatching { context.startActivity(Intent.createChooser(baseIntent, chooserTitle)) }
    }
}

private class CustomerPdfWriter(
    private val businessName: String,
    private val ownerLine: String,
) : AutoCloseable {
    private val document = PdfDocument()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var page: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var pageNumber = 0
    private var y = 0f
    private val left = 48f
    private val right = 547f

    init {
        nextPage()
    }

    fun section(title: String) {
        ensure(46f)
        y += 10f
        paint.color = Color.rgb(37, 107, 59)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 15f
        canvas!!.drawText(title, left, y, paint)
        y += 8f
        paint.color = Color.rgb(231, 169, 59)
        canvas!!.drawRect(left, y, right, y + 3f, paint)
        y += 20f
    }

    fun subsection(title: String) {
        ensure(32f)
        paint.color = Color.rgb(37, 107, 59)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12.5f
        canvas!!.drawText(title, left, y, paint)
        y += 22f
    }

    fun row(label: String, value: String, emphasize: Boolean = false) {
        ensure(24f)
        paint.color = if (emphasize) Color.rgb(37, 107, 59) else Color.DKGRAY
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas!!.drawText(label, left + 8f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas!!.drawText(value, right - 8f, y, paint)
        paint.textAlign = Paint.Align.LEFT
        y += 23f
    }

    fun paragraph(text: String) {
        if (text.isBlank()) return
        paint.color = Color.DKGRAY
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10.5f
        wrap(text, right - left).forEach { line ->
            ensure(17f)
            canvas!!.drawText(line, left + 8f, y, paint)
            y += 16f
        }
        y += 4f
    }

    fun workHeader() {
        ensure(25f)
        paint.color = Color.rgb(235, 241, 236)
        canvas!!.drawRect(left, y - 14f, right, y + 7f, paint)
        paint.color = Color.DKGRAY
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9f
        canvas!!.drawText("Date", left + 5f, y, paint)
        canvas!!.drawText("Work type", left + 85f, y, paint)
        canvas!!.drawText("Area", left + 285f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas!!.drawText("Charges", right - 5f, y, paint)
        paint.textAlign = Paint.Align.LEFT
        y += 24f
    }

    fun workRow(work: WorkSummary) {
        ensure(23f)
        paint.color = Color.DKGRAY
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9.5f
        canvas!!.drawText(formatDate(work.work.workDate), left + 5f, y, paint)
        canvas!!.drawText(ellipsize(work.work.workType, 175f), left + 85f, y, paint)
        canvas!!.drawText("${formatBigha(work.work.sizeBigha)} Bigha", left + 285f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas!!.drawText(formatMoney(work.work.totalAmount), right - 5f, y, paint)
        paint.textAlign = Paint.Align.LEFT
        y += 21f
    }

    fun paymentHeader() {
        ensure(25f)
        paint.color = Color.rgb(235, 241, 236)
        canvas!!.drawRect(left, y - 14f, right, y + 7f, paint)
        paint.color = Color.DKGRAY
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9f
        canvas!!.drawText("Date", left + 5f, y, paint)
        canvas!!.drawText("Method / notes", left + 100f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas!!.drawText("Amount", right - 5f, y, paint)
        paint.textAlign = Paint.Align.LEFT
        y += 24f
    }

    fun paymentRow(date: String, method: String, notes: String, amount: String) {
        ensure(23f)
        paint.color = Color.DKGRAY
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9.5f
        canvas!!.drawText(date, left + 5f, y, paint)
        canvas!!.drawText(ellipsize(listOf(method, notes).filter(String::isNotBlank).joinToString(" • "), 285f), left + 100f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas!!.drawText(amount, right - 5f, y, paint)
        paint.textAlign = Paint.Align.LEFT
        y += 21f
    }

    fun writeTo(output: OutputStream) {
        finishPage()
        document.writeTo(output)
    }

    override fun close() {
        if (page != null) finishPage()
        document.close()
    }

    private fun nextPage() {
        pageNumber += 1
        page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, pageNumber).create())
        canvas = page!!.canvas
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.rgb(37, 107, 59)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 21f
        canvas!!.drawText(businessName.uppercase(), left, 48f, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas!!.drawText("Customer account report", right, 45f, paint)
        if (ownerLine.isNotBlank()) canvas!!.drawText(ownerLine, right, 60f, paint)
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.rgb(231, 169, 59)
        canvas!!.drawRect(left, 72f, right, 75f, paint)
        y = 104f
    }

    private fun ensure(height: Float) {
        if (y + height <= 790f) return
        finishPage()
        nextPage()
    }

    private fun finishPage() {
        val current = page ?: return
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f
        paint.color = Color.GRAY
        canvas!!.drawText("Made with love for every farmer • Page $pageNumber", 297f, 818f, paint)
        paint.textAlign = Paint.Align.LEFT
        document.finishPage(current)
        page = null
        canvas = null
    }

    private fun wrap(text: String, maxWidth: Float): List<String> {
        val words = text.replace('\n', ' ').split(Regex("\\s+")).filter(String::isNotBlank)
        val lines = mutableListOf<String>()
        var line = ""
        words.forEach { word ->
            val candidate = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(candidate) <= maxWidth) {
                line = candidate
            } else {
                if (line.isNotBlank()) lines += line
                line = word
            }
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
