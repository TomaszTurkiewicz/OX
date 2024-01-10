package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.COLOR_BLACK
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_GREEN
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme

class XDrawable  (private val context: Context, private val color:Int, private val thick:Boolean) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val dif = bounds.width()/6
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = if(thick) (bounds.width()*0.04).toFloat() else (bounds.width()*0.04).toFloat()
        when(color){
            COLOR_BLACK -> paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
            COLOR_RED -> paint.color = ContextCompat.getColor(context, Theme(context).getRedColor())
            COLOR_GREEN -> paint.color = ContextCompat.getColor(context, Theme(context).getGreenColor())
            COLOR_BLUE -> paint.color = ContextCompat.getColor(context, Theme(context).getBlueColor())
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