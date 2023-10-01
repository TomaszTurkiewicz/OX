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

class LeftArrowDrawable(private val context: Context, private val color:Int) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {


        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.isAntiAlias = true

        paint.style = Paint.Style.FILL_AND_STROKE
        when(color){
            COLOR_BLACK -> paint.color = ContextCompat.getColor(context, R.color.black)
            COLOR_RED -> paint.color = ContextCompat.getColor(context, R.color.red)
            COLOR_GREEN -> paint.color = ContextCompat.getColor(context, R.color.green)
            COLOR_BLUE -> paint.color = ContextCompat.getColor(context, R.color.blue)
        }

        val arrowPath = MyPath()

        val top = Point((bounds.width()*0.7).toInt(), (bounds.height()*0.3).toInt())
        val bottom = Point(top.x,bounds.height()-top.y)
        val left = Point((bounds.width()*0.3).toInt(),bounds.centerY())

        arrowPath.move(top)
        arrowPath.line(left)
        arrowPath.line(bottom)
        arrowPath.line(top)
        arrowPath.close()

        canvas.drawPath(arrowPath,paint)

    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}