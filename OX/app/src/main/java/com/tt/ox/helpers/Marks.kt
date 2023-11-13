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

    fun decreasePlayerColor(context: Context){
        if(playerColor==1){
            this.playerColor = MarkColors().getHighestColor()
        }else{
            this.playerColor-=1
        }
        SharedPreferences.saveMarks(context,this)
    }

    fun increasePlayerColor(context: Context){
        if(playerColor==MarkColors().getHighestColor()){
            this.playerColor = 1
        }else{
            this.playerColor+=1
        }
        SharedPreferences.saveMarks(context,this)
    }

    fun decreaseOpponentColor(context: Context){
        if(opponentColor==1){
            this.opponentColor = MarkColors().getHighestColor()
        }else{
            this.opponentColor-=1
        }
        SharedPreferences.saveMarks(context,this)
    }

    fun increaseOpponentColor(context: Context){
        if(opponentColor==MarkColors().getHighestColor()){
            this.opponentColor = 1
        }else{
            this.opponentColor+=1
        }
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