package com.tt.ox.helpers

import android.content.Context
import com.tt.ox.database.Opponent

class Game(context: Context,private val opponent: Opponent) {

    private var mainPlayerName:String
    private var opponentPlayerName:String
    private var wins:Int
    private var loses:Int
    private var mainPlayerMark:Int
    private var opponentMark:Int

    init {
        this.mainPlayerName = SharedPreferences.readPlayerName(context)
        this.opponentPlayerName = opponent.getName()
        this.wins = opponent.getWins()
        this.loses = opponent.getLoses()
        this.mainPlayerMark = opponent.getMainPlayerMark()
        this.opponentMark = opponent.getOpponentMark()
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
            opponentMark = opponentMark
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
}