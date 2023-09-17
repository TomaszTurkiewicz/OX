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

class AddDrawable (private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val stroke = bounds.right*0.1f
        paint.strokeWidth = stroke
        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context, R.color.green)

        val dMax = 0.85f
        val dMin = 0.15f

        val bottom = Point(bounds.centerX(), (bounds.bottom*dMax).toInt())
        val top = Point(bounds.centerX(), (bounds.bottom*dMin).toInt())
        val left = Point((bounds.right*dMin).toInt(),bounds.centerY())
        val right = Point((bounds.right*dMax).toInt(),bounds.centerY())

        val path = MyPath()
        path.move(left)
        path.line(right)
        path.move(bottom)
        path.line(top)

        canvas.drawPath(path,paint)


    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}