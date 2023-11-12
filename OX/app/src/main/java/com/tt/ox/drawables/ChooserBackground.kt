package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Theme

class ChooserBackground (private val context: Context): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        val strokeWidth = bounds.height()*0.05f
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.FILL_AND_STROKE
        val radius = (bounds.height()/2)-strokeWidth
        val radius2 = (bounds.height()/2)-(2*strokeWidth)

        canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(),
            radius,paint)

        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context,Theme(context).getBackgroundColor())

        canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(),
            radius2,paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}