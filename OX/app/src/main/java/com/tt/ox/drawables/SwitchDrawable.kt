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
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.green)
        paint.isAntiAlias = true

        val left = Point((bounds.width()*0.2).toInt(),bounds.centerY())
        val leftTop = Point((bounds.width()*0.4).toInt(), (bounds.height()*0.2).toInt())
        val leftBottom = Point(leftTop.x,bounds.height()-leftTop.y)

        val right = Point(bounds.width()-left.x,bounds.centerY())
        val rightTop = Point(bounds.width()-leftTop.x,leftTop.y)
        val rightBottom = Point(bounds.width()-leftBottom.x,leftBottom.y)

        val pathRight = MyPath()
        pathRight.move(right)
        pathRight.line(rightBottom)
        pathRight.line(rightTop)
        pathRight.line(right)
        pathRight.close()

        canvas.drawPath(pathRight,paint)

        val pathLeft = MyPath()
        pathLeft.move(left)
        pathLeft.line(leftTop)
        pathLeft.line(leftBottom)
        pathLeft.line(left)
        pathLeft.close()

        canvas.drawPath(pathLeft,paint)

        val topLeft = Point(leftTop.x, (bounds.height()*0.4).toInt())
        val topRight = Point(rightTop.x, topLeft.y)
        val bottomLeft = Point(leftTop.x, (bounds.height()*0.6).toInt())
        val bottomRight = Point(rightTop.x, bottomLeft.y)

        val middlePath = MyPath()
        middlePath.move(topLeft)
        middlePath.line(topRight)
        middlePath.move(bottomLeft)
        middlePath.line(bottomRight)

        canvas.drawPath(middlePath,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}