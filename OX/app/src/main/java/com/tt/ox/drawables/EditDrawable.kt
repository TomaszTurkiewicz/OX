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

class EditDrawable (private val context: Context): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        paint.strokeWidth = bounds.right*0.15f
        paint.style = Paint.Style.STROKE
        val difH = bounds.width()*0.2
        val difV = bounds.height()*0.2

        val line = MyPath()
        line.move(Point((bounds.right-difH).toInt(), difV.toInt()))
        line.line(Point(difH.toInt(), (bounds.bottom-difV).toInt()))

        canvas.drawPath(line,paint)

        paint.color = ContextCompat.getColor(context,Theme(context).getBackgroundColor())
        paint.strokeWidth = bounds.right*0.10f

        val cutBottom = MyPath()
        cutBottom.move(Point(difH.toInt(),bounds.centerY()))
        cutBottom.line(Point(difH.toInt(), (bounds.bottom-difV).toInt()))
        cutBottom.line(Point(bounds.centerX(), (bounds.bottom-difV).toInt()))
        canvas.drawPath(cutBottom,paint)

        paint.strokeWidth = bounds.right*0.05f
        val dif = bounds.right*0.25
        val cutUpper = MyPath()
        cutUpper.move(Point((bounds.right-dif-difH).toInt(), (bounds.top+difV).toInt()))
        cutUpper.line(Point(((bounds.right-difH).toInt()), (bounds.top+dif+difV).toInt()))

        canvas.drawPath(cutUpper,paint)

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