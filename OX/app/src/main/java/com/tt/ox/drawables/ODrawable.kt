package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.helpers.COLOR_BLACK
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_GREEN
import com.tt.ox.helpers.COLOR_RED

class ODrawable (private val context: Context, private val color:Int) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val middleX = bounds.centerX()
        val middleY = bounds.centerY()
        val radius = bounds.width()/3
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        when(color){
            COLOR_BLACK -> paint.color = ContextCompat.getColor(context, R.color.black)
            COLOR_RED -> paint.color = ContextCompat.getColor(context, R.color.red)
            COLOR_GREEN -> paint.color = ContextCompat.getColor(context, R.color.green)
            COLOR_BLUE -> paint.color = ContextCompat.getColor(context, R.color.blue)
        }

        paint.isAntiAlias = true

        canvas.drawCircle(middleX.toFloat(), middleY.toFloat(), radius.toFloat(),paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}