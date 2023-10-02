package com.tt.ox.helpers

import android.graphics.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Functions {
    companion object{
        fun curvedPath(a: Point, b: Point, radius: Double, up:Boolean):CurvedPoint{
            val midX = a.x + ((b.x-a.x)/2)
            val midY = a.y + ((b.y-a.y)/2)
            val xDiff: Double = (midX - a.x).toDouble()
            val yDiff: Double = (midY - a.y).toDouble()
            val angle = (atan2(yDiff,xDiff) *(180/Math.PI))-90
            val angleRadius = Math.toRadians(angle)
            val curvedPoint = CurvedPoint()
            if(up) {
                curvedPoint.x = (midX + radius * cos(angleRadius)).toFloat()
                curvedPoint.y = (midY + radius * sin(angleRadius)).toFloat()
            }else{
                curvedPoint.x = (midX - radius * cos(angleRadius)).toFloat()
                curvedPoint.y = (midY - radius * sin(angleRadius)).toFloat()
            }
            return curvedPoint
        }

        fun orthogonalPoint(centre: Point,radius:Double,angle:Double):Point{
            val y = sin(angle)*radius
            val x = cos(angle)*radius
            return Point((centre.x+x).toInt(), (centre.y-y).toInt())
        }
    }


}