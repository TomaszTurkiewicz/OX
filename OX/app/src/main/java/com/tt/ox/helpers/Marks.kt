package com.tt.ox.helpers

import android.content.Context
import com.tt.ox.O
import com.tt.ox.X
import kotlin.random.Random

class Marks {
    var playerMark:Int = X
    var playerColor:Int = COLOR_BLACK
    var opponentMark:Int = O
    var opponentColor: Int = COLOR_BLACK


    fun initialize(context: Context){
        val random = SharedPreferences.readRandomMarks(context)
        if(random){
            generateRandomMarks()
        }
        else{
            readMarksFromSharedPreferences(context)
        }

    }

    fun swapMarks(context: Context){
        val t = this.playerMark
        this.playerMark = this.opponentMark
        this.opponentMark = t
        SharedPreferences.saveMarks(context,this)
    }

    private fun generateRandomMarks(){
        val random = Random.nextBoolean()
        if(random){
            this.playerMark = X
            this.opponentMark = O
        }else{
            this.playerMark = O
            this.opponentMark = X
        }

        this.playerColor = MarkColors().getRandomColor()
        this.opponentColor = MarkColors().getRandomColor()


    }

    private fun readMarksFromSharedPreferences(context: Context){
        val marks = SharedPreferences.readMarks(context)
        this.playerMark = marks.playerMark
        this.playerColor = marks.playerColor
        this.opponentMark = marks.opponentMark
        this.opponentColor = marks.opponentColor
    }
}