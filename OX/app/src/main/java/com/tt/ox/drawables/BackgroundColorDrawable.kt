package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Theme

class BackgroundColorDrawable (private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 0f
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, Theme(context).getBackgroundColor())


        val rect = RectF(0f,0f, bounds.width().toFloat(), bounds.height().toFloat())

        canvas.drawRect(rect,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}