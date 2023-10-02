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
import com.tt.ox.helpers.Functions
import com.tt.ox.helpers.MyPath
import java.lang.Math.PI

class SettingButtonDrawable (private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val stroke = 0f
        paint.strokeWidth = stroke
        paint.style = Paint.Style.FILL

        paint.color = ContextCompat.getColor(context, R.color.black)

        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            bounds.width()*0.3f,
            paint
        )

        paint.color = ContextCompat.getColor(context,R.color.white)
        val middle = Point(bounds.centerX(),bounds.centerY())
        val radiusSmall = bounds.width()*0.15
        val radiusBig = bounds.width()*0.4
        var angle = 0.0

        makeCut(middle,radiusSmall,radiusBig,angle,canvas)

        angle += PI/3
        makeCut(middle,radiusSmall,radiusBig,angle,canvas)
        angle += PI/3
        makeCut(middle,radiusSmall,radiusBig,angle,canvas)
        angle += PI/3
        makeCut(middle,radiusSmall,radiusBig,angle,canvas)
        angle += PI/3
        makeCut(middle,radiusSmall,radiusBig,angle,canvas)
        angle += PI/3
        makeCut(middle,radiusSmall,radiusBig,angle,canvas)

        paint.color = ContextCompat.getColor(context, R.color.black)

        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            bounds.width()*0.2f,
            paint
        )

        paint.color = ContextCompat.getColor(context,R.color.white)

        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            bounds.width()*0.1f,
            paint
        )
    }

    private fun makeCut(middle:Point, radiusSmall:Double, radiusBig:Double, angle:Double, canvas:Canvas){
        val dif = PI/8
        var angleDec = angle - dif
        var angleInc = angle + dif

        val a = Functions.orthogonalPoint(middle,radiusSmall,angle)
        val b = Functions.orthogonalPoint(middle,radiusBig,angleDec)
        val c = Functions.orthogonalPoint(middle,radiusBig,angleInc)

        val path1 = MyPath()
        path1.move(a)
        path1.line(b)
        path1.line(c)
        path1.line(a)
        path1.close()

        canvas.drawPath(path1,paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}