package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

class OtherGamesDrawable (private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val unit = ScreenMetricsCompat().getUnit(context)
        val leftCenter = Point(bounds.width()/4,bounds.centerY())
        val rightCenter = Point(bounds.width()*3/4,bounds.centerY())
        val radius = bounds.centerX()/3
        val stroke = unit*0.05f
        paint.strokeWidth = stroke
        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        paint.isAntiAlias = true

        val leftCircle = RectF(
            (leftCenter.x-radius).toFloat(),
            (leftCenter.y-radius).toFloat(),
            (leftCenter.x+radius).toFloat(),
            (leftCenter.y+radius).toFloat()
        )
        val rightCircle = RectF(
            (rightCenter.x-radius).toFloat(),
            (rightCenter.y-radius).toFloat(),
            (rightCenter.x+radius).toFloat(),
            (rightCenter.y+radius).toFloat()
        )

        canvas.drawArc(leftCircle,0f,270f,false,paint)
        canvas.drawArc(rightCircle,270f,270f,false,paint)

        val topLeft = Point(leftCenter.x,leftCenter.y-radius)
        val topRight = Point(rightCenter.x,rightCenter.y-radius)

        val topLine = MyPath()
        topLine.move(topLeft)
        topLine.line(topRight)
        canvas.drawPath(topLine,paint)


        val bottomLeft = Point(leftCenter.x+radius,leftCenter.y)
        val bottomRight = Point(rightCenter.x-radius,rightCenter.y)

        val bottomLine = MyPath()
        bottomLine.move(bottomLeft)
        bottomLine.line(bottomRight)
        canvas.drawPath(bottomLine,paint)

        val newStroke = stroke*2
        paint.strokeWidth = newStroke

        val dif = radius/2
        val leftArrow = Point(leftCenter.x-dif,leftCenter.y)
        val rightArrow = Point(leftCenter.x+dif,leftCenter.y)
        val topArrow = Point(leftCenter.x,leftCenter.y-dif)
        val bottomArrow = Point(leftCenter.x,leftCenter.y+dif)

        val arrows = MyPath()
        arrows.move(leftArrow)
        arrows.line(rightArrow)
        arrows.move(topArrow)
        arrows.line(bottomArrow)
        canvas.drawPath(arrows,paint)

        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL

        val diff = dif * 3/4
        val leftButton = Point(rightCenter.x-diff,rightCenter.y)
        val rightButton = Point(rightCenter.x+diff,rightCenter.y)
        val topButton = Point(rightCenter.x,rightCenter.y-diff)
        val bottomButton = Point(rightCenter.x,rightCenter.y+diff)

        canvas.drawCircle(topButton.x.toFloat(), topButton.y.toFloat(),stroke,paint)
        canvas.drawCircle(bottomButton.x.toFloat(), bottomButton.y.toFloat(),stroke,paint)
        canvas.drawCircle(rightButton.x.toFloat(), rightButton.y.toFloat(),stroke,paint)
        canvas.drawCircle(leftButton.x.toFloat(), leftButton.y.toFloat(),stroke,paint)

        val left = bottomLeft.x+radius/4
        val right = bottomRight.x-radius/4
        val horizontal = leftCenter.y-radius/2

        canvas.drawCircle(left.toFloat(), horizontal.toFloat(),stroke,paint)
        canvas.drawCircle(right.toFloat(), horizontal.toFloat(),stroke,paint)

        val rect = Rect(bottomLeft.x,topLeft.y-radius/3,bottomRight.x,topLeft.y)

        canvas.drawRect(rect,paint)

        paint.strokeWidth = stroke
        paint.style = Paint.Style.STROKE

        val bottomWire = Point(bounds.centerX(),topLeft.y-radius/3)
        val topWire = Point(bottomWire.x,topLeft.y-radius)

        canvas.drawLine(bottomWire.x.toFloat(), bottomWire.y.toFloat(),
            topWire.x.toFloat(), topWire.y.toFloat(),paint)

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