package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

class ButtonBackgroundWarning (private val context: Context) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val unit = ScreenMetricsCompat().getUnit(context)


        paint.strokeWidth = unit*0.05f
        paint.color = ContextCompat.getColor(context, Theme(context).getRedBackgroundColor())
        val radius = unit*0.2f
        val dif = unit*0.05f

        paint.style = Paint.Style.FILL
        val rect = RectF(dif,dif,bounds.width()-dif,bounds.height()-dif)

        canvas.drawRoundRect(rect,radius,radius,paint)

        paint.style = Paint.Style.STROKE
        paint.color = ContextCompat.getColor(context, Theme(context).getRedColor())
//        val radius = unit*0.2f
//        val dif = unit*0.05f
//
//        val rect = RectF(dif,dif,bounds.width()-dif,bounds.height()-dif)

        canvas.drawRoundRect(rect,radius,radius,paint)
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