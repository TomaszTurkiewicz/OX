package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.helpers.COLOR_BLACK
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_GREEN
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.MyPath

class RightArrowDrawable (private val context: Context, private val color:Int) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.isAntiAlias = true

        val radius = bounds.width()*0.1f
        val dif = bounds.width()*0.05f

        val rect = RectF(dif,dif,bounds.width()-dif,bounds.height()-dif)

        canvas.drawRoundRect(rect,radius,radius,paint)



        paint.style = Paint.Style.FILL_AND_STROKE

        when(color){
            COLOR_BLACK -> paint.color = ContextCompat.getColor(context, R.color.black)
            COLOR_RED -> paint.color = ContextCompat.getColor(context, R.color.red)
            COLOR_GREEN -> paint.color = ContextCompat.getColor(context, R.color.green)
            COLOR_BLUE -> paint.color = ContextCompat.getColor(context, R.color.blue)
        }
        val arrowPath = MyPath()

        val top = Point((bounds.width()*0.3).toInt(), (bounds.height()*0.3).toInt())
        val bottom = Point(top.x,bounds.height()-top.y)
        val right = Point((bounds.width()*0.7).toInt(),bounds.centerY())


        arrowPath.move(top)
        arrowPath.line(right)
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