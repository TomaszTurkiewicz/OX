package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R

class AddMovesButton (private val context: Context) : Drawable(){
    private val paint = Paint()
    private val textBounds = Rect()
    override fun draw(canvas: Canvas) {
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        val text = "+10"
        paint.color = ContextCompat.getColor(context,R.color.blue)
        paint.style = Paint.Style.FILL
        paint.textSize = bounds.width()*0.5f
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        paint.getTextBounds(text,0,text.length,textBounds)
        val newHeight = bounds.centerY()+textBounds.height()/2
        canvas.drawText(text, bounds.centerX().toFloat(), newHeight.toFloat(),paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}