package com.tt.ox.helpers

import android.graphics.Path
import android.graphics.Point

class MyPath : Path() {
    fun line(a: Point){
        lineTo(a.x.toFloat(), a.y.toFloat())
    }

    fun move(a: Point){
        moveTo(a.x.toFloat(), a.y.toFloat())
    }

    fun cubic(a:Point,b:Point,radius:CurvedPoint){
        cubicTo(a.x.toFloat(), a.y.toFloat(),radius.x,radius.y, b.x.toFloat(), b.y.toFloat())
    }
}