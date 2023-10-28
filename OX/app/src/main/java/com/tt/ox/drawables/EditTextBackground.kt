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
import com.tt.ox.helpers.ScreenMetricsCompat

class EditTextBackground (private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val unit = ScreenMetricsCompat().getUnit(context)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = unit*0.05f
        paint.color = ContextCompat.getColor(context, R.color.green)
        paint.isAntiAlias = true
        val radius = unit*0.2f
        val dif = unit*0.05f

        val rect = RectF(dif,dif,bounds.width()-dif,bounds.height()-dif)

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