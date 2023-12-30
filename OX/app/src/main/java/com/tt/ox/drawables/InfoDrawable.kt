package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Theme

class InfoDrawable (private val context: Context, private val active:Boolean) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = bounds.width()*0.1f
        paint.color = if(active) ContextCompat.getColor(context, Theme(context).getBlueColor()) else ContextCompat.getColor(context, Theme(context).getGrayColor())

        val top = Point(bounds.centerX(), (bounds.height()*0.5).toInt())
        val offset = bounds.width()*0.1
        val hook = Point((top.x-offset).toInt(), (top.y).toInt())
        val bottom = Point(bounds.centerX(), (bounds.height()*0.8).toInt())

        canvas.drawLine(
            (top.x+bounds.width()*0.05f).toFloat(),
            top.y.toFloat(),
            hook.x.toFloat(),
            hook.y.toFloat(),
            paint)


        canvas.drawLine(
            top.x.toFloat(),
            top.y.toFloat(),
            bottom.x.toFloat(),
            bottom.y.toFloat(),
            paint)

        val left = Point((bounds.width()*0.4).toInt(),bottom.y)
        val right = Point((bounds.width()*0.6).toInt(),bottom.y)

        canvas.drawLine(
            left.x.toFloat(),
            left.y.toFloat(),
            right.x.toFloat(),
            right.y.toFloat(),
            paint)

        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        val radius = bounds.width()*0.1f
        val dif = bounds.width()*0.2f

        canvas.drawCircle(
            top.x.toFloat(),
            top.y-dif,
            radius,
            paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}