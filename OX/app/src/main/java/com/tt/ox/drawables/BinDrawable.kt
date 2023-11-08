package com.tt.ox.drawables

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tt.ox.helpers.MyPath
import com.tt.ox.helpers.Theme

class BinDrawable (private val context: Context,private val deletable:Boolean): Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {

        paint.color = if(deletable) ContextCompat.getColor(context, Theme(context).getRedColor()) else ContextCompat.getColor(context, Theme(context).getAccentColor())
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        val radius = bounds.right*0.1f
        val roundedRect = RectF(bounds.right*0.25f,bounds.bottom*0.3f,bounds.right*0.75f,bounds.bottom*0.85f)
        canvas.drawRoundRect(roundedRect,radius,radius,paint)
        val rect = RectF(bounds.right*0.25f,bounds.bottom*0.3f,bounds.right*0.75f,bounds.bottom*0.5f)
        canvas.drawRect(rect,paint)

        val left = bounds.right*0.2f
        val bottom = bounds.bottom*0.25f
        val thick = bounds.bottom*0.1f

        val dekiel = RectF(left,bottom-thick,bounds.right-left,bottom)
        canvas.drawRect(dekiel,paint)

        val difUpX = bounds.right*0.4f
        val difUpY = bounds.bottom*0.1f
        val jump = bounds.right*0.1f

        val path = MyPath()
        path.move(Point(difUpX.toInt(), difUpY.toInt()))
        path.line(Point((bounds.right-difUpX).toInt(), difUpY.toInt()))
        path.line(Point((bounds.right-difUpX+jump).toInt(), (difUpY+jump).toInt()))
        path.line(Point((difUpX-jump).toInt(), (difUpY+jump).toInt()))
        path.close()

        canvas.drawPath(path,paint)

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