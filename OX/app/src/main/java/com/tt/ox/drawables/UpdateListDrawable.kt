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

class UpdateListDrawable (private val context: Context, val active:Boolean) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = bounds.width()*0.06f
        paint.color = if(active) ContextCompat.getColor(context, R.color.green) else ContextCompat.getColor(context, R.color.gray)
        canvas.drawCircle(bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),bounds.width()*0.25f,paint)

        paint.color = ContextCompat.getColor(context, R.color.white)
        paint.strokeWidth = bounds.width()*0.1f
        val erase = MyPath()
        erase.move(Point((bounds.width()*0.9).toInt(), (bounds.height()*0.1).toInt()))
        erase.line(Point((bounds.width()*0.1).toInt(), (bounds.height()*0.9).toInt()))

        canvas.drawPath(erase,paint)

        paint.color = if(active) ContextCompat.getColor(context, R.color.green) else ContextCompat.getColor(context, R.color.gray)
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL

        val upperTriangle = MyPath()
        val top = Point(bounds.centerX(),(bounds.height()*0.1).toInt())
        val bottom = Point(bounds.centerX(), (bounds.height()*0.4).toInt())
        val right = Point((bounds.centerX()+bounds.width()*0.2).toInt(),(top.y+bottom.y)/2)

        upperTriangle.move(top)
        upperTriangle.line(bottom)
        upperTriangle.line(right)
        upperTriangle.line(top)
        upperTriangle.close()

        canvas.drawPath(upperTriangle,paint)

        val lowerTriangle = MyPath()
        val lTop = Point(bounds.centerX(),(bounds.height()*0.6).toInt())
        val lBottom = Point(bounds.centerX(), (bounds.height()*0.9).toInt())
        val lRight = Point((bounds.centerX()-bounds.width()*0.2).toInt(),(lTop.y+lBottom.y)/2)

        lowerTriangle.move(lTop)
        lowerTriangle.line(lBottom)
        lowerTriangle.line(lRight)
        lowerTriangle.line(lTop)
        lowerTriangle.close()

        canvas.drawPath(lowerTriangle,paint)

    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}