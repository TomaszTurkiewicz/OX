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

class TurnDrawable(private val context: Context, private val center: Double) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val sizeH = bounds.height()*0.45f
        val sizeW = bounds.height()*0.55f
        paint.strokeWidth=bounds.height()*0.03f
        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context, R.color.blue_dark)
        paint.isAntiAlias = true
        val radius = bounds.height()*0.1f
        val middle = (bounds.width()*center).toFloat()
        val rect = RectF(middle-sizeW,bounds.centerY()-sizeH,middle+sizeW,bounds.centerY()+sizeH)
        canvas.drawRoundRect(rect,radius,radius,paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}