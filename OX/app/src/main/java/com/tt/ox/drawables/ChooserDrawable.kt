package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Theme

class ChooserDrawable (private val context: Context): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        val radius = bounds.height()/3

        canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(),
            radius.toFloat(),paint)


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