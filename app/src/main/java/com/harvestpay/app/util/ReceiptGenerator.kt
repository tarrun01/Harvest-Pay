package com.harvestpay.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.harvestpay.app.data.AppSettings
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
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Harvest Pay receipt for ${data.customerName}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (whatsapp) setPackage("com.whatsapp")
        }
        runCatching { context.startActivity(Intent.createChooser(intent, "Share receipt")) }
            .recoverCatching {
                intent.setPackage(null)
                context.startActivity(Intent.createChooser(intent, "Share receipt"))
            }
    }
}
