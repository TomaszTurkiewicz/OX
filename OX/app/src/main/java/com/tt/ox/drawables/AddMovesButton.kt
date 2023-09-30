package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R

class AddMovesButton (private val context: Context) : Drawable(){
    private val paint = Paint()
    private val textBounds = Rect()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.isAntiAlias = true
        val radius = bounds.width()*0.1f
        val dif = bounds.width()*0.05f

        val rect = RectF(dif,dif,bounds.width()-dif,bounds.height()-dif)

        canvas.drawRoundRect(rect,radius,radius,paint)

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