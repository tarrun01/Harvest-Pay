package com.harvestpay.app.util

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.harvestpay.app.R

/** Draws the existing launcher artwork without changing its aspect ratio. */
internal fun drawPdfLogo(context: Context, canvas: Canvas, left: Float, top: Float, size: Float) {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground) ?: return
    val bounds = RectF(left, top, left + size, top + size)
    val path = Path().apply { addOval(bounds, Path.Direction.CW) }
    val save = canvas.save()
    canvas.clipPath(path)
    canvas.drawColor(Color.WHITE)
    canvas.drawBitmap(bitmap, null, bounds, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    canvas.restoreToCount(save)
    bitmap.recycle()
}
