package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.Theme

class ProgressBarDrawable (private val context: Context, private val percent:Double, private val color:Int): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {

        paint.color = when(color){
            COLOR_RED -> ContextCompat.getColor(context, Theme(context).getRedColor())
            COLOR_BLUE -> ContextCompat.getColor(context, Theme(context).getBlueColor())
            else -> ContextCompat.getColor(context, Theme(context).getGreenColor())
        }

        paint.strokeWidth = bounds.width()*0.05f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias=true
        val radius = bounds.width()*0.45

        val circle = RectF(
            (bounds.centerX()-radius).toFloat(),
            (bounds.centerY()-radius).toFloat(),
            (bounds.centerX()+radius).toFloat(),
            (bounds.centerY()+radius).toFloat()
        )

        val progress = percent*360f
        canvas.drawArc(circle,270f, progress.toFloat(),false,paint)

    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}