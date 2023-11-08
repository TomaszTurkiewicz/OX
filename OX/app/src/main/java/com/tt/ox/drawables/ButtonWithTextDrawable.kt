package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Theme

class ButtonWithTextDrawable (private val context: Context, private val text:String) : Drawable(){
    private val paint = Paint()
    private val textPaint = Paint()
    override fun draw(canvas: Canvas) {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.height()*0.02).toFloat()
        textPaint.textSize = bounds.height()*0.7f
        textPaint.isAntiAlias = true
        textPaint.color = ContextCompat.getColor(context,Theme(context).getAccentColor())
        textPaint.textAlign = Paint.Align.CENTER
        val textBounds = Rect()
        textPaint.getTextBounds(text,0,text.length,textBounds)
        val bottom = (textBounds.height()+bounds.height())/2
        canvas.drawText(text, bounds.centerX().toFloat(),bottom.toFloat(),textPaint)
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