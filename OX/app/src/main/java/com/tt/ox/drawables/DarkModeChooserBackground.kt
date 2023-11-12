package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme

class DarkModeChooserBackground (private val context: Context): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        val strokeWidth = bounds.height()*0.05f
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        val radius = (bounds.height()/2)-strokeWidth
        val radius2 = (bounds.height()/2)-(2*strokeWidth)

        val line = MyPath()
        line.move(Point((bounds.width()*0.25).toInt(),bounds.centerY()))
        line.line(Point((bounds.width()*0.75).toInt(),bounds.centerY()))
        canvas.drawPath(line,paint)

        paint.style = Paint.Style.FILL_AND_STROKE

        canvas.drawCircle((bounds.width()*0.25).toFloat(), bounds.centerY().toFloat(),radius,paint)
        canvas.drawCircle((bounds.width()*0.5).toFloat(), bounds.centerY().toFloat(),radius,paint)
        canvas.drawCircle((bounds.width()*0.75).toFloat(), bounds.centerY().toFloat(),radius,paint)


        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context,Theme(context).getBackgroundColor())

        canvas.drawCircle((bounds.width()*0.25).toFloat(), bounds.centerY().toFloat(),radius2,paint)
        canvas.drawCircle((bounds.width()*0.5).toFloat(), bounds.centerY().toFloat(),radius2,paint)
        canvas.drawCircle((bounds.width()*0.75).toFloat(), bounds.centerY().toFloat(),radius2,paint)
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