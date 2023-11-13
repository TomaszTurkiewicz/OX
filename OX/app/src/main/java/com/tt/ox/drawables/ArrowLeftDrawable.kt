package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme

class ArrowLeftDrawable (private val context: Context, private val enable:Boolean) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.color = if (enable) ContextCompat.getColor(
            context,
            Theme(context).getAccentColor()
        ) else ContextCompat.getColor(
            context,
            Theme(context).getControlsDisableColor())

        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        val roundedCorner = bounds.width()*0.1f
        paint.pathEffect = CornerPathEffect(roundedCorner)
        paint.strokeCap = Paint.Cap.ROUND

        val pointer = Point((bounds.width()*0.2).toInt(),bounds.centerY())
        val upper = Point((bounds.width()*0.8).toInt(), (bounds.height()*0.2).toInt())
        val bottom = Point(upper.x,bounds.height()-upper.y)

        val arrow = MyPath()
        arrow.move(pointer)
        arrow.line(upper)
        arrow.line(bottom)
        arrow.line(pointer)
        arrow.close()

        canvas.drawPath(arrow,paint)

    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}