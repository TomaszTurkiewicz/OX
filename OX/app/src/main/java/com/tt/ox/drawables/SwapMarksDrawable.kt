package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.Functions
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme

class SwapMarksDrawable (private val context: Context, private val enable:Boolean) : Drawable(){
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        paint.color = if(enable) ContextCompat.getColor(context,Theme(context).getGreenColor()) else ContextCompat.getColor(context,Theme(context).getControlsDisableColor())
        val strokeWidth = bounds.width()*0.1f
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE

        val radius = bounds.width()*0.2f
        val leftUp = Point((bounds.width()*0.2).toInt(), (bounds.height()*0.4).toInt())
        val rightUp = Point(bounds.width()-leftUp.x,leftUp.y)
        val arrowCurvedUp = Functions.curvedPath(leftUp,rightUp,radius,true)

        val arrowUp = MyPath()
        arrowUp.move(leftUp)
        arrowUp.cubic(leftUp,rightUp,arrowCurvedUp)

        canvas.drawPath(arrowUp,paint)


        val leftDown = Point((bounds.width()*0.2).toInt(), (bounds.height()*0.6).toInt())
        val rightDown = Point(bounds.width()-leftDown.x,leftDown.y)
        val arrowCurvedDown = Functions.curvedPath(leftDown,rightDown,radius,false)

        val arrowDown = MyPath()
        arrowDown.move(leftDown)
        arrowDown.cubic(leftDown,rightDown,arrowCurvedDown)

        canvas.drawPath(arrowDown,paint)



        val newStrokeWidth = bounds.width()*0.1f
        paint.strokeWidth = newStrokeWidth
        paint.style = Paint.Style.FILL_AND_STROKE

        val leftFirst = Point((leftUp.x+bounds.width()*0.1).toInt(), (leftUp.y-bounds.height()*0.1).toInt())
        val leftSecond = Point((leftUp.x+bounds.width()*0.15).toInt(), (leftUp.y))

        val leftArrow = MyPath()
        leftArrow.move(leftUp)
        leftArrow.line(leftFirst)
        leftArrow.line(leftSecond)
        leftArrow.line(leftUp)
        leftArrow.close()

        canvas.drawPath(leftArrow,paint)

        val rightFirst = Point(bounds.width()-leftFirst.x,bounds.height()-leftFirst.y)
        val rightSecond = Point(bounds.width()-leftSecond.x,bounds.height()-leftSecond.y)

        val rightArrow = MyPath()
        rightArrow.move(rightDown)
        rightArrow.line(rightFirst)
        rightArrow.line(rightSecond)
        rightArrow.line(rightDown)
        rightArrow.close()

        canvas.drawPath(rightArrow,paint)

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