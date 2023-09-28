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

class SwitchDrawable (private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val diff = bounds.height()/2
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.green)
        paint.isAntiAlias = true

        val pathMiddle = MyPath()
        pathMiddle.move(Point(diff, (bounds.height()*0.4).toInt()))
        pathMiddle.line(Point(bounds.width()-diff, (bounds.height()*0.4).toInt()))
        pathMiddle.line(Point(bounds.width()-diff, (bounds.height()*0.6).toInt()))
        pathMiddle.line(Point(diff, (bounds.height()*0.6).toInt()))
        pathMiddle.line(Point(diff, (bounds.height()*0.4).toInt()))
        pathMiddle.close()

        canvas.drawPath(pathMiddle,paint)

        val pathRight = MyPath()
        pathRight.move(Point(bounds.width()-diff,0))
        pathRight.line(Point(bounds.width()-diff,bounds.height()))
        pathRight.line(Point(bounds.width(),bounds.centerY()))
        pathRight.line(Point(bounds.width()-diff,0))
        pathRight.close()

        canvas.drawPath(pathRight,paint)

        val pathLeft = MyPath()
        pathLeft.move(Point(diff,0))
        pathLeft.line(Point(diff,bounds.height()))
        pathLeft.line(Point(0,bounds.centerY()))
        pathLeft.line(Point(diff,0))
        pathLeft.close()

        canvas.drawPath(pathLeft,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}