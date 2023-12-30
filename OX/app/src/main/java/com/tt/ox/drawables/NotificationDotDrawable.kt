package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Theme

class NotificationDotDrawable (private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f
        paint.color = ContextCompat.getColor(context, Theme(context).getRedColor())
        paint.isAntiAlias = true
        val radius = bounds.centerX().toFloat()

        canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(),radius,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}