package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.helpers.MyPath

class PointerUpperDrawable (private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.green)
        paint.isAntiAlias = true

        val path = MyPath()
        path.move(Point(bounds.centerX(),bounds.height()))
        path.line(Point(0,bounds.centerY()))
        path.line(Point((bounds.width()*0.4).toInt(),bounds.centerY()))
        path.line(Point((bounds.width()*0.4).toInt(),0))
        path.line(Point((bounds.width()*0.6).toInt(),0))
        path.line(Point((bounds.width()*0.6).toInt(),bounds.centerY()))
        path.line(Point(bounds.width(),bounds.centerY()))
        path.line(Point(bounds.centerX(),bounds.height()))
        path.close()

        canvas.drawPath(path,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}