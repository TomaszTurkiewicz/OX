package com.tt.ox.helpers

import android.content.Context
import com.tt.ox.database.Opponent

class Game(context: Context,private val opponent: Opponent) {

    private var mainPlayerName:String
    private var opponentPlayerName:String
    private var wins:Int
    private var loses:Int
    private var mainPlayerMark:Int
    private var mainPlayerMarkColor:Int
    private var opponentMark:Int
    private var opponentMarkColor:Int

    init {
        this.mainPlayerName = SharedPreferences.readPlayerName(context)
        this.opponentPlayerName = opponent.getName()
        this.wins = opponent.getWins()
        this.loses = opponent.getLoses()
        this.mainPlayerMark = opponent.getMainPlayerMark()
        this.mainPlayerMarkColor = opponent.getMainPlayerMarkColor()
        this.opponentMark = opponent.getOpponentMark()
        this.opponentMarkColor = opponent.getOpponentMarkColor()
    }

    fun addWin(){
        this.wins += 1
    }

    fun addLose(){
        this.loses += 1
    }

    fun getOpponent():Opponent{
        return Opponent(
            id = opponent.getId(),
            name = opponent.getName(),
            wins = wins,
            loses = loses,
            mainPlayerMark = mainPlayerMark,
            mainPlayerMarkColor = mainPlayerMarkColor,
            opponentMark = opponentMark,
            opponentMarkColor = opponentMarkColor
        )
    }

    fun getMainPlayerName():String{
        return this.mainPlayerName
    }

    fun getOpponentName():String{
        return this.opponentPlayerName
    }

    fun getWins():Int{
        return this.wins
    }

    fun getLoses():Int{
        return this.loses
    }

    fun switchMarks(){
        val tMark = this.opponentMark
        this.opponentMark = mainPlayerMark
        this.mainPlayerMark = tMark
    }

    fun getMainPlayerMark():Int{
        return this.mainPlayerMark
    }

    fun getOpponentMark():Int{
        return this.opponentMark
    }

    fun getMainPlayerMarkColor():Int{
        return this.mainPlayerMarkColor
    }

    fun getOpponentMarkColor():Int{
        return this.opponentMarkColor
    }

    fun setOpponentMarkColor(color:Int){
        this.opponentMarkColor = color
    }

    fun setPlayerMarkColor(color:Int){
        this.mainPlayerMarkColor = color
    }
}