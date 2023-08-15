package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.helpers.MyPath

class WinLineDrawable (private val context: Context,
                       private val horizontalTop:Boolean,
                       private val horizontalMid:Boolean,
                       private val horizontalBottom:Boolean,
                       private val verticalLeft:Boolean,
                       private val verticalMid:Boolean,
                       private val verticalRight:Boolean,
                       private val angleUp:Boolean,
                       private val angleDown:Boolean) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.red)
        paint.isAntiAlias = true
        val fieldSize = bounds.width()/3
        val line = MyPath()
        if(horizontalTop){
            line.move(Point(0,fieldSize/2))
            line.line(Point(bounds.width(),fieldSize/2))
        }
        if(horizontalMid){
            line.move(Point(0,fieldSize+fieldSize/2))
            line.line(Point(bounds.width(),fieldSize+fieldSize/2))
        }
        if(horizontalBottom){
            line.move(Point(0,2*fieldSize+fieldSize/2))
            line.line(Point(bounds.width(),2*fieldSize+fieldSize/2))
        }
        if(verticalLeft){
            line.move(Point(fieldSize/2,0))
            line.line(Point(fieldSize/2,bounds.height()))
        }
        if(verticalMid){
            line.move(Point(fieldSize+fieldSize/2,0))
            line.line(Point(fieldSize+fieldSize/2,bounds.height()))
        }
        if(verticalRight){
            line.move(Point(2*fieldSize+fieldSize/2,0))
            line.line(Point(2*fieldSize+fieldSize/2,bounds.height()))
        }
        if(angleUp){
            line.move(Point(0,bounds.height()))
            line.line(Point(bounds.width(),0))
        }
        if(angleDown){
            line.move(Point(0,0))
            line.line(Point(bounds.width(),bounds.height()))
        }

        canvas.drawPath(line,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}