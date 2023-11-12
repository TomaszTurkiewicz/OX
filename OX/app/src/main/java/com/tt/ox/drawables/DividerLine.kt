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

class DividerLine (private val context: Context): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        paint.strokeWidth = bounds.height().toFloat()
        paint.style = Paint.Style.STROKE

        val n = 20
        val single = bounds.width()/n
        val dif = single*0.15

        val line = MyPath()
        for(i in 0 until n){
            line.move(Point((i * single + dif).toInt(),bounds.centerY()))
            line.line(Point((i*single+single-dif).toInt(),bounds.centerY()))
        }
        canvas.drawPath(line,paint)

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