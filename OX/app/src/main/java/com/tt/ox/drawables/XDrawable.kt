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
import com.tt.ox.helpers.COLOR_BLACK
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_GREEN
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.MyPath

class XDrawable  (private val context: Context, private val color:Int) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val dif = bounds.width()/6
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        when(color){
            COLOR_BLACK -> paint.color = ContextCompat.getColor(context, R.color.black)
            COLOR_RED -> paint.color = ContextCompat.getColor(context, R.color.red)
            COLOR_GREEN -> paint.color = ContextCompat.getColor(context, R.color.green)
            COLOR_BLUE -> paint.color = ContextCompat.getColor(context, R.color.blue)
        }
        paint.isAntiAlias = true

        val x = MyPath()
        x.move(Point(dif,dif))
        x.line(Point(bounds.width()-dif,bounds.height()-dif))
        x.move(Point(dif,bounds.height()-dif))
        x.line(Point(bounds.width()-dif,dif))

        canvas.drawPath(x,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}