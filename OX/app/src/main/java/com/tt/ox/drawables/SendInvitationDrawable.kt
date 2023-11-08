package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme

class SendInvitationDrawable (private val context: Context, private val enable:Boolean) : Drawable(){
    private val paint = Paint()
    private var color:Int = 0
    override fun draw(canvas: Canvas) {
        paint.strokeWidth =(bounds.width()*0.04).toFloat()

        color = if(enable) ContextCompat.getColor(context, Theme(context).getGreenColor()) else ContextCompat.getColor(context, Theme(context).getRedColor())
        paint.isAntiAlias = true

        makeLeftPerson(bounds.centerX().toDouble(),canvas)

        makePlusMark(canvas)



    }

    private fun makePlusMark(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.05).toFloat()
        paint.color = color

        val plusCenter = Point((bounds.width()*0.7).toInt(), (bounds.height()*0.3).toInt())
        val dif = bounds.width()*0.1
        val plus = MyPath()
        plus.move(Point((plusCenter.x-dif).toInt(), plusCenter.y))
        plus.line(Point((plusCenter.x+dif).toInt(), plusCenter.y))
        plus.move(Point(plusCenter.x, (plusCenter.y-dif).toInt()))
        plus.line(Point(plusCenter.x, (plusCenter.y+dif).toInt()))

        canvas.drawPath(plus,paint)

    }

    private fun makeLeftPerson(leftCenter:Double, canvas:Canvas){
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f
        paint.color = color
        val bodyDif = bounds.height()*0.15
        val difHorizontal = bounds.height()*0.05
        val difHorizontalAdd = bounds.height()*0.06
        val upperBody = RectF(
            (leftCenter-bodyDif).toFloat(),
            (bounds.centerY()-bodyDif+difHorizontal).toFloat(),
            (leftCenter+bodyDif).toFloat(),
            (bounds.centerY()+bodyDif+difHorizontal).toFloat()
        )
        val radius = bounds.height()*0.1f

        canvas.drawRoundRect(upperBody,radius,radius,paint)

        val loverBody = Rect(
            (leftCenter-bodyDif).toInt(),
            bounds.centerY(),
            (leftCenter+bodyDif).toInt(),
            (bounds.centerY()+bodyDif+difHorizontalAdd).toInt()
        )

        canvas.drawRect(loverBody,paint)

        paint.color = ContextCompat.getColor(context,Theme(context).getBackgroundColor())

        val down = Point(leftCenter.toInt(),bounds.centerY())
        val left = Point((leftCenter-bodyDif).toInt(), (bounds.height()*0.3).toInt())
        val right = Point((leftCenter+bodyDif).toInt(), (bounds.height()*0.3).toInt())


        val neck = MyPath()
        neck.move(down)
        neck.line(left)
        neck.line(right)
        neck.line(down)
        neck.close()

        canvas.drawPath(neck,paint)

        paint.color = color

        canvas.drawCircle(leftCenter.toFloat(),
            (bounds.height()*0.33).toFloat(), (bounds.height()*0.07).toFloat(),paint)

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