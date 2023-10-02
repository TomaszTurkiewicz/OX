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
import com.tt.ox.R
import com.tt.ox.helpers.MyPath

class SinglePlayerButtonDrawable (private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val stroke = 0f
        paint.strokeWidth = stroke
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        val leftCenter = bounds.width()*0.3
        val rightCenter = bounds.width()-leftCenter

        makeLeftPerson(leftCenter,canvas)

        makeRightPhone(rightCenter,canvas)

        makeCenterArrows(canvas)

    }

    private fun makeCenterArrows(canvas: Canvas){
        val difHorizontal = bounds.height()*0.2
        val difVertical = bounds.height()*0.1

        paint.color = ContextCompat.getColor(context,R.color.green)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f

        val arrowDif = bounds.height()*0.1
        val left = Point((bounds.centerX()-difHorizontal).toInt(),bounds.centerY())
        val leftTop = Point((left.x+arrowDif).toInt(), (left.y-arrowDif).toInt())
        val leftBottom = Point((left.x+arrowDif).toInt(), (left.y+arrowDif).toInt())

        val leftArrow = MyPath()
        leftArrow.move(left)
        leftArrow.line(leftTop)
        leftArrow.line(leftBottom)
        leftArrow.line(left)
        leftArrow.close()

        canvas.drawPath(leftArrow,paint)

        val right = Point((bounds.centerX()+difHorizontal).toInt(),bounds.centerY())
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

    private fun makeLeftPerson(leftCenter:Double, canvas:Canvas){
        val dif = bounds.height()*0.2
        val rect = Rect((leftCenter-dif).toInt(),
            (bounds.centerY()-dif).toInt(), (leftCenter+dif).toInt(), (bounds.centerY()+dif).toInt()
        )
        canvas.drawRect(rect,paint)
    }

    private fun makeRightPhone(rightCenter:Double,canvas: Canvas){

        paint.style = Paint.Style.FILL
        paint.strokeWidth = 0f

        paint.color = ContextCompat.getColor(context,R.color.black)
        val difHorizontal = bounds.height()*0.15
        val difVertical = bounds.height()*0.2
        val radius = bounds.height()*0.04f
        val rectF = RectF((rightCenter-difHorizontal).toFloat(),
            (bounds.centerY()-difVertical).toFloat(), (rightCenter+difHorizontal).toFloat(), (bounds.centerY()+difVertical).toFloat()
        )

        canvas.drawRoundRect(rectF,radius,radius,paint)

        paint.color = ContextCompat.getColor(context,R.color.white)
        val screenWidth = bounds.height()*0.2f
        val screen = Rect((rightCenter-screenWidth/2).toInt(),
            (bounds.centerY()-bounds.height()*0.15).toInt(),
            (rightCenter+screenWidth/2).toInt(), (bounds.centerY()-bounds.height()*0.05).toInt()
        )

        canvas.drawRect(screen,paint)
        val difButton = bounds.height()*0.07
        makePhoneButton(canvas, rightCenter.toInt(),bounds.centerY())
        makePhoneButton(canvas, (rightCenter-difButton).toInt(),bounds.centerY())
        makePhoneButton(canvas, (rightCenter+difButton).toInt(),bounds.centerY())

        makePhoneButton(canvas, rightCenter.toInt(), (bounds.centerY()+difButton).toInt())
        makePhoneButton(canvas, (rightCenter-difButton).toInt(),(bounds.centerY()+difButton).toInt())
        makePhoneButton(canvas, (rightCenter+difButton).toInt(),(bounds.centerY()+difButton).toInt())

        makePhoneButton(canvas, rightCenter.toInt(), (bounds.centerY()+2*difButton).toInt())
        makePhoneButton(canvas, (rightCenter-difButton).toInt(),(bounds.centerY()+2*difButton).toInt())
        makePhoneButton(canvas, (rightCenter+difButton).toInt(),(bounds.centerY()+2*difButton).toInt())


    }

    private fun makePhoneButton(canvas:Canvas,positionX:Int,positionY:Int){
        val buttonSize = bounds.height()*0.05
        val button = Rect(
            (positionX - buttonSize/2).toInt(),
            (positionY - buttonSize/2).toInt(),
            (positionX + buttonSize/2).toInt(),
            (positionY + buttonSize/2).toInt(),
        )
        canvas.drawRect(button,paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}

