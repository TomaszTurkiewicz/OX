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
import com.tt.ox.helpers.Functions
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class OnlinePlayerButtonDrawable (private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val stroke = 0f
        paint.strokeWidth = stroke
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        val leftCenter = bounds.width()*0.2
        val rightCenter = bounds.width()-leftCenter
        val leftArrow = bounds.width()*0.35
        val rightArrow = bounds.width()-leftArrow

        makePerson(leftCenter,canvas)

        makePerson(rightCenter,canvas)

        makeOnline(bounds.centerX().toDouble(),canvas)

        makeCenterArrows(canvas,leftArrow)
        makeCenterArrows(canvas,rightArrow)

    }

    private fun makeCenterArrows(canvas: Canvas, middle:Double){
        val difHorizontal = bounds.height()*0.2
        val difVertical = bounds.height()*0.1

        paint.color = ContextCompat.getColor(context, Theme(context).getGreenColor())
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f

        val arrowDif = bounds.height()*0.1
        val left = Point((middle-difHorizontal).toInt(),bounds.centerY())
        val leftTop = Point((left.x+arrowDif).toInt(), (left.y-arrowDif).toInt())
        val leftBottom = Point((left.x+arrowDif).toInt(), (left.y+arrowDif).toInt())

        val leftArrow = MyPath()
        leftArrow.move(left)
        leftArrow.line(leftTop)
        leftArrow.line(leftBottom)
        leftArrow.line(left)
        leftArrow.close()

        canvas.drawPath(leftArrow,paint)

        val right = Point((middle+difHorizontal).toInt(),bounds.centerY())
        val rightTop = Point((right.x-arrowDif).toInt(), (right.y-arrowDif).toInt())
        val rightBottom = Point((right.x-arrowDif).toInt(), (right.y+arrowDif).toInt())

        val rightArrow = MyPath()
        rightArrow.move(right)
        rightArrow.line(rightTop)
        rightArrow.line(rightBottom)
        rightArrow.line(right)
        rightArrow.close()

        canvas.drawPath(rightArrow,paint)

        paint.strokeWidth = bounds.height()*0.02f
        paint.style = Paint.Style.STROKE
        val topLeft = Point(leftTop.x, (bounds.centerY()-difVertical/3).toInt())
        val bottomLeft = Point(leftTop.x, (bounds.centerY()+difVertical/3).toInt())

        val topRight = Point(rightTop.x, (topLeft.y))
        val bottomRight = Point(rightTop.x, (bottomLeft.y))

        val lines = MyPath()
        lines.move(topLeft)
        lines.line(topRight)
        lines.move(bottomLeft)
        lines.line(bottomRight)
        canvas.drawPath(lines,paint)

    }

    private fun makePerson(leftCenter:Double, canvas: Canvas){
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f
        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())
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

        paint.color = ContextCompat.getColor(context, Theme(context).getBackgroundColor())

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

        paint.color = ContextCompat.getColor(context, Theme(context).getAccentColor())

        canvas.drawCircle(leftCenter.toFloat(),
            (bounds.height()*0.33).toFloat(), (bounds.height()*0.07).toFloat(),paint)

    }

    private fun makeOnline(rightCenter:Double,canvas: Canvas){

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = bounds.height()*0.02f
        val radius = bounds.height()*0.18f
        canvas.drawCircle(rightCenter.toFloat(), bounds.centerY().toFloat(),radius,paint)

        paint.color = ContextCompat.getColor(context,Theme(context).getAccentColor())
        val a = Point((rightCenter-radius).toInt(),bounds.centerY())
        val b = Point((rightCenter+radius).toInt(),bounds.centerY())
        val curvedAB1 = Functions.curvedPath(a,b,radius/2,false)
//        val curvedAB2 = Functions.curvedPath(a,b,radius,true)

        val c = Point(rightCenter.toInt(), (bounds.centerY()-radius).toInt())
        val d = Point(rightCenter.toInt(), (bounds.centerY()+radius).toInt())
        val curvedCD1 = Functions.curvedPath(c,d,radius/2,false)
        val curvedCD2 = Functions.curvedPath(c,d,radius/2,true)
        val curvedCD3 = Functions.curvedPath(c,d,radius*3/2,false)
        val curvedCD4 = Functions.curvedPath(c,d,radius*3/2,true)

        val left1 = Point((rightCenter - cos(PI/4) *radius).toInt(),
            (bounds.centerY() - sin(PI/4) *radius).toInt()
        )
        val right1 = Point((rightCenter + cos(PI/4) *radius).toInt(),
            (bounds.centerY() - sin(PI/4) *radius).toInt()
        )
        val curvedUp1 = Functions.curvedPath(left1,right1,radius/2,false)

        val left2 = Point((rightCenter - cos(PI/9) *radius).toInt(),
            (bounds.centerY() + sin(PI/9) *radius).toInt()
        )
        val right2 = Point((rightCenter + cos(PI/9) *radius).toInt(),
            (bounds.centerY() + sin(PI/9) *radius).toInt()
        )
        val curvedUp2 = Functions.curvedPath(left2,right2,radius/2,false)

        val left3 = Point((rightCenter - cos(PI/9) *radius).toInt(),
            (bounds.centerY() - sin(PI/9) *radius).toInt()
        )
        val right3 = Point((rightCenter + cos(PI/9) *radius).toInt(),
            (bounds.centerY() - sin(PI/9) *radius).toInt()
        )
        val curvedUp3 = Functions.curvedPath(left3,right3,radius/2,false)


        val path1 = MyPath()
        path1.move(a)
        path1.cubic(a,b,curvedAB1)
//        path1.move(a)
//        path1.cubic(a,b,curvedAB2)

        path1.move(c)
        path1.cubic(c,d,curvedCD1)
        path1.move(c)
        path1.cubic(c,d,curvedCD2)
        path1.move(c)
        path1.cubic(c,d,curvedCD3)
        path1.move(c)
        path1.cubic(c,d,curvedCD4)

        path1.move(left1)
        path1.cubic(left1,right1,curvedUp1)

        path1.move(left2)
        path1.cubic(left2,right2,curvedUp2)

        path1.move(left3)
        path1.cubic(left3,right3,curvedUp3)

        canvas.drawPath(path1,paint)

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