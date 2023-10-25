package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.helpers.MyPath

class LogoutDrawable (private val context: Context, val active:Boolean) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val stroke = bounds.width()*0.1f
        paint.strokeWidth = stroke
        paint.style = Paint.Style.STROKE
        paint.color = if(active) ContextCompat.getColor(context, R.color.red) else ContextCompat.getColor(context, R.color.gray)
        paint.isAntiAlias = true

        val radius = bounds.width()*0.1f
        val horizontalLeft = bounds.width()*0.4f
        val horizontalRight = bounds.width()*0.15f
        val vertical = bounds.width()*0.15f
        val rect = RectF(horizontalLeft,vertical,bounds.width()-horizontalRight,bounds.height()-vertical)
        canvas.drawRoundRect(rect,radius,radius,paint)

        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.white)
        val blank = MyPath()
        val w = Point((bounds.width()*0.3).toInt(), (bounds.height()*0.4).toInt())
        val x = Point((bounds.width()*0.5).toInt(), (bounds.height()*0.4).toInt())
        val y = Point((bounds.width()*0.5).toInt(), (bounds.height()*0.6).toInt())
        val z = Point((bounds.width()*0.3).toInt(), (bounds.height()*0.6).toInt())

        blank.move(w)
        blank.line(x)
        blank.line(y)
        blank.line(z)
        blank.line(w)
        blank.close()
        canvas.drawPath(blank,paint)


        paint.color = if(active) ContextCompat.getColor(context, R.color.red) else ContextCompat.getColor(context, R.color.gray)


        val rad = radius/2
        val cornerEffects = CornerPathEffect(rad)
        paint.pathEffect = cornerEffects

        val arrow = MyPath()
        val a = Point((bounds.width()*0.1).toInt(),bounds.centerY())
        val b = Point((bounds.width()*0.3).toInt(), (bounds.height()*0.3).toInt())
        val c = Point((bounds.width()*0.3).toInt(), (bounds.height()*0.7).toInt())

        arrow.move(a)
        arrow.line(b)
        arrow.line(c)
        arrow.line(a)
        arrow.close()

        canvas.drawPath(arrow,paint)

        val line = MyPath()
        val d = Point((bounds.width()*0.2).toInt(), (bounds.height()*0.45).toInt())
        val e = Point((bounds.width()*0.6).toInt(), (bounds.height()*0.45).toInt())
        val f = Point((bounds.width()*0.6).toInt(), (bounds.height()*0.55).toInt())
        val g = Point((bounds.width()*0.2).toInt(), (bounds.height()*0.55).toInt())

        line.move(d)
        line.line(e)
        line.line(f)
        line.line(g)
        line.line(d)
        line.close()

        canvas.drawPath(line,paint)

    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}