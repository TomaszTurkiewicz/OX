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

class LeftArrowDrawable(private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.isAntiAlias = true

        val leftArrowPath = MyPath()

        leftArrowPath.move(Point(bounds.width(),0))
        leftArrowPath.line(Point(0,bounds.centerY()))
        leftArrowPath.line(Point(bounds.width(),bounds.height()))

        canvas.drawPath(leftArrowPath,paint)

    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}