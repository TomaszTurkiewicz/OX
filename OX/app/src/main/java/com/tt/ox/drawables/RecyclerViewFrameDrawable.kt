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

class RecyclerViewFrameDrawable (private val context: Context,private val frameWidth:Int) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (frameWidth*0.1).toFloat()
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        val radius = frameWidth.toFloat()
        val dif = frameWidth*0.5f

        val rect = RectF(dif,dif, bounds.width()-dif,bounds.height()-dif)

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