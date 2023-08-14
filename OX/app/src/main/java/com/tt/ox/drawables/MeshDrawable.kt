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

class MeshDrawable(private val context: Context) : Drawable() {
    private val paint = Paint()
    override fun draw(canvas: Canvas) {
        val x = bounds.width()/3
        val y = bounds.height()/3
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = (bounds.width()*0.02).toFloat()
        paint.color = ContextCompat.getColor(context, R.color.black)
        paint.isAntiAlias = true
        val mesh = MyPath()
        mesh.move(Point(x,0))
        mesh.line(Point(x,bounds.height()))
        mesh.move(Point(2*x,0))
        mesh.line(Point(2*x,bounds.height()))
        mesh.move(Point(0,y))
        mesh.line(Point(bounds.width(),y))
        mesh.move(Point(0,2*y))
        mesh.line(Point(bounds.width(),2*y))
        canvas.drawPath(mesh,paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha=alpha
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
}