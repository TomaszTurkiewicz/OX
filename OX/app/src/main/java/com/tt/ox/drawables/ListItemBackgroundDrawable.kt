package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R

class ListItemBackgroundDrawable (private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.height()*0.01).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.isAntiAlias = true

        val margin = bounds.height()*0.1f
        val radius = margin*2

        val rect = RectF(margin,margin,bounds.width()-margin,bounds.height()-margin)

        canvas.drawRoundRect(rect,radius,radius,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}