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

class SearchDrawable (private val context: Context, private val active:Boolean) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.strokeWidth = bounds.width()*0.1f
        paint.style = Paint.Style.STROKE
        paint.color = if(active) ContextCompat.getColor(context, Theme(context).getBlueColor()) else ContextCompat.getColor(context, Theme(context).getGrayColor())
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        val dif = 0
        val handle = bounds.width()*0.3
        val middle = Point(bounds.centerX()+dif, bounds.centerY()-dif)
        val handleEnd = Point((middle.x-handle).toInt(), (middle.y+handle).toInt())

        val handlePath = MyPath()
        handlePath.move(middle)
        handlePath.line(handleEnd)

        canvas.drawPath(handlePath,paint)

        val radius = bounds.width()*0.25
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context,Theme(context).getBackgroundColor())
        canvas.drawCircle(middle.x.toFloat(), middle.y.toFloat(), radius.toFloat(),paint)

        paint.style = Paint.Style.STROKE
        paint.color = if(active) ContextCompat.getColor(context, Theme(context).getBlueColor()) else ContextCompat.getColor(context, Theme(context).getGrayColor())
        canvas.drawCircle(middle.x.toFloat(), middle.y.toFloat(), radius.toFloat(),paint)



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