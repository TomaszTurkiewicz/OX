package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R

class ActiveCircleDrawable (private val context: Context, private val active:Boolean) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.strokeWidth=0f
        paint.style = Paint.Style.FILL
        paint.color = if(active)ContextCompat.getColor(context, R.color.green)else ContextCompat.getColor(context,R.color.gray)

        canvas.drawCircle(bounds.centerX().toFloat(),
            bounds.centerY().toFloat(), (bounds.width()/2).toFloat(),paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}